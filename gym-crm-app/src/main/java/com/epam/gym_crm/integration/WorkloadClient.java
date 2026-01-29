package com.epam.gym_crm.integration;

import com.epam.gym_crm.integration.security.IntegrationJwtService;
import com.epam.gym_crm.workload.contract.TrainerWorkloadRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WorkloadClient {
    private static final Logger log = LoggerFactory.getLogger(WorkloadClient.class);

    private static final String URL = "http://workload-service/workload";

    private final RestTemplate restTemplate;
    private final IntegrationJwtService integrationJwtService;

    public WorkloadClient(RestTemplate restTemplate, IntegrationJwtService integrationJwtService) {
        this.restTemplate = restTemplate;
        this.integrationJwtService = integrationJwtService;
    }

    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallback")
    public void send(TrainerWorkloadRequest req) {
        String token = integrationJwtService.generateToken("gym-crm");
        log.info("INTEGRATION JWT: {}", token);//for demonstration!

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        String txId = MDC.get("transactionId");
        if (txId != null && !txId.isBlank()) {
            headers.set("X-Transaction-Id", txId);
        }

        HttpEntity<TrainerWorkloadRequest> entity = new HttpEntity<>(req, headers);

        restTemplate.postForEntity(URL, entity, Void.class);
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
