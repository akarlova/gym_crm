package com.epam.gym_crm.web.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ChangeActiveRequestDto {
    @NotBlank
    private String username;

    @NotNull
    private Boolean active;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
