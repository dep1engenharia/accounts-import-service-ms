package com.ogamex.accounts_import_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailDTO {
    @Email
    @NotBlank
    private String email;
    @SuppressWarnings("unused")
    public EmailDTO() { }

    @SuppressWarnings("unused")
    public EmailDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
    @SuppressWarnings("unused")
    public void setEmail(String email) {
        this.email = email;
    }
}