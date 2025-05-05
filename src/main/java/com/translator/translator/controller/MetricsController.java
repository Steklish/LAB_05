package com.translator.translator.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.translator.translator.service.RequestCounter;

@RestController
@RequestMapping("/stats")
public class MetricsController {
    @Autowired
    private final RequestCounter requestCounter;

    public MetricsController(RequestCounter requestCounter) {
        this.requestCounter = requestCounter;
    }

    @GetMapping("/requests")
    public Map<String, Integer> getMetrics() {
        Map<String, Integer> metrics = new HashMap<>();
        metrics.put("totalRequests", requestCounter.getTotalRequests());
        metrics.put("successfulRequests", requestCounter.getSuccessfulRequests());
        metrics.put("failedRequests", requestCounter.getFailedRequests());
        return metrics;
    }
}
