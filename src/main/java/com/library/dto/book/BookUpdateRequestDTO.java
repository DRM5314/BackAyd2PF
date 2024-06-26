package com.library.dto.book;

import com.library.model.Editorial;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BookUpdateRequestDTO {
    private Long id;
    private String title;
    private String auth;
    private Integer quantity;
    private Long idEditorial;
}
