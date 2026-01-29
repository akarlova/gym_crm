package com.epam.gym_crm.web.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class UpdateTraineeTrainersRequestDto {
    @NotBlank
    private String username;

    @NotEmpty
    private List<@NotBlank String> trainerUsernames;

    public UpdateTraineeTrainersRequestDto() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public List<String> getTrainerUsernames() { return trainerUsernames; }
    public void setTrainerUsernames(List<String> trainerUsernames) { this.trainerUsernames = trainerUsernames; }
}
