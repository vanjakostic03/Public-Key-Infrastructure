package com.ftn.pki.services.users;

import com.ftn.pki.dtos.users.CreateUserDTO;
import com.ftn.pki.dtos.users.CreatedUserDTO;
import com.ftn.pki.dtos.users.SimpleUserDTO;
import com.ftn.pki.entities.users.User;
import com.ftn.pki.entities.users.UserType;
import com.ftn.pki.mappers.users.UserMapper;
import com.ftn.pki.repositories.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    public SimpleUserDTO findOne(UUID id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return null;

        return userMapper.fromUserToSimpleUserDTO(user);
    }

    public List<SimpleUserDTO> findAll() {
        List<User> users = userRepository.findAll();
        return userMapper.fromUserListToSimpleUserDTOList(users);
    }

    public List<SimpleUserDTO> findByType(UserType type) {
        List<User> users = userRepository.findByType(type);

        return userMapper.fromUserListToSimpleUserDTOList(users);
    }

    public CreatedUserDTO create(CreateUserDTO userDTO) {
        User user = userMapper.fromCreateUserDTOToUser(userDTO);
        this.save(user);
        return userMapper.fromUserToCreatedUserDTO(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

}
