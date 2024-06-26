package com.library.service.security;

import com.library.dto.user.UserResponseDto;
import com.library.exceptions.ServiceException;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUserName(String token);
    String generateToken (UserResponseDto user) throws ServiceException;
    boolean isTokenValid(String token, UserDetails userDetails);
}
