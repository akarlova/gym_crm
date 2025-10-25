package com.epam.gym_crm.controller;

import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.dto.request.TraineeRegistrationRequest;
import com.epam.gym_crm.dto.response.RegistrationResponse;
import com.epam.gym_crm.facade.GymFacade;
import com.epam.gym_crm.mapper.TraineeMapper;
import io.swagger.annotations.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainees")
@Api(tags = "Trainee Management", description = "Operations for trainee management")
public class TraineeController {
    
    private static final Logger log = LoggerFactory.getLogger(TraineeController.class);
    private final GymFacade gymFacade;
    private final TraineeMapper traineeMapper;

    public TraineeController(GymFacade gymFacade, TraineeMapper traineeMapper) {
        this.gymFacade = gymFacade;
        this.traineeMapper = traineeMapper;
    }

    @PostMapping("/register")
    @ApiOperation(value = "Register a new trainee", response = RegistrationResponse.class)
    @ApiResponses({
        @ApiResponse(code = 201, message = "Trainee registered successfully"),
        @ApiResponse(code = 400, message = "Invalid input data")
    })
    public ResponseEntity<RegistrationResponse> registerTrainee(@Valid @RequestBody TraineeRegistrationRequest request) {

        log.info("POST /api/trainees/register - firstName: {}, lastName: {}",
                request.getFirstName(), request.getLastName());

        Trainee trainee = traineeMapper.toEntity(request);
        Trainee created = gymFacade.createTrainee(trainee);
        RegistrationResponse response = traineeMapper.toRegistrationResponse(created);
        log.info("Trainee registered successfully - username: {}", response.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/test")
    public String test() {
        return "Controller is working!";
    }

}