package com.epam.gym_crm.web.mapper;

import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.web.dto.responseDto.TraineeSummaryDto;
import com.epam.gym_crm.web.dto.responseDto.TrainerProfileResponseDto;
import com.epam.gym_crm.web.dto.responseDto.TrainerProfileWithUsernameResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainerProfileMapper {
    @Mapping(source = "user.firstName",     target = "firstName")
    @Mapping(source = "user.lastName",      target = "lastName")
    @Mapping(source = "specialization.name",target = "specialization")
    @Mapping(source = "user.active",        target = "active")
    @Mapping(source = "trainees",           target = "trainees")
    TrainerProfileResponseDto toDto(Trainer trainer);

    @Mapping(source = "user.username",  target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName",  target = "lastName")
    @Mapping(source = "specialization.name", target = "specialization")
    @Mapping(source = "user.active",    target = "active")
    @Mapping(source = "trainees",       target = "trainees")
    TrainerProfileWithUsernameResponseDto toDtoWithUsername(Trainer trainer);

    @Mapping(source = "user.username",  target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName",  target = "lastName")
    TraineeSummaryDto toTraineeSummary(Trainee trainee);
}
