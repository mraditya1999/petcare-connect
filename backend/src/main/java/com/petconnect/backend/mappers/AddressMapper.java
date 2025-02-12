package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.AddressDTO;
import com.petconnect.backend.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AddressMapper {

    @Mapping(target = "addressId", source = "addressId") // Ensure ID is correctly mapped
    AddressDTO toDTO(Address address);

    @Mapping(target = "addressId", source = "addressId")
    Address toEntity(AddressDTO addressDTO);
}
