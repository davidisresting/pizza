package com.example.awesomepizza.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity(name = "Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(unique = true)
    private String email;

    private String mobileNumber;
    private byte[] storedHash;
    private byte[] storedSalt;

    public User(String email, String mobileNumber) {
        this.email = email;
        this.mobileNumber = mobileNumber;
    }
}
