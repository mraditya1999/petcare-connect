package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.SpecialistDTO;
import com.petconnect.backend.entity.Specialist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SpecialistMapper {
//    @Mapping(source = "specialistId", target = "specialistId")
    SpecialistDTO toDTO(Specialist specialist);
    Specialist toEntity(SpecialistDTO specialistDTO);

    void updateSpecialistFromDTO(SpecialistDTO specialistDTO, @MappingTarget Specialist specialist);
}
