package com.ogamex.accounts_import_service.dto;

public class AccountDTO {
    private String email;
    private String password;

    public AccountDTO() { }           // <-- obrigatório
    public AccountDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
