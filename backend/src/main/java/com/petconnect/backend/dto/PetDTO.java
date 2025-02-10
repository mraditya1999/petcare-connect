//package com.petconnect.backend.dto;
//
//public class PetRequestDTO {
//    private String petName;
//    private Integer age;
//    private Double weight;
//    private String avatarUrl;
//    private Long petOwnerId; // Only send userId, not entire User object
//
//    // Getters and Setters
//
//    public String getPetName() {
//        return petName;
//    }
//
//    public void setPetName(String petName) {
//        this.petName = petName;
//    }
//
//    public Integer getAge() {
//        return age;
//    }
//
//    public void setAge(Integer age) {
//        this.age = age;
//    }
//
//    public Double getWeight() {
//        return weight;
//    }
//
//    public void setWeight(Double weight) {
//        this.weight = weight;
//    }
//
//    public String getAvatarUrl() {
//        return avatarUrl;
//    }
//
//    public void setAvatarUrl(String avatarUrl) {
//        this.avatarUrl = avatarUrl;
//    }
//
//    public Long getPetOwnerId() {
//        return petOwnerId;
//    }
//
//    public void setPetOwnerId(Long petOwnerId) {
//        this.petOwnerId = petOwnerId;
//    }
//}
//

package com.petconnect.backend.dto;

public class PetDTO {

    private Long petId;
    private String petName;
    private Integer age;
    private Double weight;
//    private String avatarUrl;
    private String avatarPublicId;  // Add this
    private String ownerFirstName;
    private String ownerLastName;
    private String ownerMobileNumber;

    public PetDTO() {
    }

    public PetDTO(Long petId, String petName, Integer age, Double weight, String avatarUrl, String avatarPublicId, String ownerFirstName, String ownerLastName, String ownerMobileNumber) {
        this.petId = petId;
        this.petName = petName;
        this.age = age;
        this.weight = weight;
//        this.avatarUrl = avatarUrl;
//        this.avatarPublicId = avatarPublicId;
        this.ownerFirstName = ownerFirstName;
        this.ownerLastName = ownerLastName;
        this.ownerMobileNumber = ownerMobileNumber;
    }

//    public String getAvatarPublicId() {
//        return avatarPublicId;
//    }
//
//    public void setAvatarPublicId(String avatarPublicId) {
//        this.avatarPublicId = avatarPublicId;
//    }

    // Getters and Setters
    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

//    public String getAvatarUrl() {
//        return avatarUrl;
//    }
//
//    public void setAvatarUrl(String avatarUrl) {
//        this.avatarUrl = avatarUrl;
//    }

    public String getOwnerFirstName() {
        return ownerFirstName;
    }

    public void setOwnerFirstName(String ownerFirstName) {
        this.ownerFirstName = ownerFirstName;
    }

    public String getOwnerLastName() {
        return ownerLastName;
    }

    public void setOwnerLastName(String ownerLastName) {
        this.ownerLastName = ownerLastName;
    }

    public String getOwnerMobileNumber() {
        return ownerMobileNumber;
    }

    public void setOwnerMobileNumber(String ownerMobileNumber) {
        this.ownerMobileNumber = ownerMobileNumber;
    }
}
