package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.specialist.SpecialistResponseDTO;
import com.petconnect.backend.dto.specialist.SpecialistUpdateRequestDTO;
import com.petconnect.backend.dto.specialist.SpecialistCreateRequestDTO;
import com.petconnect.backend.entity.Specialist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { RoleMapper.class })
public interface SpecialistMapper {

    @Mapping(target = "specialistId", source = "userId")
    @Mapping(target = "userId", source = "userId") // Explicitly map userId
    SpecialistResponseDTO toDTO(Specialist specialist);

    @Mapping(target = "address.pincode", source = "pincode")
    @Mapping(target = "address.city", source = "city")
    @Mapping(target = "address.state", source = "state")
    @Mapping(target = "address.country", source = "country")
    @Mapping(target = "address.locality", source = "locality")
    Specialist toSpecialistEntity(SpecialistCreateRequestDTO specialistCreateRequestDTO);

    @Mapping(target = "userId", source = "specialistId")
    @Mapping(target = "specialistId", source = "userId") //Explicitly mapping specialistId
    SpecialistResponseDTO toSpecialistResponseDTO(Specialist specialist);

    @Mapping(target = "userId", source = "specialistId")
    @Mapping(target = "specialistId", source = "specialistId") //Explicitly mapping specialistId
    SpecialistResponseDTO toSpecialistResponseDTO(SpecialistResponseDTO specialistResponseDTO);

//    @Mapping(target = "address.pincode", source = "pincode")
//    @Mapping(target = "address.city", source = "city")
//    @Mapping(target = "address.state", source = "state")
//    @Mapping(target = "address.country", source = "country")
//    @Mapping(target = "address.locality", source = "locality")
//    void updateSpecialistEntity(SpecialistUpdateRequestDTO specialistUpdateRequestDTO, @MappingTarget Specialist specialist);
}