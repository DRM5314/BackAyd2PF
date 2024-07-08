package library.service.controller;

import com.library.controller.PaymentController;
import com.library.controller.exceptionhandler.GlobalExceptionHandler;
import com.library.dto.loan.LoanCreateRequestDTO;
import com.library.dto.payment.PaymentCreateRequestDTO;
import com.library.dto.payment.PaymentResponseDto;
import com.library.enums.LoanEnum;
import com.library.enums.PaymentEnum;
import com.library.exceptions.NotFoundException;
import com.library.model.*;
import com.library.service.payment.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.*;


@ContextConfiguration (classes = {PaymentController.class, PaymentService.class, GlobalExceptionHandler.class})
public class PaymentControllerTest extends AbstractMvcTest{
    @MockBean
    PaymentService paymentService;
    private Payment PAYMENT;
    private final Long ID_PAYMENT = 1L;
    private final PaymentEnum TYPE_PAYMENT = PaymentEnum.normal;
    private final Double TOTAL = 100.0;
    private final LocalDate DATE_PAYMENT = LocalDate.parse("2024-01-01");
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

        PAYMENT = new Payment();
        PAYMENT.setId(ID_PAYMENT);
        PAYMENT.setLoan(LOAN);
        PAYMENT.setType(TYPE_PAYMENT);
        PAYMENT.setTotal(TOTAL);
        PAYMENT.setDatePayment(DATE_PAYMENT);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void saveWithLoanNotExist() throws Exception{
        PaymentCreateRequestDTO request = new PaymentCreateRequestDTO(ID_LOAN, TYPE_PAYMENT, TOTAL, DATE_PAYMENT);
        PaymentResponseDto expected = new PaymentResponseDto(PAYMENT);
        Mockito.when(paymentService.save(any(PaymentCreateRequestDTO.class))).thenReturn(expected);
        mockMvc.perform(post("/payment")
                .with(csrf())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    PaymentResponseDto actual = objectMapper.readValue(json, PaymentResponseDto.class);
                    assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
                });
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void findByIdNotExist() throws Exception{
        Mockito.doThrow(NotFoundException.class).when(paymentService).findById(any(Long.class));
        mockMvc.perform(get("/payment/{id}", ID_PAYMENT))
                .andExpect(status().isNotFound());
    }
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void findById() throws Exception{
        PaymentResponseDto expected = new PaymentResponseDto(PAYMENT);
        when(paymentService.findById(ID_PAYMENT)).thenReturn(expected);
        mockMvc.perform(get("/payment/{id}", ID_PAYMENT)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    PaymentResponseDto actual = objectMapper.readValue(json, PaymentResponseDto.class);
                    assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
                });
    }
}
