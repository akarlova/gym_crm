package com.epam.gym_crm.web.dto.responseDto;

public class RegisterResponseDto {
    private String username;
    private String password;

    public RegisterResponseDto() {
    }

    public RegisterResponseDto(String username, String password) {
        this.password = password;
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
