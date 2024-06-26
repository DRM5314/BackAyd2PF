package com.library.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequestDTO {
    private String user;
    private String password;
}
