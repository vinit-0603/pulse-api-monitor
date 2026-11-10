package com.pulse.model;

import java.time.LocalDateTime;

public class Incident {
    private int id;
    private int endpointId;
    private LocalDateTime startedAt;
    private LocalDateTime recoveredAt;
    private String failureReason;
    private long downtimeSeconds;
    private String status; // ONGOING, RESOLVED

    public Incident(int id, int endpointId, LocalDateTime startedAt, LocalDateTime recoveredAt, 
                    String failureReason, long downtimeSeconds, String status) {
        this.id = id;
        this.endpointId = endpointId;
        this.startedAt = startedAt;
        this.recoveredAt = recoveredAt;
        this.failureReason = failureReason;
        this.downtimeSeconds = downtimeSeconds;
        this.status = status;
    }

    // Getters
    public int getId() { return id; }
    public int getEndpointId() { return endpointId; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getRecoveredAt() { return recoveredAt; }
    public String getFailureReason() { return failureReason; }
    public long getDowntimeSeconds() { return downtimeSeconds; }
    public String getStatus() { return status; }
}