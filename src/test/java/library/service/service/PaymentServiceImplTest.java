package library.service.service;

import com.library.dto.loan.LoanCreateRequestDTO;
import com.library.dto.payment.PaymentCreateRequestDTO;
import com.library.dto.payment.PaymentResponseDto;
import com.library.enums.LoanEnum;
import com.library.enums.PaymentEnum;
import com.library.exceptions.NotFoundException;
import com.library.exceptions.ServiceException;
import com.library.model.*;
import com.library.repository.PaymentRepository;
import com.library.service.loan.LoanService;
import com.library.service.loan.LoanServiceImpl;
import com.library.service.payment.PaymentServiceImpl;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;

import static org.mockito.Mockito.when;
import java.util.List;

public class PaymentServiceImplTest {
    private LoanService loanService = Mockito.mock(LoanService.class);
    private PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);

    private PaymentServiceImpl paymentService;
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
    private Payment PAYMENT;
    private final Long ID_PAYMENT = 1L;
    private final PaymentEnum type = PaymentEnum.normal;
    private final Double TOTAL = 100.0;
    private final LocalDate DATE_PAYMENT = LocalDate.parse("2024-01-01");



    @BeforeEach
    void setUp() {
         paymentService = new PaymentServiceImpl(paymentRepository, loanService);

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
        PAYMENT.setType(type);
        PAYMENT.setTotal(TOTAL);
        PAYMENT.setDatePayment(DATE_PAYMENT);
    }

    @Test
    void saveSuccessful() throws ServiceException {
        PaymentCreateRequestDTO request = new PaymentCreateRequestDTO(ID_LOAN, type, TOTAL, DATE_PAYMENT);
        when(loanService.findByCodeNotDto(ID_LOAN)).thenReturn(LOAN);
        when(paymentRepository.save(Mockito.any(Payment.class))).thenReturn(PAYMENT);
        PaymentResponseDto expected = new PaymentResponseDto(PAYMENT);
        PaymentResponseDto actually = paymentService.save(request);
        assertThat(expected).isEqualToComparingFieldByFieldRecursively(actually);
    }

    @Test
    void findByIdNotExist(){
        when(paymentRepository.findById(ID_PAYMENT)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> paymentService.findById(ID_PAYMENT));
    }
    @Test
    void findByIdSuccessful() throws ServiceException {
        when(paymentRepository.findById(ID_PAYMENT)).thenReturn(Optional.of(PAYMENT));
        PaymentResponseDto expected = new PaymentResponseDto(PAYMENT);
        PaymentResponseDto actually = paymentService.findById(ID_PAYMENT);
        assertThat(expected).isEqualToComparingFieldByFieldRecursively(actually);
    }
    @Test
    void findByStateAndDate() {
        PAYMENT.setType(PaymentEnum.sanction);
        when(paymentRepository.findAllByTypeAndDatePaymentBetween(type, DATE_PAYMENT, DATE_PAYMENT)).thenReturn(List.of(PAYMENT));
        List<PaymentResponseDto> expected = List.of(new PaymentResponseDto(PAYMENT));
        List<PaymentResponseDto> actually = paymentService.findAllByTypeAndDate(type, DATE_PAYMENT, DATE_PAYMENT);
        assertThat(actually.size()).isEqualTo(expected.size());
        for (int i = 0; i < actually.size(); i++) {
            assertThat(expected.get(i)).isEqualToComparingFieldByFieldRecursively(actually.get(i));
            assertThat(expected.get(i).getType()).isEqualTo(PaymentEnum.sanction);
        }
    }
    @Test
    void findByState() {
        PAYMENT.setType(PaymentEnum.sanction);
        when(paymentRepository.findAllByType(type)).thenReturn(List.of(PAYMENT));
        List<PaymentResponseDto> expected = List.of(new PaymentResponseDto(PAYMENT));
        List<PaymentResponseDto> actually = paymentService.findAllByType(type);
        assertThat(actually.size()).isEqualTo(expected.size());
        for (int i = 0; i < actually.size(); i++) {
            assertThat(expected.get(i)).isEqualToComparingFieldByFieldRecursively(actually.get(i));
            assertThat(expected.get(i).getType()).isEqualTo(PaymentEnum.sanction);
        }
    }
}
