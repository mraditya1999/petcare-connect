package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.pet.PetRequestDTO;
import com.petconnect.backend.dto.pet.PetResponseDTO;
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
    PetResponseDTO toDTO(Pet pet);

    @Mappings({
            @Mapping(source = "gender", target = "gender", qualifiedByName = "stringToEnum")
    })
    Pet toEntity(PetRequestDTO petRequestDTO);

    @Named("enumToString")
    default String enumToString(Pet.Gender gender) {
        return gender != null ? gender.name() : null;
    }

    @Named("stringToEnum")
    default Pet.Gender stringToEnum(String gender) {
        return gender != null ? Pet.Gender.valueOf(gender.toUpperCase()) : null;
    }
}
