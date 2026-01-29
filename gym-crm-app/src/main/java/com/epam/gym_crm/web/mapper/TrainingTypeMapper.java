package com.epam.gym_crm.web.mapper;

import com.epam.gym_crm.domain.TrainingType;
import com.epam.gym_crm.web.dto.responseDto.TrainingTypeResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {
    TrainingTypeResponseDto toDto(TrainingType tt);
}
