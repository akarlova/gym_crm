package com.epam.gym_crm.web.controller;

import com.epam.gym_crm.facade.GymFacade;
import com.epam.gym_crm.service.IAuthService;
import com.epam.gym_crm.web.dto.requestDto.ChangeLoginRequestDto;
import com.epam.gym_crm.web.exception.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users")
@Tag(name = "Users")
public class UserController {

    private final IAuthService authService;
    private final GymFacade gymFacade;


    public UserController(IAuthService authService, GymFacade gymFacade) {
        this.authService = authService;
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
    public ResponseEntity<Void> login(@RequestParam("username") String username,
                                      @RequestParam("password") String password) {
        boolean ok = authService.verifyTrainee(username, password) ||
                     authService.verifyTrainer(username, password);
        if (!ok) {
            throw new UnauthorizedException("Invalid credentials");
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/login", consumes = "application/json")
    @Operation(summary = "Change password", description = "Requires X-Username/X-Password headers and matching body")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized / credentials mismatch"),
            @ApiResponse(responseCode = "409", description = "Data conflict"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public ResponseEntity<Void> changeLogin(@Valid @RequestBody ChangeLoginRequestDto req,
                                            HttpServletRequest httpReq) {
        String hUser = httpReq.getHeader("X-Username");
        String hPass = httpReq.getHeader("X-Password");
        if (hUser == null || hPass == null
            || !hUser.equals(req.getUsername())
            || !hPass.equals(req.getOldPassword())) {
            throw new UnauthorizedException("Credentials mismatch");
        }

        if (authService.verifyTrainee(req.getUsername(), req.getOldPassword())) {
            gymFacade.changeTraineePassword(req.getUsername(), req.getOldPassword(), req.getNewPassword());
        } else if (authService.verifyTrainer(req.getUsername(), req.getOldPassword())) {
            gymFacade.changeTrainerPassword(req.getUsername(), req.getOldPassword(), req.getNewPassword());
        } else {
            throw new UnauthorizedException("Invalid credentials");
        }
        return ResponseEntity.ok().build();
    }
}
