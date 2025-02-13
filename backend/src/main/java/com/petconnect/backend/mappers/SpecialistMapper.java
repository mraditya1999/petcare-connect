package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.entity.Address;
import com.petconnect.backend.entity.Specialist;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SpecialistMapper {

    SpecialistDTO toDTO(Specialist specialist);

    Specialist toEntity(SpecialistCreateRequestDTO specialistCreateRequestDTO);

    SpecialistResponseDTO toSpecialistResponseDTO(SpecialistDTO specialistDTO);

    void updateSpecialistFromDTO(SpecialistUpdateRequestDTO specialistUpdateRequestDTO, @MappingTarget Specialist specialist);

    AddressDTO toAddressDTO(Address address); // For converting Address to AddressDTO

    Address toAddress(AddressDTO addressDTO); // For converting AddressDTO to Address
}