package com.library.service.security;

import com.library.dto.JwtAuthenticationResponse;
import com.library.dto.user.SignInRequestDTO;
import com.library.dto.user.UserCreateRequestDTO;
import com.library.exceptions.ServiceException;

public interface AuthenticationService {
    JwtAuthenticationResponse signup(UserCreateRequestDTO request) throws ServiceException;

    JwtAuthenticationResponse signin(SignInRequestDTO request) throws ServiceException;
}
