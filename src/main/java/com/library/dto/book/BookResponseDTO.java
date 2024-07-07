package com.library.dto.book;

import com.library.model.Book;
import com.library.model.Editorial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.time.LocalDate;
@Getter
@AllArgsConstructor
public class BookResponseDTO {
    private String code;
    private String title;
    private String auth;
    private Integer quantity;
    private LocalDate datePublication;
    private Editorial idEditorial;

    public BookResponseDTO(Book book){
        this.code = book.getCode();
        this.title = book.getTitle();
        this.auth = book.getAuth()      ;
        this.quantity = book.getQuantity();
        this.datePublication = book.getDatePublication();
        this.idEditorial = book.getIdEditorial();
    }
}
