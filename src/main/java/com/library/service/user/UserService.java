package com.library.service.user;

import com.library.dto.user.UserCreateRequestDTO;
import com.library.dto.user.UserResponseDto;
import com.library.exceptions.ServiceException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface UserService {
    UserResponseDto save(UserCreateRequestDTO saveUser) throws ServiceException;
    UserResponseDto findUserByUserName(String name) throws ServiceException;
    UserDetailsService userDetailsService()throws IOException;
//    List<UserResponseDto> findAll();
//    void deleteUser(Long id);
}
