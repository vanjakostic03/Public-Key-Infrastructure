package com.ftn.pki.entities.organizations;

import com.ftn.pki.entities.users.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Organization {
    private UUID id;
    private String name;
    private String description;
    private User CAUser;
}
