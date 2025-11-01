package com.epam.gym_crm.web.mapper;

import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.web.dto.requestDto.RegisterTraineeRequestDto;
import com.epam.gym_crm.web.dto.requestDto.RegisterTrainerRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {
    //Trainee
    @Mapping(target = "user.firstName", source = "dto.firstName")
    @Mapping(target = "user.lastName", source = "dto.lastName")
    @Mapping(target = "dateOfBirth", source = "dto.dateOfBirth")
    @Mapping(target = "address", source = "dto.address")
    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "id", ignore = true)
    public Trainee toEntity(RegisterTraineeRequestDto dto);

    //Trainer

    @Mapping(target = "user.firstName", source = "dto.firstName")
    @Mapping(target = "user.lastName", source = "dto.lastName")
    @Mapping(target = "specialization", ignore = true)
    @Mapping(target = "trainees", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "id", ignore = true)
    public Trainer toEntity(RegisterTrainerRequestDto dto);

}
