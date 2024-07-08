package com.library.controller;

import com.library.dto.book.BookCreateRequestDTO;
import com.library.dto.book.BookResponseDTO;
import com.library.dto.book.BookUpdateRequestDTO;
import com.library.exceptions.ServiceException;
import com.library.service.book.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {
    private BookService bookService;
    @Autowired
    public BookController(BookService bookService){
        this.bookService = bookService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<BookResponseDTO> create (@RequestBody BookCreateRequestDTO newBook) throws ServiceException {
        BookResponseDTO response = bookService.save(newBook);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','STUDENT')")
    @GetMapping("/findAll")
    public ResponseEntity<List<BookResponseDTO>> findAll(){
        return ResponseEntity.ok(bookService.findCatalog());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{bookCode}")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable String bookCode, @RequestBody BookUpdateRequestDTO update) throws ServiceException{
        return ResponseEntity.ok(bookService.update(bookCode,update));
    }

}
