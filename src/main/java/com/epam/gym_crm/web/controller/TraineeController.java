package com.epam.gym_crm.web.controller;

import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.service.ITraineeService;
import com.epam.gym_crm.web.dto.requestDto.ChangeActiveRequestDto;
import com.epam.gym_crm.web.dto.requestDto.RegisterTraineeRequestDto;
import com.epam.gym_crm.web.dto.requestDto.UpdateTraineeProfileRequestDto;
import com.epam.gym_crm.web.dto.requestDto.UpdateTraineeTrainersRequestDto;
import com.epam.gym_crm.web.dto.responseDto.RegisterResponseDto;
import com.epam.gym_crm.web.dto.responseDto.TraineeProfileResponseDto;
import com.epam.gym_crm.web.dto.responseDto.TraineeProfileWithUsernameResponseDto;
import com.epam.gym_crm.web.dto.responseDto.TraineeTrainingSummaryDto;
import com.epam.gym_crm.web.dto.responseDto.TrainerSummaryDto;
import com.epam.gym_crm.web.exception.UnauthorizedException;
import com.epam.gym_crm.web.mapper.RegistrationMapper;
import com.epam.gym_crm.web.mapper.TraineeProfileMapper;
import com.epam.gym_crm.web.mapper.TrainingMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/trainees")
@Tag(name = "Trainees")
public class TraineeController {
    private final ITraineeService traineeService;
    private final RegistrationMapper registrationMapper;
    private final TraineeProfileMapper profileMapper;
    private final TrainingMapper trainingMapper;

    public TraineeController(TraineeProfileMapper profileMapper,
                             ITraineeService traineeService,
                             RegistrationMapper registrationMapper,
                             TrainingMapper trainingMapper) {
        this.profileMapper = profileMapper;
        this.traineeService = traineeService;
        this.registrationMapper = registrationMapper;
        this.trainingMapper = trainingMapper;
    }

    @PostMapping
    @Operation(summary = "registration of trainee", security = {})
    @ApiResponse(responseCode = "200", description = "Created, returns username/password")
    @ApiResponse(responseCode = "400", description = "wrong data")
    public RegisterResponseDto registerTrainee
            (@Valid @RequestBody RegisterTraineeRequestDto dto) {
        Trainee entity = registrationMapper.toEntity(dto);
        Trainee created = traineeService.create(entity);

        return new RegisterResponseDto(
                created.getUser().getUsername(),
                created.getUser().getPassword()
        );
    }

