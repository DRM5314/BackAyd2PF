package library.service.service;

import com.library.dto.book.BookCreateRequestDTO;
import com.library.dto.book.BookResponseDTO;
import com.library.dto.book.BookUpdateRequestDTO;
import com.library.exceptions.DuplicatedEntityException;
import com.library.exceptions.NotFoundException;
import com.library.exceptions.ServiceException;
import com.library.model.Book;
import com.library.model.Editorial;
import com.library.repository.BookRepository;
import com.library.service.book.BookServiceImpl;
import com.library.service.editorial.EditorialService;
import com.library.service.editorial.EditorialServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookServiceImplTest {
    private final Long ID = 1L;
    private final String CODE = "CODE BOOK";
    private final String TITLE = "TITLE";
    private final String AUTH = "AUTHOR";
    private final Integer QUANTITY = 1;
    private LocalDate DATE_PUBLICATION = LocalDate.now();

    private Editorial EDITORIAL;
    private Long  EDITORIAL_ID = 1L;
    private String EDITORIAL_NAME = "EDITORIAL 1";


    private BookServiceImpl bookService;
    private BookRepository bookRepository = mock(BookRepository.class);
    private EditorialService editorialService = mock(EditorialServiceImpl.class);
    private Book book;
    private BookCreateRequestDTO bookCreateDto;

    @BeforeEach
    public void SetUp(){
        bookService = new BookServiceImpl(bookRepository, editorialService);
        EDITORIAL = new Editorial();
        EDITORIAL.setId(EDITORIAL_ID);
        EDITORIAL.setName(EDITORIAL_NAME);


        book = new Book();
        book.setId(ID);
        book.setCode(CODE);
        book.setTitle(TITLE);
        book.setAuth(AUTH);
        book.setQuantity(QUANTITY);
        book.setDatePublication(DATE_PUBLICATION);
        book.setIdEditorial(EDITORIAL);

        bookCreateDto = new BookCreateRequestDTO(CODE,TITLE,AUTH,QUANTITY,DATE_PUBLICATION,EDITORIAL_ID);
    }

    @Test
    void save() throws ServiceException {
        when(bookRepository.findByCode(CODE)).thenReturn(Optional.empty());
        when(bookRepository.existsByTitle(TITLE)).thenReturn(false);
        when(editorialService.findByCodeNotDto(EDITORIAL_ID)).thenReturn(EDITORIAL);
        BookResponseDTO expected = new BookResponseDTO(this.book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        BookResponseDTO actually = bookService.save(bookCreateDto);
        assertThat(expected).isEqualToComparingFieldByFieldRecursively(actually);
    }

    @Test
    public void saveWithCodeExistError(){
        when(bookRepository.findByCode(CODE)).thenReturn(Optional.ofNullable(this.book));
        assertThrows(DuplicatedEntityException.class,() -> bookService.save(bookCreateDto));
    }

    @Test
    public void saveWithTitleExistError(){
        when(bookRepository.existsByTitle(TITLE)).thenReturn(true);
        assertThrows(DuplicatedEntityException.class,() -> bookService.save(bookCreateDto));
    }
    @Test
    void findByCodeDto() throws ServiceException{
        this.book.setIdEditorial(this.EDITORIAL);

        when(bookRepository.findByCode(CODE)).thenReturn(Optional.of(this.book));
        BookResponseDTO expected = new BookResponseDTO(this.book);

        BookResponseDTO actually = bookService.findByCode(CODE);
        assertThat(actually).isEqualToComparingFieldByFieldRecursively(expected);
    }
    @Test
    void findByCodeNotDto() throws ServiceException{
        this.book.setIdEditorial(this.EDITORIAL);

        when(bookRepository.findByCode(CODE)).thenReturn(Optional.of(this.book));

        Book actually = bookService.findByCodeNotDTO(CODE);
        assertThat(actually).isEqualToComparingFieldByFieldRecursively(this.book);
    }

    @Test
    public void  findByCodeWithNotExist(){
        when(bookRepository.findByCode(CODE)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,() -> bookService.findByCode(CODE));
    }
    @Test
    void findCatalog() {
        List<Book> response = mock(List.class);
        when(bookRepository.findAll()).thenReturn(response);
        List<BookResponseDTO> actually = bookService.findCatalog();
        assertThat(actually).doesNotContainNull();
    }

    @Test
    void bookUpdate() throws ServiceException{
        when(bookRepository.existsByTitleAndCodeIsNot(TITLE,CODE)).thenReturn(false);
        when(bookRepository.findByCode(CODE)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(editorialService.findByCodeNotDto(EDITORIAL_ID)).thenReturn(EDITORIAL);

        book.setQuantity(20);
        BookResponseDTO expected = new BookResponseDTO(book);
        book.setQuantity(0);
        BookUpdateRequestDTO updateDto = new BookUpdateRequestDTO(ID,TITLE,AUTH,20,EDITORIAL_ID);
        BookResponseDTO actually = bookService.update(CODE,updateDto);
        assertThat(actually).isEqualToComparingFieldByFieldRecursively(expected);
        book.setQuantity(QUANTITY);
    }

    @Test
    void bookUupdateWithNameExist() throws ServiceException{
        when(bookRepository.existsByTitleAndCodeIsNot(TITLE,CODE)).thenReturn(true);
        BookUpdateRequestDTO updateDto = new BookUpdateRequestDTO(ID,TITLE,AUTH,20,EDITORIAL_ID);
        assertThrows(DuplicatedEntityException.class,()->bookService.update(CODE,updateDto));
    }

}