package com.example.springproject.dto;

import com.example.springproject.helper.AES;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class MyUSer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int otp;
    private boolean verified;

    @Size(min = 3, max = 20, message = "*Enter Between 3 and 20 characters")
    private String name;

    @Email(message = "*Enter Proper Email format")
    @NotNull(message = "*This is Required")
    private String email;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "*Enter more than 8 characters consisting of one upper case,one lower case and one special characters")
    private String password;

    public void setPassword(String password) {
      
        this.password=AES.encrypt(password, "123");
    }

    public String getPassword() {
       
        return AES.decrypt(this.password,"123");
    }

    
}
