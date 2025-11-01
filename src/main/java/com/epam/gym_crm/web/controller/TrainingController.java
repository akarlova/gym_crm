package com.epam.gym_crm.web.controller;

import com.epam.gym_crm.facade.GymFacade;
import com.epam.gym_crm.web.dto.requestDto.AddTrainingRequestDto;
import com.epam.gym_crm.web.exception.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trainings")
@Tag(name = "Trainings")
public class TrainingController {
    private final GymFacade gymFacade;

    public TrainingController(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }

    @PostMapping
    @Operation(summary = "Add training (trainer auth required)")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "401", description = "Unauthorized / credentials mismatch")
    @ApiResponse(responseCode = "409", description = "Data conflict")
    public ResponseEntity<Void> addTraining(@Valid @RequestBody AddTrainingRequestDto req,
                                            HttpServletRequest httpReq) {
        String hUser = httpReq.getHeader("X-Username");
        String hPass = httpReq.getHeader("X-Password");
        if (hUser == null || hPass == null || !hUser.equals(req.getTrainerUsername())) {
            throw new UnauthorizedException("Credentials mismatch");
        }

        gymFacade.addTraining(
                req.getTrainerUsername(),
                hPass,
                req.getTraineeUsername(),
                req.getTrainingName(),
                req.getTrainingTypeName(),
                req.getTrainingDate(),
                req.getDurationMinutes()
        );

        return ResponseEntity.ok().build();
    }
}
