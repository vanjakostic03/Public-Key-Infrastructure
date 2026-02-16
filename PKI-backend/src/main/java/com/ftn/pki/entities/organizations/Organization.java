package com.ftn.pki.entities.organizations;

import com.ftn.pki.entities.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="organizations")
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ca_user_id", nullable = false)
    private User caUser;
    @Column(nullable = false)
    private String encKey;              //dek
    @Column(nullable = false)
    private String keyIv;


}
