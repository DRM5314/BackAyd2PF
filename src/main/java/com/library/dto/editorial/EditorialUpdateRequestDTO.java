package com.library.dto.editorial;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EditorialUpdateRequestDTO {
    private Long id;
    private String name;
}
