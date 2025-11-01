package com.epam.gym_crm.web.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class TraineeTrainingSummaryDto {
    private String trainingName;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime trainingDate;

    private String trainingType;
    private Integer trainingDuration;
    private String trainerName; // "First Last"

    public String getTrainingName() { return trainingName; }
    public void setTrainingName(String trainingName) { this.trainingName = trainingName; }

    public LocalDateTime getTrainingDate() { return trainingDate; }
    public void setTrainingDate(LocalDateTime trainingDate) { this.trainingDate = trainingDate; }

    public String getTrainingType() { return trainingType; }
    public void setTrainingType(String trainingType) { this.trainingType = trainingType; }

    public Integer getTrainingDuration() { return trainingDuration; }
    public void setTrainingDuration(Integer trainingDuration) { this.trainingDuration = trainingDuration; }

    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }
}
