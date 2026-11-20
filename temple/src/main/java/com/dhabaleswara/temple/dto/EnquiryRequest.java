package com.dhabaleswara.temple.dto;

import jakarta.validation.constraints.*;

public class EnquiryRequest {
    @NotBlank @Size(max = 100) private String name;
    @NotBlank @Email @Size(max = 150) private String email;
    @Pattern(regexp = "^[0-9+()\\-\\s]{0,30}$", message = "Invalid phone number")
    private String phone;
    @NotBlank @Size(max = 60) private String subject;
    @NotBlank @Size(max = 2000) private String message;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
