package com.epam.gym_crm.metrics;

import com.epam.gym_crm.repository.ITraineeRepository;
import com.epam.gym_crm.repository.ITrainerRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.stereotype.Component;

@Component
public class GymMetrics implements MeterBinder {
    private final ITrainerRepository trainerRepo;
    private final ITraineeRepository traineeRepo;

    public static final String ACTIVE_TRAINERS_GAUGE = "gym.trainers.active";
    public static final String ACTIVE_TRAINEES_GAUGE = "gym.trainees.active";

    public GymMetrics(ITrainerRepository trainerRepo,
                      ITraineeRepository traineeRepo) {
        this.trainerRepo = trainerRepo;
        this.traineeRepo = traineeRepo;
    }

    @Override
    public void bindTo(MeterRegistry registry) {

        Gauge.builder(ACTIVE_TRAINERS_GAUGE, () ->
                        trainerRepo.findAll().stream()
                                .filter(t -> t.getUser() != null && Boolean.TRUE
                                        .equals(t.getUser().isActive()))
                                .count()
                )
                .description("Active trainers count")
                .register(registry);

        Gauge.builder(ACTIVE_TRAINEES_GAUGE, () ->
                        traineeRepo.findAll().stream()
                                .filter(t -> t.getUser() != null && Boolean.TRUE
                                        .equals(t.getUser().isActive()))
                                .count()
                )
                .description("Active trainees count")
                .register(registry);
    }
}
