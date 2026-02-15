package com.ftn.pki.entities.organizations;

import com.ftn.pki.entities.users.User;
import jakarta.persistence.Column;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Organization {
    private UUID id;
    private String name;
    private String description;
    private User CAUser;
    private String encKey;              //dek
    private String keyIv;


}
