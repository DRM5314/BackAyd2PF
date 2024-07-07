package library.service.controller;

import com.library.controller.LoanController;
import com.library.controller.exceptionhandler.GlobalExceptionHandler;
import com.library.dto.loan.LoanCreateRequestDTO;
import com.library.dto.loan.LoanResponseDTO;
import com.library.enums.LoanEnum;
import com.library.exceptions.LimitBookLoanStudent;
import com.library.exceptions.NotFoundException;
import com.library.exceptions.StudentInactive;
import com.library.model.*;
import com.library.service.loan.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
@ContextConfiguration (classes = {LoanController.class, LoanService.class, GlobalExceptionHandler.class})
public class LoanControllerTest extends AbstractMvcTest{
    @MockBean
    LoanService loanService;

    private Loan LOAN;
    private final Long ID_LOAN = 1l;
    private final LocalDate LAON_DATA = LocalDate.now();
    private final LocalDate RETURN_DATA = LocalDate.now().plusDays(3);
    private final LoanEnum STATE = LoanEnum.borrowed;
    private Book BOOK;
    private final Long ID_BOOK = 1L;
    private final String CODE_BOOK = "CODE BOOK";
    private final String TITLE = "TITLE";
    private final String AUTH = "AUTHOR";
    private final Integer QUANTITY = 10;
    private LocalDate DATE_PUBLICATION = LocalDate.now();

    private Editorial EDITORIAL;
    private Long  EDITORIAL_ID = 1L;
    private String EDITORIAL_NAME = "EDITORIAL 1";
    private Student STUDENT;

    private Long ID_STUDENT = 1L;
    private String NAME = "DAVID";
    private LocalDate DATE_BIRD = LocalDate.now();
    private String CARNET = "201632145";
    private Integer STATUS = 1;
    private Career CAREER;
    private Long CARRER_ID = 1L;
    private String CARRER_NAME = "CARRER";
    private final Double NO_FEE = 0.0;

    @BeforeEach
    void setUp() {
        EDITORIAL = new Editorial();
        EDITORIAL.setId(EDITORIAL_ID);
        EDITORIAL.setName(EDITORIAL_NAME);


        BOOK = new Book();
        BOOK.setId(ID_BOOK);
        BOOK.setCode(CODE_BOOK);
        BOOK.setTitle(TITLE);
        BOOK.setAuth(AUTH);
        BOOK.setQuantity(QUANTITY);
        BOOK.setDatePublication(DATE_PUBLICATION);
        BOOK.setIdEditorial(EDITORIAL);

        CAREER = new Career();
        CAREER.setId(CARRER_ID);
        CAREER.setName(CARRER_NAME);

        STUDENT = new Student();
        STUDENT.setId(ID_STUDENT);
        STUDENT.setName(NAME);
        STUDENT.setIdCareer(CAREER);
        STUDENT.setDteBirth(DATE_BIRD);
        STUDENT.setCarnet(CARNET);
        STUDENT.setStatus(STATUS);

        LOAN = new Loan();
        LOAN.setId(ID_LOAN);
        LOAN.setBookCode(BOOK);
        LOAN.setCarnet(STUDENT);
        LOAN.setLaonDate(LAON_DATA);
        LOAN.setReturnDate(RETURN_DATA);
        LOAN.setState(STATE);
        LOAN.setLoan_fee(NO_FEE);
        LOAN.setPenalized_fee(NO_FEE);
        LOAN.setSanction_fee(NO_FEE);
    }
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void loanInactiveUser() throws Exception{
        Mockito.doThrow(StudentInactive.class).when(loanService).save(any(LoanCreateRequestDTO.class));
        mockMvc.perform(post("/loan")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new LoanCreateRequestDTO(CODE_BOOK,CARNET,LAON_DATA,STATE))))
                .andExpect(status().isNotAcceptable())
        ;
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void loanLimitBookStudent() throws Exception{
        Mockito.doThrow(LimitBookLoanStudent.class).when(loanService).save(any(LoanCreateRequestDTO.class));
        mockMvc.perform(post("/loan")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new LoanCreateRequestDTO(CODE_BOOK,CARNET,LAON_DATA,STATE))))
                .andExpect(status().isNotAcceptable());
    }
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void loan() throws Exception{
        LoanResponseDTO expected = new LoanResponseDTO(LOAN);
        when(loanService.save(any(LoanCreateRequestDTO.class))).thenReturn(expected);
        mockMvc.perform(post("/loan")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new LoanCreateRequestDTO(CODE_BOOK,CARNET,LAON_DATA,STATE))))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    LoanResponseDTO response = objectMapper.readValue(result.getResponse().getContentAsString(),LoanResponseDTO.class);
                    assertThat(response).isEqualToComparingFieldByFieldRecursively(expected);
                })
        ;
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateNotExist() throws Exception {
        Mockito.doThrow(NotFoundException.class).when(loanService).update(any(Long.class),any(LoanEnum.class));
        mockMvc.perform(put("/loan/"+ID_LOAN+"/"+STATE)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new LoanCreateRequestDTO(CODE_BOOK,CARNET,LAON_DATA,STATE))))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateSuccessful() throws Exception{
        LoanEnum state = LoanEnum.penalized;
        LOAN.setState(state);

        when(loanService.update(any(Long.class),any(LoanEnum.class))).thenReturn(new LoanResponseDTO(LOAN));
        mockMvc.perform(put("/loan/"+ID_LOAN+"/"+state)
                        .with(csrf())
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    LoanResponseDTO response = objectMapper.readValue(result.getResponse().getContentAsString(),LoanResponseDTO.class);
                    assertThat(response).isEqualToComparingFieldByFieldRecursively(new LoanResponseDTO(LOAN));
                });
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN","STUDENT"})
    void findByIdNotFound() throws Exception{
        Mockito.doThrow(NotFoundException.class).when(loanService).findByCodeDto(any(Long.class));
        mockMvc.perform(get("/loan/"+ID_LOAN))
                .andExpect(status().isNotFound());
    }
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN","STUDENT"})
    void findById() throws Exception{
        when(loanService.findByCodeDto(any(Long.class))).thenReturn(new LoanResponseDTO(LOAN));
        mockMvc.perform(get("/loan/"+ID_LOAN)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    LoanResponseDTO response = objectMapper.readValue(result.getResponse().getContentAsString(),LoanResponseDTO.class);
                    assertThat(response).isEqualToComparingFieldByFieldRecursively(new LoanResponseDTO(LOAN));
                });
    }
}
