package com.translator.translator.service;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

@Service
public class RequestCounter {
    private final AtomicInteger totalRequests = new AtomicInteger(0);
    private final AtomicInteger successfulRequests = new AtomicInteger(0);
    private final AtomicInteger failedRequests = new AtomicInteger(0);

    public void incrementTotal() {
        totalRequests.incrementAndGet();
    }

    public void incrementSuccessful() {
        successfulRequests.incrementAndGet();
    }

    public void incrementFailed() {
        failedRequests.incrementAndGet();
    }

    public int getTotalRequests() {
        return totalRequests.get();
    }

    public int getSuccessfulRequests() {
        return successfulRequests.get();
    }

    public int getFailedRequests() {
        return failedRequests.get();
    }

    public void reset() {
        totalRequests.set(0);
        successfulRequests.set(0);
        failedRequests.set(0);
    }
}