package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.entity.Address;
import com.petconnect.backend.entity.Specialist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SpecialistMapper {

    SpecialistDTO toDTO(Specialist specialist);

    @Mapping(target = "address", source = "addressDTO")
    Specialist toSpecialistEntity(SpecialistCreateRequestDTO specialistCreateRequestDTO);

    SpecialistResponseDTO toSpecialistResponseDTO(SpecialistDTO specialistDTO);

    @Mapping(target = "address", source = "addressDTO")
    void updateSpecialistEntity(SpecialistUpdateRequestDTO specialistUpdateRequestDTO, @MappingTarget Specialist specialist);

    AddressDTO toAddressDTO(Address address);

    Address toAddress(AddressDTO addressDTO);
}