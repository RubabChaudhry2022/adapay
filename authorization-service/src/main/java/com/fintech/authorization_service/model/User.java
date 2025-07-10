package com.fintech.authorization_service.model;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   
    private String name;
    private String pass;
    @Pattern(regexp = "^[A-Za-z0-9_]+@[a-z]+\\.(com|org)$",
    		message = "Email must be in the format name@domain.com or name@domain.org with no digits and capital letters in the domain")
    private String email;
    private boolean isAdmin;
    // Getters & Setters
    public Long getUserId() { return id; }
    public void setUserId(Long id) { this.id = id; }

    
    
    @Pattern(regexp = "^\\d{11}$", message = "Phone number must be 11 digits")
    @NotBlank(message = "Phone number is required")
    private String number;
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPass() { return pass; }
    public void setPass(String pass) { this.pass = pass; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }
   
}
