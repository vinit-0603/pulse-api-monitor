package com.pulse.service;

import com.pulse.database.EndpointDao;
import com.pulse.model.Endpoint;
import com.pulse.model.HistoryRecord;
import com.pulse.network.HttpMonitor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MonitoringService {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    // One scheduled task per endpoint ID, so we can cancel or replace it later
    private static final Map<Integer, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

    private static boolean isRunning = false;

    public static synchronized void startMonitoring() {
        if (isRunning) return;
        isRunning = true;

        List<Endpoint> endpoints = EndpointDao.getAllEndpoints();
        for (Endpoint endpoint : endpoints) {
            scheduleEndpoint(endpoint); // skips inactive endpoints by itself
        }
        System.out.println("Background Monitoring Engine started.");
    }

    /**
     * Schedules (or reschedules) monitoring for an endpoint.
     * Any existing task for the same ID is cancelled first.
     * If the endpoint is paused, the old task is cancelled and nothing new is scheduled.
     */
    public static synchronized void scheduleEndpoint(Endpoint endpoint) {
        cancelEndpoint(endpoint.getId());

        if (!endpoint.isActive() || scheduler.isShutdown()) {
            return;
        }

        Runnable checkTask = () -> {
            try {
                HistoryRecord record = HttpMonitor.checkEndpoint(endpoint);
                EndpointDao.saveHistoryRecord(record);
                System.out.println("Checked [" + endpoint.getName() + "] -> Status: "
                        + record.getStatus() + " (" + record.getResponseTimeMs() + "ms)");
            } catch (Exception e) {
                System.err.println("Failed check task for endpoint: " + endpoint.getName());
            }
        };

        long interval = Math.max(5, endpoint.getIntervalSeconds());
        ScheduledFuture<?> future =
                scheduler.scheduleWithFixedDelay(checkTask, 0, interval, TimeUnit.SECONDS);
        tasks.put(endpoint.getId(), future);
    }

    /** Stops monitoring a single endpoint (used for pause and delete). */
    public static synchronized void cancelEndpoint(int endpointId) {
        ScheduledFuture<?> old = tasks.remove(endpointId);
        if (old != null) {
            old.cancel(false);
        }
    }

    public static synchronized void stopMonitoring() {
        if (!isRunning) return;
        tasks.values().forEach(f -> f.cancel(false));
        tasks.clear();
        scheduler.shutdownNow();
        isRunning = false;
        System.out.println("Background Monitoring Engine stopped.");
    }
}