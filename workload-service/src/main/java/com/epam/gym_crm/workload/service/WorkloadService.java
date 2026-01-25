package com.epam.gym_crm.workload.service;

import com.epam.gym_crm.workload.contract.ActionType;
import com.epam.gym_crm.workload.contract.TrainerWorkloadRequest;
import com.epam.gym_crm.workload.domain.TrainerMonthlyWorkload;
import com.epam.gym_crm.workload.web.dto.MonthlyWorkloadResponse;
import com.epam.gym_crm.workload.repo.ITrainerMonthlyWorkloadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkloadService {
    private final ITrainerMonthlyWorkloadRepository repo;

    public WorkloadService(ITrainerMonthlyWorkloadRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public void apply(TrainerWorkloadRequest req) {
        int year = req.getTrainingDate().getYear();
        int month = req.getTrainingDate().getMonthValue();

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

        if (req.getActionType() == ActionType.ADD) {
            row.setTotalMinutes(cur + delta);
        } else if (req.getActionType() == ActionType.DELETE) {
            row.setTotalMinutes(Math.max(0, cur - delta));
        } else {
            throw new IllegalArgumentException("Unknown actionType: " + req.getActionType());
        }

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
