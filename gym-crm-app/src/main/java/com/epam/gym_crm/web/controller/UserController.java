package com.epam.gym_crm.web.controller;

import com.epam.gym_crm.facade.GymFacade;
import com.epam.gym_crm.web.dto.requestDto.ChangeLoginRequestDto;
import com.epam.gym_crm.web.exception.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@RestController
@RequestMapping("/users")
@Tag(name = "Users")
public class UserController {

    private final GymFacade gymFacade;


    public UserController(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }

    @GetMapping("/ping")
    @Operation(summary = "checking if works", security = {})
    @ApiResponse(responseCode = "200", description = "ok")
    public String ping() {
        return "ok";
    }

    @GetMapping("/login")
    @Operation(summary = "login by username & password", security = {})
    @ApiResponse(responseCode = "200", description = "success")
    @ApiResponse(responseCode = "401", description = "bad credentials")
    public ResponseEntity<Void> login(Principal principal) {
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/login", consumes = "application/json")
    @Operation(summary = "Change password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized / credentials mismatch"),
            @ApiResponse(responseCode = "409", description = "Data conflict"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public ResponseEntity<Void> changeLogin(@Valid @RequestBody ChangeLoginRequestDto req,
                                            Principal principal) {
        String currentUser = principal.getName();

        try {
            gymFacade.changeTraineePassword(currentUser, req.getOldPassword(), req.getNewPassword());
        } catch (RuntimeException e1) {
            try {
                gymFacade.changeTrainerPassword(currentUser, req.getOldPassword(), req.getNewPassword());
            } catch (RuntimeException e2) {
                throw new UnauthorizedException("Invalid credentials or user not found");
            }
        }
        return ResponseEntity.ok().build();
    }
}
