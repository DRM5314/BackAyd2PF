package library.service.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.library.controller.BookController;
import com.library.controller.exceptionhandler.GlobalExceptionHandler;
import com.library.dto.book.BookCreateRequestDTO;
import com.library.dto.book.BookResponseDTO;
import com.library.dto.book.BookUpdateRequestDTO;
import com.library.exceptions.DuplicatedEntityException;
import com.library.exceptions.NotFoundException;
import com.library.exceptions.ServiceException;
import com.library.model.Book;
import com.library.model.Editorial;
import com.library.service.book.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

@ContextConfiguration (classes = {BookController.class, BookService.class, GlobalExceptionHandler.class})
public class BookControllerTest extends AbstractMvcTest {
    private final String CODE = "CO1";
    private final String TITLE = "Title1";
    private final String AUTH = "auth1";
    private final Integer QUANTITY = 1;
    private final LocalDate DATE_PUBLICATION = LocalDate.parse("2021-01-01");
    private final Long ID_EDITORIAL = 1L;
    private final String EDITORIAL_NAME = "Editorial1";
    private Book BOOK;
    private Editorial EDITORIAL;

    @MockBean
    private BookService bookService;
    @BeforeEach
    public void setUp(){
        this.EDITORIAL = new Editorial();
        this.EDITORIAL.setName(EDITORIAL_NAME);
        this.EDITORIAL.setId(ID_EDITORIAL);

        this.BOOK = new Book();
        this.BOOK.setId(1L);
        this.BOOK.setCode(CODE);
        this.BOOK.setTitle(TITLE);
        this.BOOK.setAuth(AUTH);
        this.BOOK.setQuantity(QUANTITY);
        this.BOOK.setDatePublication(DATE_PUBLICATION);
        this.BOOK.setIdEditorial(EDITORIAL);

    }

    @Test
    @WithMockUser("ADMIN")
    void testCreateRepeated() throws Exception{
        Mockito.doThrow(DuplicatedEntityException.class).when(bookService).save(any(BookCreateRequestDTO.class));
        mockMvc.perform(post("/book")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new BookCreateRequestDTO(CODE, TITLE, AUTH, QUANTITY, DATE_PUBLICATION, ID_EDITORIAL)))
        )
                .andExpect(status().isBadRequest());
    }
    @Test
    @WithMockUser("ADMIN")
    public void testCreate() throws ServiceException, Exception {
        BookResponseDTO expected = new BookResponseDTO(BOOK);

        BookCreateRequestDTO bookRequest = new BookCreateRequestDTO(CODE, TITLE, AUTH, QUANTITY, DATE_PUBLICATION, ID_EDITORIAL);
        when(bookService.save(any(BookCreateRequestDTO.class))).thenReturn(expected);

        String response = mockMvc.perform(
          post("/book")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(bookRequest))
        )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString()
                ;
        BookResponseDTO actual = objectMapper.readValue(response,BookResponseDTO.class);
    }

    @Test
    @WithMockUser(username = "david", authorities = {"STUDENT"})
    public void testCreateUserStudent() throws Exception{
        BookResponseDTO expected = new BookResponseDTO(BOOK);

        BookCreateRequestDTO bookRequest = new BookCreateRequestDTO(CODE, TITLE, AUTH, QUANTITY, DATE_PUBLICATION, ID_EDITORIAL);
        when(bookService.save(any(BookCreateRequestDTO.class))).thenReturn(expected);
        mockMvc.perform(
                post("/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
        )
                .andExpect(status().isForbidden())
        ;
    }

    @Test
    @WithMockUser(username = "david", authorities = {"ADMIN"})
    public void testCreateSuccessful() throws Exception {
        BookResponseDTO expected = new BookResponseDTO(BOOK);

        BookCreateRequestDTO bookRequest = new BookCreateRequestDTO(CODE, TITLE, AUTH, QUANTITY, DATE_PUBLICATION, ID_EDITORIAL);
        when(bookService.save(any(BookCreateRequestDTO.class))).thenReturn(expected);

        mockMvc.perform(
                        post("/book")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(bookRequest))
                                .with(csrf())
                )
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    BookResponseDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookResponseDTO.class);
                    assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
                })
                ;
    }

    @Test
    @WithMockUser(roles = {"ADMIN","STUDENT"})
    public void testFindAll() throws Exception{
        List<BookResponseDTO> expected = List.of(new BookResponseDTO(BOOK));
        when(bookService.findCatalog()).thenReturn(expected);

        mockMvc.perform(get("/book/findAll"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    List<BookResponseDTO> actual = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<BookResponseDTO>>(){});
                    assertThat(actual.size()).isEqualTo(expected.size());
                    for (int i = 0; i < actual.size(); i++) {
                        assertThat(actual.get(i)).isEqualToComparingFieldByFieldRecursively(expected.get(i));
                    }
                });
    }
    @Test
    @WithMockUser("ADMIN")
    void updateNoExist() throws Exception{
        Mockito.doThrow(NotFoundException.class).when(bookService).update(any(String.class),any(BookUpdateRequestDTO.class));
        mockMvc.perform(put("/book")
                        .with(csrf())
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "david", authorities = {"ADMIN"})
    void updateBook() throws Exception{
        String newTitle = "TITLE UPDATE";
        String newAuth = "AUTH UPDATE";
        Integer newQuantity = 20;
        Long newIdEditorial = 2L;

        EDITORIAL.setId(newIdEditorial);

        BOOK.setTitle(newTitle);
        BOOK.setAuth(newAuth);
        BOOK.setQuantity(newQuantity);
        BOOK.setIdEditorial(EDITORIAL);
        BookUpdateRequestDTO update = new BookUpdateRequestDTO(1L,newTitle, newAuth, newQuantity, 2L);

        BookResponseDTO expected = new BookResponseDTO(BOOK);

        when(bookService.update(any(String.class),any(BookUpdateRequestDTO.class))).thenReturn(expected);

        mockMvc.perform(
                put("/book/{bookCode}", CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update))
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(result -> {
                    BookResponseDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookResponseDTO.class);
                    assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
                });
    }
}
