package com.library.dto.book;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class BookCreateRequestDTO {
    private final String code;
    private final String title;
    private final String auth;
    private final Integer quantity;
    private final LocalDate datePublication;
    private final Long idEditorial;
}
