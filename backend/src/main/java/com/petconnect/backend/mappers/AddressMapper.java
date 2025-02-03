package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.AddressDTO;
import com.petconnect.backend.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    AddressDTO toDTO(Address address);
    Address toEntity(AddressDTO addressDTO);
}
