package com.library.controller;

import com.library.dto.JwtAuthenticationResponse;
import com.library.dto.user.SignInRequestDTO;
import com.library.dto.user.UserCreateRequestDTO;
import com.library.service.security.AuthenticationService;
import com.library.exceptions.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("v1/auth")
public class AuthController {
    private final AuthenticationService authenticationService;
    @Autowired
    public AuthController(AuthenticationService authenticationService){
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> signUp(@RequestBody UserCreateRequestDTO newUSer) throws IOException, ServiceException{
        var token = authenticationService.signup(newUSer);
        var headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token.getToken());
        ResponseEntity returns = ResponseEntity.ok().headers(headers).build();
        return returns;
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> getToken(@RequestBody SignInRequestDTO credentials)throws ServiceException{
        System.out.println(credentials);
        var token = authenticationService.signin(credentials);
        var headers = new HttpHeaders();
        System.out.println(token);
        headers.add("Authorization", "Bearer " + token.getToken());
        ResponseEntity returns = ResponseEntity.ok().headers(headers).build();
        return returns;
    }


}
