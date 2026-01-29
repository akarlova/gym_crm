package com.epam.gym_crm.web.controller;

import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.domain.TrainingType;
import com.epam.gym_crm.service.ITrainerService;
import com.epam.gym_crm.web.dto.requestDto.ChangeActiveRequestDto;
import com.epam.gym_crm.web.dto.requestDto.RegisterTrainerRequestDto;
import com.epam.gym_crm.web.dto.requestDto.UpdateTrainerProfileRequestDto;
import com.epam.gym_crm.web.dto.responseDto.RegisterResponseDto;
import com.epam.gym_crm.web.dto.responseDto.TrainerProfileResponseDto;
import com.epam.gym_crm.web.dto.responseDto.TrainerProfileWithUsernameResponseDto;
import com.epam.gym_crm.web.dto.responseDto.TrainerTrainingSummaryDto;
import com.epam.gym_crm.web.mapper.RegistrationMapper;
import com.epam.gym_crm.web.mapper.TrainerProfileMapper;
import com.epam.gym_crm.web.mapper.TrainingMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/trainers")
@Tag(name = "Trainers")
public class TrainerController {
    private final ITrainerService trainerService;
    private final RegistrationMapper registrationMapper;
    private final TrainerProfileMapper trainerProfileMapper;
    private final TrainingMapper trainingMapper;

    public TrainerController(RegistrationMapper registrationMapper,
                             ITrainerService trainerService,
                             TrainerProfileMapper trainerProfileMapper,
                             TrainingMapper trainingMapper) {
        this.registrationMapper = registrationMapper;
        this.trainerService = trainerService;
        this.trainerProfileMapper = trainerProfileMapper;
        this.trainingMapper = trainingMapper;
    }

    @PostMapping
    @Operation(summary = "registration of trainer", security = {})
    @ApiResponse(responseCode = "200", description = "Created, returns username/password")
    @ApiResponse(responseCode = "400", description = "Wrong data")
    public RegisterResponseDto registerTrainer(
            @Valid @RequestBody RegisterTrainerRequestDto dto) {
        Trainer entity = registrationMapper.toEntity(dto);
        TrainingType ref = new TrainingType();
        ref.setId(dto.getSpecializationId());
        entity.setSpecialization(ref);
        Trainer created = trainerService.create(entity);
        return new RegisterResponseDto(
                created.getUser().getUsername(),
//                created.getUser().getPassword()
                entity.getUser().getRawPassword()
        );
    }
    @GetMapping("/profile")
    @Operation(summary = "get trainer profile")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Trainer not found")
    public ResponseEntity<TrainerProfileResponseDto> getTrainerProfile(
            Principal principal) {
        String username = principal.getName();
        var trainer = trainerService.getProfile(username);
        return ResponseEntity.ok(trainerProfileMapper.toDto(trainer));
    }
    @PutMapping(path = "/profile", consumes = "application/json")
    @Operation(summary = "Update trainer profile (first/last name, active). Specialization is read-only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "updated"),
            @ApiResponse(responseCode = "400", description = "validation error"),
            @ApiResponse(responseCode = "401", description = "unauthorized / credentials mismatch"),
            @ApiResponse(responseCode = "404", description = "trainer not found")
    })
    public ResponseEntity<TrainerProfileWithUsernameResponseDto> updateTrainerProfile(
            @Valid @RequestBody UpdateTrainerProfileRequestDto req,
            Principal principal) {

        String username = principal.getName();

        trainerService.updateProfile(username, req.getFirstName(), req.getLastName());
        trainerService.setActive(username, req.getActive());

        var trainer = trainerService.getProfile(username);
        return ResponseEntity.ok(trainerProfileMapper.toDtoWithUsername(trainer));
    }
    @GetMapping("/trainings")
    @Operation(summary = "Get trainer trainings with optional filters")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized / credentials mismatch")
    public ResponseEntity<List<TrainerTrainingSummaryDto>> getTrainerTrainings(
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(value = "trainee", required = false) String traineeName,
            @RequestParam(value = "type", required = false) String trainingTypeName,
            Principal principal) {

        String username = principal.getName();

        List<Training> list;
        if (traineeName != null && !traineeName.isBlank()) {
            list = trainerService.findTrainingsByTraineeName(username, traineeName);
        } else if (trainingTypeName != null && !trainingTypeName.isBlank()) {
            list = trainerService.findTrainingsByType(username, trainingTypeName);
        }
        else if (from != null || to != null) {
            if (from == null || to == null) {
                throw new IllegalArgumentException("Both 'from' and 'to' must be provided together");
            }
            if (from.isAfter(to)) {
                throw new IllegalArgumentException("'from' must be <= 'to'");
            }
            list = trainerService.findTrainingsByDateRange(username, from, to);
        } else {
            list = trainerService.getTrainings(username);
        }

        List<TrainerTrainingSummaryDto> out = list.stream()
                .map(trainingMapper::toTrainerSummary)
                .toList();

        return ResponseEntity.ok(out);
    }
    @PatchMapping(path = "/active", consumes = "application/json")
    @Operation(summary = "Activate/De-Activate trainer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized / credentials mismatch"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    public ResponseEntity<Void> changeActiveStatus(
            @Valid @RequestBody ChangeActiveRequestDto dto,
            Principal principal) {
        String username = principal.getName();

        if (dto.getActive() == null) {
            throw new IllegalArgumentException("'active' must not be null");
        }
        trainerService.setActive(username, dto.getActive());
        return ResponseEntity.ok().build();
    }
}
