package com.library.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class JwtAuthenticationResponse {
    private String token;
}
