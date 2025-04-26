package com.translator.translator.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.translator.translator.service.RequestCounter;

@Aspect
@Component
public class RequestCounterAspect {
    private final RequestCounter requestCounter;

    public RequestCounterAspect(RequestCounter requestCounter) {
        this.requestCounter = requestCounter;
    }

    // Track ALL requests (even failed ones)
    @Before("within(@org.springframework.stereotype.Controller *) || " +
            "within(@org.springframework.web.bind.annotation.RestController *)")
    public void countAllRequests() {
        requestCounter.incrementTotal();
    }

    // Track SUCCESSFUL requests (no exceptions thrown)
    @AfterReturning(
        pointcut = "within(@org.springframework.stereotype.Controller *) || " +
                  "within(@org.springframework.web.bind.annotation.RestController *)"
    )
    public void countSuccessfulRequests(Object result) {
        requestCounter.incrementSuccessful();
    }
}
