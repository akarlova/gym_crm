package com.epam.gym_crm.workload.repo;

import com.epam.gym_crm.workload.domain.TrainerMonthlyWorkload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ITrainerMonthlyWorkloadRepository extends JpaRepository<TrainerMonthlyWorkload, Long> {
    Optional<TrainerMonthlyWorkload> findByTrainerUsernameAndYearAndMonth(String trainerUsername, int year, int month);
}
