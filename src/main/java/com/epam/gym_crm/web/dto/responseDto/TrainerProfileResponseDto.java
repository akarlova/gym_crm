package com.epam.gym_crm.web.dto.responseDto;

import java.util.ArrayList;
import java.util.List;

public class TrainerProfileResponseDto {
    private String firstName;
    private String lastName;
    private String specialization; // training type name
    private boolean active;
    private List<TraineeSummaryDto> trainees = new ArrayList<>();

    public TrainerProfileResponseDto() {
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public List<TraineeSummaryDto> getTrainees() {
        return trainees;
    }

    public void setTrainees(List<TraineeSummaryDto> trainees) {
        this.trainees = trainees;
    }
}
