package com.epam.gym_crm.workload.web.controller;

import com.epam.gym_crm.workload.contract.TrainerWorkloadRequest;
import com.epam.gym_crm.workload.web.dto.MonthlyWorkloadResponse;
import com.epam.gym_crm.workload.service.WorkloadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workload")
public class WorkloadController {
    private final WorkloadService service;

    public WorkloadController(WorkloadService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> apply(@RequestBody TrainerWorkloadRequest req) {

        if (req == null
            || isBlank(req.getTrainerUsername())
            || isBlank(req.getTrainerFirstName())
            || isBlank(req.getTrainerLastName())
            || req.getTrainingDate() == null
            || req.getTrainingDurationMinutes() <= 0
            || req.getActionType() == null) {
            return ResponseEntity.badRequest().body("Invalid request payload");
        }

        service.apply(req);
        return ResponseEntity.ok().build();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    @GetMapping("/{trainerUsername}/{year}/{month}")
    public ResponseEntity<MonthlyWorkloadResponse> getMonthly(
            @PathVariable("trainerUsername") String trainerUsername,
            @PathVariable("year") int year,
            @PathVariable("month") int month
    ) {
        if (month < 1 || month > 12) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.getMonthly(trainerUsername, year, month));
    }
}

