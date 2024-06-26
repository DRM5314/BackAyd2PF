package com.library.dto.user;

import com.library.enums.Rol;
import lombok.*;
@AllArgsConstructor
@Getter
public class UserCreateRequestDTO {
    private final Rol role;
    private final String name;
    private final String email;
    private final String userName;
    private final String password;
}
