package com.ftn.pki.entities.users;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @Pattern(regexp = "^[A-Za-z]+$", message = "Name must contain only alphabetic characters")
    private String name;

    @Column(nullable = false)
    @Pattern(regexp = "^[A-Za-z]+$", message = "Surname must contain only alphabetic characters")
    private String surname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

}
