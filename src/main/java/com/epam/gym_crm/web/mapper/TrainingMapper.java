package com.epam.gym_crm.web.mapper;

import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.web.dto.responseDto.TraineeTrainingSummaryDto;
import com.epam.gym_crm.web.dto.responseDto.TrainerTrainingSummaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingMapper {
    @Mapping(source = "trainingName", target = "trainingName")
    @Mapping(source = "trainingDate", target = "trainingDate")
    @Mapping(source = "trainingType.name", target = "trainingType")
    @Mapping(source = "trainingDuration", target = "trainingDuration")
    @Mapping(target = "trainerName",
            expression = "java( training.getTrainer()!=null && training.getTrainer().getUser()!=null ? " +
                         "training.getTrainer().getUser().getFirstName() + \" \" + training.getTrainer().getUser().getLastName() : null)")
    TraineeTrainingSummaryDto toSummary(Training training);

    @Mapping(source = "trainingName", target = "trainingName")
    @Mapping(source = "trainingDate", target = "trainingDate")
    @Mapping(source = "trainingType.name", target = "trainingType")
    @Mapping(source = "trainingDuration", target = "trainingDuration")
    @Mapping(target = "traineeName", expression = "java(fullName(training.getTrainee()))")
    TrainerTrainingSummaryDto toTrainerSummary(Training training);

    default String fullName(Trainee t) {
        if (t == null || t.getUser() == null) return null;
        var u = t.getUser();
        return (u.getFirstName() == null ? "" : u.getFirstName()) +
               " " +
               (u.getLastName() == null ? "" : u.getLastName());
    }
}
