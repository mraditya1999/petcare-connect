//package com.petconnect.backend.mappers;
//
//public class SpecialistMapper {
//
//    public static SpecialistDTO toDTO(Specialist specialist, User user) {
//        Address address = user.getAddress();
//
//        return new SpecialistDTO(
//                specialist.getSpecialistId(),
//                specialist.getAbout(),
//                user.getUserId(),
//                specialist.getSpeciality(),
//                user.getFirstName(),
//                user.getLastName(),
//                user.getEmail(),
//                user.getAvatarUrl(),
//                (address != null) ? address.getAddressLine1() : null,
//                (address != null) ? address.getCity() : null,
//                (address != null) ? address.getState() : null
//        );
//    }
//
//    public static Specialist toEntity(SpecialistDTO dto) {
//        Specialist specialist = new Specialist();
//        specialist.setAbout(dto.getAbout());
//        specialist.setSpeciality(dto.getSpecialityId());
//        return specialist;
//    }
//}
