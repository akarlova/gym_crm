package com.epam.gym_crm.web.controller;

import com.epam.gym_crm.repository.ITraineeRepository;
import com.epam.gym_crm.repository.ITrainerRepository;
import com.epam.gym_crm.security.BruteForceService;
import com.epam.gym_crm.security.JwtService;
import com.epam.gym_crm.security.TokenBlacklistService;
import com.epam.gym_crm.web.dto.requestDto.LoginRequestDto;
import com.epam.gym_crm.web.dto.responseDto.LoginResponseDto;
import com.epam.gym_crm.web.exception.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication")
public class AuthController {
    private final ITraineeRepository traineeRepository;
    private final ITrainerRepository trainerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final BruteForceService brute;
    private final TokenBlacklistService blacklist;

    public AuthController(JwtService jwtService,
                          ITraineeRepository traineeRepository,
                          ITrainerRepository trainerRepository,
                          PasswordEncoder passwordEncoder,
                          BruteForceService brute,
                          TokenBlacklistService blacklist) {
        this.jwtService = jwtService;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.passwordEncoder = passwordEncoder;
        this.brute = brute;
        this.blacklist = blacklist;
    }

    @Operation(summary = "Authentication", description = "authentication with JWT-token", security = {})
    @ApiResponse(responseCode = "200", description = "Created, returns token")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto req) {
        String username = req.getUsername().trim();

        if (brute.isBlocked(username)) {
            throw new UnauthorizedException(
                    "User is temporarily blocked due to multiple failed logins." +
                    " Try again in 5 minutes."
            );
        }
        var trainee = traineeRepository.findByUsername(username);
        if (trainee.isPresent()) {
            var user = trainee.get().getUser();
            if (user.isActive() && passwordEncoder.matches(req.getPassword(), user.getPassword())) {
                brute.onSuccess(username);
                return ResponseEntity.ok(new LoginResponseDto(jwtService.generateToken(username)));
            }
        }
        var trainer = trainerRepository.findByUsername(username);
        if (trainer.isPresent()) {
            var user = trainer.get().getUser();
            if (user.isActive() && passwordEncoder.matches(req.getPassword(), user.getPassword())) {
                brute.onSuccess(username);
                return ResponseEntity.ok(new LoginResponseDto(jwtService.generateToken(username)));
            }
        }

        brute.onFailure(username);
        int left = brute.remaining(username);
        String msg = (left == 0)
                ? "Too many failed attempts. User is blocked for 5 minutes."
                : "Invalid credentials. Attempts left before block: " + left;

        throw new UnauthorizedException(msg);
    }
    @PostMapping("/logout")
    @Operation(summary = "Logout (blacklist current JWT)")
    @ApiResponse(responseCode = "200", description = "Logged out")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing Bearer token");
        }
        String token = header.substring(7).trim();
        long ttl = jwtService.getRemainingSeconds(token);
        blacklist.blacklist(token, ttl);
        return ResponseEntity.ok().build();
    }

}
