package com.library.service.security;

import com.library.dto.*;
import com.library.dto.user.SignInRequestDTO;
import com.library.dto.user.UserCreateRequestDTO;
import com.library.dto.user.UserResponseDto;
import com.library.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.library.exceptions.ServiceException;

@Service
public class AuthenticationServiceImpl implements AuthenticationService{
    private final UserService userService;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Autowired
    public AuthenticationServiceImpl(UserService userService,JwtService jwtService, AuthenticationManager authenticationManager){
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }
    @Override
    public JwtAuthenticationResponse signup(UserCreateRequestDTO request) throws ServiceException{
        UserResponseDto user = userService.save(request);
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    @Override
    public JwtAuthenticationResponse signin(SignInRequestDTO request) throws ServiceException{
        UserResponseDto user = userService.findUserByUserName(request.getUser());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUser(),request.getPassword())
        );
        String jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }
}
