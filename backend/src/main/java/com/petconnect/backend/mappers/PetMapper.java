package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.PetDTO;
import com.petconnect.backend.entity.Pet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PetMapper {
    @Mappings({
            @Mapping(source = "gender", target = "gender", qualifiedByName = "enumToString")
    })
    PetDTO toDTO(Pet pet);

    @Mappings({
            @Mapping(source = "gender", target = "gender", qualifiedByName = "stringToEnum")
    })
    Pet toEntity(PetDTO petDTO);

    @Named("enumToString")
    default String enumToString(Pet.Gender gender) {
        return gender != null ? gender.name() : null;
    }

    @Named("stringToEnum")
    default Pet.Gender stringToEnum(String gender) {
        return gender != null ? Pet.Gender.valueOf(gender.toUpperCase()) : null;
    }
}
