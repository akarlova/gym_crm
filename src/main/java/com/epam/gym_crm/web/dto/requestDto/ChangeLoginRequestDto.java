package com.epam.gym_crm.web.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangeLoginRequestDto {

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 10, max = 50)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String oldPassword;

    @NotBlank
    @Size(min = 10, max = 50)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getUsername() {
        return username;
    }
}
