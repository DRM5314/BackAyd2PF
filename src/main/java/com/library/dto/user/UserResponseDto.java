package com.library.dto.user;

import com.library.enums.Rol;
import com.library.model.User;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private Long userId;
    private String name;
    private Rol role;
    private String email;
    private String username;
    private int status;

    public UserResponseDto(User userEntity){
        this.userId = userEntity.getId();
        this.name = userEntity.getName();
        this.role = userEntity.getRole();
        this.email = userEntity.getEmail();
        this.username = userEntity.getUsername();
        this.status = userEntity.getStatus();
    }
}
