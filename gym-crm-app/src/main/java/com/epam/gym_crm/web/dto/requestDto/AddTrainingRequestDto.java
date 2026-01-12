package com.epam.gym_crm.web.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class AddTrainingRequestDto {
    @NotBlank
    private String traineeUsername;

    @NotBlank
    private String trainerUsername;

    @NotBlank
    private String trainingName;

    @NotBlank
    private String trainingTypeName;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime trainingDate;

    @NotNull
    @Min(30)
    private Integer durationMinutes;

    public AddTrainingRequestDto() {}

    public String getTraineeUsername() { return traineeUsername; }
    public void setTraineeUsername(String traineeUsername) { this.traineeUsername = traineeUsername; }

    public String getTrainerUsername() { return trainerUsername; }
    public void setTrainerUsername(String trainerUsername) { this.trainerUsername = trainerUsername; }

    public String getTrainingName() { return trainingName; }
    public void setTrainingName(String trainingName) { this.trainingName = trainingName; }

    public String getTrainingTypeName() { return trainingTypeName; }
    public void setTrainingTypeName(String trainingTypeName) { this.trainingTypeName = trainingTypeName; }

    public LocalDateTime getTrainingDate() { return trainingDate; }
    public void setTrainingDate(LocalDateTime trainingDate) { this.trainingDate = trainingDate; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
}
