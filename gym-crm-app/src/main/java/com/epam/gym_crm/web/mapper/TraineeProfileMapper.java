package com.epam.gym_crm.web.mapper;

import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.web.dto.responseDto.TraineeProfileResponseDto;
import com.epam.gym_crm.web.dto.responseDto.TraineeProfileWithUsernameResponseDto;
import com.epam.gym_crm.web.dto.responseDto.TrainerSummaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface TraineeProfileMapper {

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "address", target = "address")
    @Mapping(source="user.active", target = "active")
    @Mapping(source = "trainers",       target = "trainers")
    TraineeProfileResponseDto toDto(Trainee trainee);

    @Mapping(source = "user.username",  target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName",  target = "lastName")
    @Mapping(source = "dateOfBirth",    target = "dateOfBirth")
    @Mapping(source = "address",        target = "address")
    @Mapping(source = "user.active",    target = "active")
    @Mapping(source = "trainers",       target = "trainers")
    TraineeProfileWithUsernameResponseDto toDtoWithUsername(Trainee trainee);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "specialization.name", target = "specialization")
    TrainerSummaryDto toTrainerSummary(Trainer tr);

    List<TrainerSummaryDto> toTrainerSummaryList(Set<Trainer> trainers);
}
