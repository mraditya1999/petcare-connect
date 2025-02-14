//package com.petconnect.backend.mappers;
//
//import com.petconnect.backend.dto.*;
//import com.petconnect.backend.entity.Address;
//import com.petconnect.backend.entity.Specialist;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.MappingTarget;
//
//@Mapper(componentModel = "spring")
//public interface SpecialistMapper {
//
//    @Mapping(target = "specialistId", expression = "java(specialist.getUserId())")
//    SpecialistDTO toDTO(Specialist specialist);
//
//    @Mapping(target = "address", source = "addressDTO")
//    Specialist toSpecialistEntity(SpecialistCreateRequestDTO specialistCreateRequestDTO);
//
//    @Mapping(target = "specialistId", source = "specialistId")
//    SpecialistResponseDTO toSpecialistResponseDTO(SpecialistDTO specialistDTO);
//
//    @Mapping(target = "address", source = "addressDTO")
//    void updateSpecialistEntity(SpecialistUpdateRequestDTO specialistUpdateRequestDTO, @MappingTarget Specialist specialist);
//}

package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.entity.Specialist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SpecialistMapper {

    @Mapping(target = "specialistId", source = "userId")
    SpecialistDTO toDTO(Specialist specialist);

    @Mapping(target = "address", source = "addressDTO")
    Specialist toSpecialistEntity(SpecialistCreateRequestDTO specialistCreateRequestDTO);

    @Mapping(target = "userId", source = "specialistId")
    SpecialistResponseDTO toSpecialistResponseDTO(Specialist specialist);

    @Mapping(target = "userId", source = "specialistId")
    SpecialistResponseDTO toSpecialistResponseDTO(SpecialistDTO specialistDTO);

    @Mapping(target = "address", source = "addressDTO")
    void updateSpecialistEntity(SpecialistUpdateRequestDTO specialistUpdateRequestDTO, @MappingTarget Specialist specialist);
}
