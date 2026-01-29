package com.epam.gym_crm.web.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegisterTrainerRequestDto {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotNull
    private long specializationId;

    public RegisterTrainerRequestDto() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getSpecializationId() {
        return specializationId;
    }

    public void setSpecializationId(long specializationId) {
        this.specializationId = specializationId;
    }
}
