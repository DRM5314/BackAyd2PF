package com.library.controller;

import com.library.dto.user.UserCreateRequestDTO;
import com.library.dto.user.UserResponseDto;
import com.library.exceptions.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.library.service.user.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;
    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody UserCreateRequestDTO newUser) throws ServiceException {
        UserResponseDto response = userService.save(newUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userName}")
    public ResponseEntity<UserResponseDto> findByUserName(@PathVariable String userName) throws ServiceException{
        UserResponseDto response = userService.findUserByUserName(userName);
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/findAll")
//    public ResponseEntity<List<UserResponseDto>> findAll(){
//        List<UserResponseDto> response = userService.findAll();
//        return ResponseEntity.ok(response);
//    }

}
