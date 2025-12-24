package com.ftn.pki.controllers.users;

import com.ftn.pki.dtos.users.CreateUserDTO;
import com.ftn.pki.dtos.users.CreatedUserDTO;
import com.ftn.pki.dtos.users.SimpleUserDTO;
import com.ftn.pki.services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<SimpleUserDTO>> getUsers(){
        List<SimpleUserDTO> users = userService.findAll();
        if(users == null){
            return new ResponseEntity<Collection<SimpleUserDTO>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleUserDTO> getUser(@PathVariable UUID id){
        SimpleUserDTO user = userService.findOne(id);
        if(user == null){
            return new ResponseEntity<SimpleUserDTO>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedUserDTO> createUser(@RequestBody CreateUserDTO userDTO) {
        return new ResponseEntity<>(userService.create(userDTO), HttpStatus.CREATED);
    }

}
