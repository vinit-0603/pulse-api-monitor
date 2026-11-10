package com.pulse.model;

import java.time.LocalDateTime;

public class HistoryRecord {
    private int id;
    private int endpointId;
    private LocalDateTime checkedAt;
    private String status;
    private int httpStatusCode;
    private long responseTimeMs;
    private String errorMessage;

    public HistoryRecord(int id, int endpointId, LocalDateTime checkedAt, String status, int httpStatusCode, long responseTimeMs, String errorMessage) {
        this.id = id;
        this.endpointId = endpointId;
        this.checkedAt = checkedAt;
        this.status = status;
        this.httpStatusCode = httpStatusCode;
        this.responseTimeMs = responseTimeMs;
        this.errorMessage = errorMessage;
    }

    public int getId() { return id; }
    public int getEndpointId() { return endpointId; }
    public LocalDateTime getCheckedAt() { return checkedAt; }
    public String getStatus() { return status; }
    public int getHttpStatusCode() { return httpStatusCode; }
    public long getResponseTimeMs() { return responseTimeMs; }
    public String getErrorMessage() { return errorMessage; }
}