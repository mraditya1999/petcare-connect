package com.petconnect.backend.dto.user;

public class GoogleUserDTO {
    private String sub;
    private String email;
    private String given_name;
    private String family_name;
    private String picture;

    public GoogleUserDTO() {
    }

    public GoogleUserDTO(String sub, String email, String given_name, String family_name, String picture) {
        this.sub = sub;
        this.email = email;
        this.given_name = given_name;
        this.family_name = family_name;
        this.picture = picture;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGiven_name() {
        return given_name;
    }

    public void setGiven_name(String given_name) {
        this.given_name = given_name;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
