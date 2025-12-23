package com.ftn.pki.mappers.users;

import com.ftn.pki.dtos.users.CreateUserDTO;
import com.ftn.pki.dtos.users.CreatedUserDTO;
import com.ftn.pki.dtos.users.SimpleUserDTO;
import com.ftn.pki.entities.users.User;
import com.ftn.pki.entities.users.UserType;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor

@Component
public class UserMapper {

    public User fromCreateUserDTOToUser(CreateUserDTO userDTO){
        User user = new User();

        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());
        user.setUserType(UserType.REGULAR);

        return user;
    }

    public CreatedUserDTO fromUserToCreatedUserDTO(User user){
        CreatedUserDTO createdUserDTO = new CreatedUserDTO();
        createdUserDTO.setId(user.getId());
        createdUserDTO.setName(user.getName());
        createdUserDTO.setSurname(user.getSurname());
        createdUserDTO.setPassword(user.getPassword());
        createdUserDTO.setEmail(user.getEmail());
        createdUserDTO.setUserType(UserType.REGULAR);
        return createdUserDTO;
    }

    public SimpleUserDTO fromUserToSimpleUserDTO(User user) {
        SimpleUserDTO result = new SimpleUserDTO();
        result.setEmail(user.getEmail());
        result.setName(user.getName());
        result.setSurname(user.getSurname());

        return result;
    }

    public List<SimpleUserDTO> fromUserListToSimpleUserDTOList(List<User> users) {
        List<SimpleUserDTO> userDTOs = new ArrayList<>();
        if(users != null){
            for (User user : users) {
                userDTOs.add(fromUserToSimpleUserDTO(user));
            }
        }
        return userDTOs;
    }



}
