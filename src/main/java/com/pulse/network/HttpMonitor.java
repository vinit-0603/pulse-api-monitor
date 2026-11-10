package com.pulse.network;


import com.pulse.model.Endpoint;
import com.pulse.model.HistoryRecord;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

public class HttpMonitor {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public static HistoryRecord checkEndpoint(Endpoint endpoint) {
    long startTime = System.currentTimeMillis();
    try {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(endpoint.getUrl()))
                .timeout(Duration.ofSeconds(endpoint.getTimeoutSeconds()));

        if ("HEAD".equalsIgnoreCase(endpoint.getMethod())) {
            requestBuilder.method("HEAD", HttpRequest.BodyPublishers.noBody());
        } else {
            requestBuilder.GET();
        }

        HttpResponse<Void> response = httpClient.send(requestBuilder.build(),
                HttpResponse.BodyHandlers.discarding());

        long duration = System.currentTimeMillis() - startTime;
        int statusCode = response.statusCode();
        boolean isSuccess = isStatusCodeExpected(statusCode, endpoint.getExpectedStatusCodes());

        String status = isSuccess ? "SUCCESS" : "FAILED";
        String errorMsg = isSuccess ? null
                : "HTTP Status " + statusCode + " does not match expected: ("
                  + endpoint.getExpectedStatusCodes() + ")";

        return new HistoryRecord(0, endpoint.getId(), LocalDateTime.now(),
                status, statusCode, duration, errorMsg);

    } catch (Exception e) {
        long duration = System.currentTimeMillis() - startTime;
        String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();

        return new HistoryRecord(0, endpoint.getId(), LocalDateTime.now(),
                "FAILED", 0, duration, errorMsg);
    }
}

    private static boolean isStatusCodeExpected(int actualCode, String expectedCodesStr) {
        if (expectedCodesStr == null || expectedCodesStr.trim().isEmpty()) {
            return actualCode == 200;
        }

        String[] expectedArray = expectedCodesStr.split(",");
        return Arrays.stream(expectedArray)
                .map(String::trim)
                .anyMatch(code -> code.equals(String.valueOf(actualCode)));
    }
}