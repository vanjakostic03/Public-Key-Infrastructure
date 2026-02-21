package com.ftn.pki.repositories.users;

import com.ftn.pki.entities.users.User;
import com.ftn.pki.entities.users.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserRepository extends JpaRepository<User, UUID> {

    List<User> findByUserType(UserType type);

    Optional<User> findByEmail(String email);
}
