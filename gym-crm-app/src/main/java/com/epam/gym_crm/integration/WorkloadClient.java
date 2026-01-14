package com.epam.gym_crm.integration;

import com.epam.gym_crm.workload.contract.TrainerWorkloadRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WorkloadClient {
    private final RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(WorkloadClient.class);

    public WorkloadClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallback")
    public void send(TrainerWorkloadRequest req) {
        restTemplate.postForEntity("http://workload-service/workload", req, Void.class);
    }

    private void fallback(TrainerWorkloadRequest req, Throwable ex) {
        log.warn("WorkloadService unavailable. Skip sending {} for trainer={}, date={}, minutes={}. Reason: {}",
                req != null ? req.getActionType() : null,
                req != null ? req.getTrainerUsername() : null,
                req != null ? req.getTrainingDate() : null,
                req != null ? req.getTrainingDurationMinutes() : null,
                ex.toString());
    }
}
