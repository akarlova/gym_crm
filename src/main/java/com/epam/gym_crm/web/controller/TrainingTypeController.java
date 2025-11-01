package com.epam.gym_crm.web.controller;

import com.epam.gym_crm.domain.TrainingType;
import com.epam.gym_crm.service.ITrainingTypeService;
import com.epam.gym_crm.web.dto.responseDto.TrainingTypeResponseDto;
import com.epam.gym_crm.web.mapper.TrainingTypeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/training-types")
@Tag(name = "Training Types")
public class TrainingTypeController {
    private final ITrainingTypeService service;
    private final TrainingTypeMapper mapper;

    public TrainingTypeController(ITrainingTypeService service, TrainingTypeMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "Get all training types", security = {})
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<List<TrainingTypeResponseDto>> getAll() {
        List<TrainingType> types = service.findAll();
        List<TrainingTypeResponseDto> out = types.stream()
                .map(mapper::toDto)
                .toList();
        return ResponseEntity.ok(out);
    }
}
