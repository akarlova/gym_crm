package com.epam.gym_crm.web.controller;

import com.epam.gym_crm.facade.GymFacade;
import com.epam.gym_crm.web.dto.requestDto.AddTrainingRequestDto;
import com.epam.gym_crm.web.exception.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

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
                                            Principal principal) {
        String trainerUsername = principal.getName();
        if (req.getTrainerUsername() != null
            && !req.getTrainerUsername().trim().equals(trainerUsername)) {
            throw new UnauthorizedException("Credentials mismatch");
        }
        gymFacade.addTraining(
                trainerUsername,
                req.getTraineeUsername(),
                req.getTrainingName(),
                req.getTrainingTypeName(),
                req.getTrainingDate(),
                req.getDurationMinutes()
        );

        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTraining(@PathVariable("id") Long id) {
        boolean ok = gymFacade.deleteTraining(id);
        return ok ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
