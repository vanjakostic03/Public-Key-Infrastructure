package com.ftn.pki.dtos.users;

import com.ftn.pki.entities.users.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreatedUserDTO {
    private UUID id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private UserType userType;
}
