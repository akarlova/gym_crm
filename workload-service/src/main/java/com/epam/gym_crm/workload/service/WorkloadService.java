package com.epam.gym_crm.workload.service;

import com.epam.gym_crm.workload.contract.ActionType;
import com.epam.gym_crm.workload.contract.TrainerWorkloadRequest;
import com.epam.gym_crm.workload.domain.TrainerMonthlyWorkload;
import com.epam.gym_crm.workload.web.dto.MonthlyWorkloadResponse;
import com.epam.gym_crm.workload.repo.ITrainerMonthlyWorkloadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkloadService {
    private final ITrainerMonthlyWorkloadRepository repo;
    private static final Logger log = LoggerFactory.getLogger(WorkloadService.class);

    public WorkloadService(ITrainerMonthlyWorkloadRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public void apply(TrainerWorkloadRequest req) {
        int year = req.getTrainingDate().getYear();
        int month = req.getTrainingDate().getMonthValue();

        log.info("WORKLOAD apply: action={}, trainer={}, date={}, minutes={}",
                req.getActionType(), req.getTrainerUsername(), req.getTrainingDate(), req.getTrainingDurationMinutes());

        TrainerMonthlyWorkload row = repo
                .findByTrainerUsernameAndYearAndMonth(req.getTrainerUsername(), year, month)
                .orElseGet(() -> {
                    TrainerMonthlyWorkload nw = new TrainerMonthlyWorkload();
                    nw.setTrainerUsername(req.getTrainerUsername());
                    nw.setTrainerFirstName(req.getTrainerFirstName());
                    nw.setTrainerLastName(req.getTrainerLastName());
                    nw.setActive(req.isActive());
                    nw.setYear(year);
                    nw.setMonth(month);
                    nw.setTotalMinutes(0);
                    return nw;
                });

        row.setTrainerFirstName(req.getTrainerFirstName());
        row.setTrainerLastName(req.getTrainerLastName());
        row.setActive(req.isActive());

        int cur = row.getTotalMinutes();
        int delta = req.getTrainingDurationMinutes();

        int updated;
        if (req.getActionType() == ActionType.ADD) {
            updated = cur + delta;
        } else if (req.getActionType() == ActionType.DELETE) {
            updated = Math.max(0, cur - delta);
        } else {
            throw new IllegalArgumentException("Unknown actionType: " + req.getActionType());
        }

        row.setTotalMinutes(updated);

        log.info("WORKLOAD updated: trainer={}, year={}, month={}, totalMinutes {} -> {}",
                row.getTrainerUsername(), year, month, cur, updated);

        repo.save(row);
    }
    @Transactional(readOnly = true)
    public MonthlyWorkloadResponse getMonthly(String trainerUsername, int year, int month) {
        var row = repo.findByTrainerUsernameAndYearAndMonth(trainerUsername, year, month)
                .orElse(null);

        int total = (row == null) ? 0 : row.getTotalMinutes();
        return new MonthlyWorkloadResponse(trainerUsername, year, month, total);
    }
}
