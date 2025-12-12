package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.user.AddressDTO;
import com.petconnect.backend.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AddressMapper {

    @Mapping(target = "addressId", source = "addressId")
    @Mapping(target = "pincode", source = "pincode")
    @Mapping(target = "city", source = "city")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "country", source = "country")
    @Mapping(target = "locality", source = "locality")
    AddressDTO toDTO(Address address);

    @Mapping(target = "addressId", source = "addressId")
    @Mapping(target = "pincode", source = "pincode")
    @Mapping(target = "city", source = "city")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "country", source = "country")
    @Mapping(target = "locality", source = "locality")
    Address toEntity(AddressDTO addressDTO);
}
