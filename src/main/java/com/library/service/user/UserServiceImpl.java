package com.library.service.user;

import com.library.dto.student.StudentResponseDTO;
import com.library.model.UserInfoDetails;
import com.library.repository.StudentRepository;
import com.library.repository.UserRepository;
import com.library.dto.user.UserCreateRequestDTO;
import com.library.dto.user.UserResponseDto;
import com.library.exceptions.DuplicatedEntityException;
import com.library.exceptions.NotFoundException;
import com.library.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import com.library.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public UserResponseDto save(UserCreateRequestDTO saveUser) throws ServiceException {
        Optional<User> user = userRepository.findByUsername(saveUser.getUserName());
        if (user.isPresent()){
            throw new DuplicatedEntityException(String.format("This user with userName:%s already exists!!",saveUser.getUserName()));
        }

        User newUserEntity = new User();
        newUserEntity.setRole(saveUser.getRole());
        newUserEntity.setName(saveUser.getName());
        newUserEntity.setEmail(saveUser.getEmail());
        newUserEntity.setUsername(saveUser.getUserName());
        newUserEntity.setPassword(passwordEncoder().encode(saveUser.getPassword()));
        newUserEntity.setStatus(1);

        newUserEntity = userRepository.save(newUserEntity);

        return new UserResponseDto(newUserEntity);
    }
    @Override
    public UserResponseDto findUserByUserName(String userName) throws ServiceException {
        //Error
        User userFind = userRepository.findByUsername(userName).orElseThrow(()->
                new NotFoundException(String.format("This user by user name: %s, not exist",userName)));
        return new UserResponseDto(userFind);
    }

    public UserDetailsService userDetailsService(){
        return username -> userRepository.findByUsername(username)
                .map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
//    @Override
//    public List<UserResponseDto> findAll() {
//        return userRepository.findAll().stream().map(UserResponseDto::new).collect(Collectors.toList());
//    }

//    @Override
//    public void deleteUser(Long id) {
//
//    }
}