    @GetMapping("/profile")
    @Operation(summary = "get trainee profile")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Trainee not found")
    public ResponseEntity<TraineeProfileResponseDto> getTraineeProfile(
            @RequestParam("username") String username) {
        Trainee trainee = traineeService.getProfile(username);
        if (trainee == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(profileMapper.toDto(trainee));
    }

    @PutMapping(path = "/profile", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Update trainee profile")
    @ApiResponse(responseCode = "200", description = "Profile updated")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<TraineeProfileWithUsernameResponseDto> updateTraineeProfile(
            @Valid @RequestBody UpdateTraineeProfileRequestDto dto,
            HttpServletRequest req) {

        String username = req.getHeader("X-Username");
        String password = req.getHeader("X-Password");

        Trainee updated = traineeService.updateProfile(
                username,
                password,
                dto.getFirstName(),
                dto.getLastName(),
                dto.getDateOfBirth(),
                dto.getAddress()
        );
        if (updated.getUser().isActive() != dto.getActive()) {
            updated = traineeService.setActive(username, password, dto.getActive());
        }
        return ResponseEntity.ok(profileMapper.toDtoWithUsername(updated));
    }
    @DeleteMapping("/profile")
    @Operation(summary = "delete trainee profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deleted (idempotent)"),
            @ApiResponse(responseCode = "400", description = "Username mismatch"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> deleteTraineeProfile(
            @RequestParam("username") String username,
            HttpServletRequest req) {

        String hUser = req.getHeader("X-Username");
        String hPass = req.getHeader("X-Password");

        if (hUser == null || hPass == null || !hUser.equals(username)) {
            throw new UnauthorizedException("Credentials mismatch");
        }

        traineeService.deleteByUsername(username, hPass);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/not-assigned-trainers")
    @Operation(summary = "Get active trainers not assigned to the trainee")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<List<TrainerSummaryDto>> getNotAssignedTrainers(
            @RequestParam("username") String username,
            HttpServletRequest req) {

        String hUser = req.getHeader("X-Username");
        String hPass = req.getHeader("X-Password");
        if (hUser == null || hPass == null || !hUser.equals(username)) {
            throw new UnauthorizedException("Credentials mismatch");
        }

        List<Trainer> trainers = traineeService.findNotAssignedTrainers(username, hPass);
        List<TrainerSummaryDto> dto = trainers.stream()
                .map(profileMapper::toTrainerSummary)
                .toList();
        return ResponseEntity.ok(dto);
    }

    @PutMapping(path = "/trainers", consumes = "application/json")
    @Operation(summary = "Update trainee's trainer list")
    @ApiResponse(responseCode = "200", description = "OK, returns trainers summary")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "401", description = "Unauthorized / credentials mismatch")
    public ResponseEntity<List<TrainerSummaryDto>> updateTraineeTrainers(
            @Valid @RequestBody UpdateTraineeTrainersRequestDto req,
            HttpServletRequest httpReq) {

        String hUser = httpReq.getHeader("X-Username");
        String hPass = httpReq.getHeader("X-Password");
        if (hUser == null || hPass == null || !hUser.equals(req.getUsername())) {
            throw new UnauthorizedException("Credentials mismatch");
        }

        traineeService.updateTrainers(req.getUsername(), hPass, req.getTrainerUsernames());

        Trainee fresh = traineeService.getProfile(req.getUsername(), hPass);

        List<TrainerSummaryDto> out = fresh.getTrainers().stream()
                .map(profileMapper::toTrainerSummary)
                .toList();

        return ResponseEntity.ok(out);
    }
    @GetMapping("/trainings")
    @Operation(summary = "Get trainee trainings with optional filters")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized / credentials mismatch")
    public ResponseEntity<List<TraineeTrainingSummaryDto>> getTraineeTrainings(
            @RequestParam("username") String username,
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to,
            @RequestParam(value = "trainer", required = false) String trainerName,
            @RequestParam(value = "type", required = false) String trainingType,
            HttpServletRequest httpReq) {

        String hUser = httpReq.getHeader("X-Username");
        String hPass = httpReq.getHeader("X-Password");
        if (hUser == null || hPass == null || !hUser.equals(username)) {
            throw new com.epam.gym_crm.web.exception.UnauthorizedException("Credentials mismatch");
        }

        List<Training> list;
        if (from != null || to != null) {
            if (from == null || to == null) {
                throw new IllegalArgumentException("Both 'from' and 'to' must be provided together");
            }
            if (from.isAfter(to)) {
                throw new IllegalArgumentException("'from' must be <= 'to'");
            }
            list = traineeService.findTrainingsByDateRange(username, hPass, from, to);
        } else if (trainerName != null && !trainerName.isBlank()) {
            list = traineeService.findTrainingsByTrainerName(username, hPass, trainerName);
        } else if (trainingType != null && !trainingType.isBlank()) {
            list = traineeService.findTrainingsByType(username, hPass, trainingType);
        } else {
            list = traineeService.getTrainings(username, hPass);
        }

        List<TraineeTrainingSummaryDto> out = list.stream()
                .map(trainingMapper::toSummary)
                .toList();

        return ResponseEntity.ok(out);
    }
    @PatchMapping(path = "/active", consumes = "application/json")
    @Operation(summary = "Activate/De-Activate trainee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized / credentials mismatch"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    public ResponseEntity<Void> changeActiveStatus(
            @Valid @RequestBody ChangeActiveRequestDto dto,
            HttpServletRequest httpReq) {

        String hUser = httpReq.getHeader("X-Username");
        String hPass = httpReq.getHeader("X-Password");
        if (hUser == null || hPass == null || !hUser.equals(dto.getUsername())) {
            throw new UnauthorizedException("Credentials mismatch");
        }

        traineeService.setActive(dto.getUsername(), hPass, dto.getActive());
        return ResponseEntity.ok().build();
    }
}