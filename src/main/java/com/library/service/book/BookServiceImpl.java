package com.library.service.book;

import com.library.dto.book.BookCreateRequestDTO;
import com.library.dto.book.BookResponseDTO;
import com.library.dto.book.BookUpdateRequestDTO;
import com.library.exceptions.DuplicatedEntityException;
import com.library.exceptions.NotFoundException;
import com.library.exceptions.ServiceException;
import com.library.model.Book;
import com.library.model.Editorial;
import com.library.repository.BookRepository;
import com.library.service.editorial.EditorialService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Service
public class BookServiceImpl implements BookService{
    private BookRepository bookRepository;
    private EditorialService editorialService;
    @Autowired
    public BookServiceImpl(BookRepository bookRepository,EditorialService editorialService){
        this.bookRepository = bookRepository;
        this.editorialService = editorialService;
    }

    @Override
    public BookResponseDTO save(BookCreateRequestDTO bookEntry) throws ServiceException {
        Optional<Book> exist = bookRepository.findByCode(bookEntry.getCode());
        if(exist.isPresent()){
            throw new DuplicatedEntityException(String.format("Book with code: %s, already exist!",bookEntry.getCode()));
        }
        if(bookRepository.existsByTitle(bookEntry.getTitle())){
            throw new DuplicatedEntityException(String.format("Book with name: %s, already exist!",bookEntry.getTitle()));
        }
        Book bookSave = new Book();
        bookSave.setCode(bookEntry.getCode());
        bookSave.setTitle(bookEntry.getTitle());
        bookSave.setAuth(bookEntry.getAuth());
        bookSave.setQuantity(bookEntry.getQuantity());
        bookSave.setDatePublication(bookEntry.getDatePublication());
        bookSave.setIdEditorial(editorialService.findByCodeNotDto(bookEntry.getIdEditorial()));
        bookSave = bookRepository.save(bookSave);
        return new BookResponseDTO(bookSave);
    }

    @Override
    @Transactional
    public BookResponseDTO update(String code, BookUpdateRequestDTO updateBook) throws ServiceException {
        var conditon = bookRepository.existsByTitleAndCodeIsNot(updateBook.getTitle(),code);
        if(conditon){
            throw new DuplicatedEntityException(String.format("Sorry this book with name: %s, already exist!",updateBook.getTitle()));
        }
        Editorial editorial = editorialService.findByCodeNotDto(updateBook.getIdEditorial());
        Book bookStock = findByCodeNotDTO(code);
        bookStock.setTitle(updateBook.getTitle());
        bookStock.setAuth(updateBook.getAuth());
        bookStock.setQuantity(updateBook.getQuantity());
        bookStock.setIdEditorial(editorial);
        bookStock = bookRepository.save(bookStock);
        return new BookResponseDTO(bookStock);
    }

    @Override
    public BookResponseDTO findByCode(String code) throws ServiceException{
        Book book = findByCodeNotDTO(code);
        return new BookResponseDTO(book);
    }

    @Override
    public Book findByCodeNotDTO(String code) throws ServiceException {
        var returns = bookRepository.findByCode(code).orElseThrow(()->
                new NotFoundException(String.format("Book with code: %s, no exists!",code))
        );
        return returns;
    }

    @Override
    public List<BookResponseDTO> findCatalog() {
        return bookRepository.findAll().stream().map(BookResponseDTO::new).collect(Collectors.toList());
    }

    @Override
    public List<BookResponseDTO> reportBookStock(Integer quantity) {
        return bookRepository.findAllByQuantityLessThanEqual(quantity).stream().map(BookResponseDTO::new).collect(Collectors.toList());
    }

    @Override
    public List<BookResponseDTO> findNotLoans() {
        return bookRepository.findNotLoans().stream().map(BookResponseDTO::new).collect(Collectors.toList());
    }

    @Override
    public Book updateReturn(Book book){
        book.setQuantity(book.getQuantity()+1);
        return bookRepository.save(book);
    }


}
