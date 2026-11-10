package com.pulse.model;

import java.time.LocalDateTime;

public class Endpoint {
    private int id;
    private String name;
    private String url;
    private String method; // "GET" or "HEAD"
    private int intervalSeconds;
    private int timeoutSeconds;
    private String expectedStatusCodes; // e.g., "200,201"
    private boolean active;
    private LocalDateTime createdAt;

    public Endpoint(int id, String name, String url, String method, int intervalSeconds, 
                    int timeoutSeconds, String expectedStatusCodes, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.method = method;
        this.intervalSeconds = intervalSeconds;
        this.timeoutSeconds = timeoutSeconds;
        this.expectedStatusCodes = expectedStatusCodes;
        this.active = active;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public int getIntervalSeconds() { return intervalSeconds; }
    public void setIntervalSeconds(int intervalSeconds) { this.intervalSeconds = intervalSeconds; }

    public int getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }

    public String getExpectedStatusCodes() { return expectedStatusCodes; }
    public void setExpectedStatusCodes(String expectedStatusCodes) { this.expectedStatusCodes = expectedStatusCodes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}