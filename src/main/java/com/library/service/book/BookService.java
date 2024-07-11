package com.library.service.book;

import com.library.dto.book.BookCreateRequestDTO;
import com.library.dto.book.BookResponseDTO;
import com.library.dto.book.BookUpdateRequestDTO;
import com.library.exceptions.ServiceException;
import com.library.model.Book;

import java.util.List;

public interface BookService {
    BookResponseDTO save(BookCreateRequestDTO userEntry) throws ServiceException;
    BookResponseDTO update(String code, BookUpdateRequestDTO update) throws ServiceException;
    BookResponseDTO findByCode(String code) throws ServiceException;
    Book findByCodeNotDTO(String code) throws ServiceException;
    List<BookResponseDTO> findCatalog();
    List<BookResponseDTO> reportBookStock(Integer quantity);
    List<BookResponseDTO> findNotLoans();
    Book updateReturn(Book book) ;
}
