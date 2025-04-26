package com.translator.translator.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicInteger; // Thread-safe

@Service
public class RequestCounter {
    private final AtomicInteger totalRequests = new AtomicInteger(0);
    private final AtomicInteger successfulRequests = new AtomicInteger(0);

    public void incrementTotal() {
        totalRequests.incrementAndGet();
    }

    public void incrementSuccessful() {
        successfulRequests.incrementAndGet();
    }

    public int getTotalRequests() {
        return totalRequests.get();
    }

    public int getSuccessfulRequests() {
        return successfulRequests.get();
    }
}
