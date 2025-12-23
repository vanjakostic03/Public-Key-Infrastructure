package com.ftn.pki.dtos.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class SimpleUserDTO {
    private String name;
    private String surname;
    private String email;
}
