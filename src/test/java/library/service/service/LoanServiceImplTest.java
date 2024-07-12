package library.service.service;

import com.library.dto.career.CareerResponseDTO;
import com.library.dto.loan.*;
import com.library.dto.payment.PaymentResponseDto;
import com.library.enums.LoanEnum;
import com.library.enums.PaymentEnum;
import com.library.exceptions.LimitBookLoanStudent;
import com.library.exceptions.NotFoundException;
import com.library.exceptions.ServiceException;
import com.library.exceptions.StudentInactive;
import com.library.model.*;
import com.library.repository.LoanRepository;
import com.library.service.ObtainsDateNow;
import com.library.service.book.BookService;
import com.library.service.career.CareerService;
import com.library.service.fee.FeeService;
import com.library.service.loan.LoanServiceImpl;
import com.library.service.payment.PaymentService;
import com.library.service.student.StudentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;

class LoanServiceImplTest {
    private LoanServiceImpl loanService;
    private StudentService studentService = mock(StudentService.class);
    private BookService bookService = mock(BookService.class);
    private LoanRepository loanRepository = mock(LoanRepository.class);
    private FeeService feeService = mock(FeeService.class);
    private PaymentService paymentService = mock(PaymentService.class);
    private CareerService careerService = mock(CareerService.class);
    private ObtainsDateNow dateNowService = mock(ObtainsDateNow.class);

    private SecurityContext securityContext = mock(SecurityContext.class);

    private Authentication authentication = mock(Authentication.class);
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

    //Data for loan actualization fee
    private final LocalDate RETURN_DATA_THREE_DAYS = LocalDate.parse("2024-01-29");
    private final LocalDate RETURN_DATA_PENALIZED_TEN_DAYS = LocalDate.parse("2024-01-22");
    private final LocalDate RETURN_DATA_ONE_MONTH = LocalDate.parse("2024-01-01");
    private final LocalDate DATA_FIND = LocalDate.parse( "2024-02-01");
    private final LocalDate HISTORY_FEE_NOT_CONSIDERATION = LocalDate.parse("2023-12-08");
    private final LocalDate HISTORY_FEE_CONSIDERATION_NORMAL = LocalDate.parse("2024-01-31");
    private final Double NO_FEE = 0.0;
    private final Double FEE_THREE_DAYS = 5.0 * 3;
    private final Double PENALIZED_FEE_BASE_TEN_DAYS = 15.0 * (10-3);
    private final Double SANCTION_FEE_BASE_ONE_MONT = 150.0;

    private Loan LOAN;
    private LoanCreateRequestDTO dtoCreate;
    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        loanService = new LoanServiceImpl(studentService, bookService, loanRepository,feeService,paymentService,careerService,dateNowService);

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

        dtoCreate = new LoanCreateRequestDTO(CODE_BOOK,CARNET,LAON_DATA,STATE);
    }

    @Test
    void save() throws ServiceException {
        List<Loan> response = new ArrayList<>();
        response.add(LOAN);

        when(studentService.isActive(CARNET)).thenReturn(true);
        when(loanRepository.findAllByStateIsNotAndCarnet_Carnet(LoanEnum.cancelled,CARNET)).thenReturn(response);
        when(bookService.findByCodeNotDTO(CODE_BOOK)).thenReturn(BOOK);
        when(studentService.findStudentByCarnetNotDto(CARNET)).thenReturn(STUDENT);

        when(loanRepository.save(any(Loan.class))).thenReturn(LOAN);

        LoanResponseDTO actually = loanService.save(dtoCreate);

        LOAN.getBookCode().setQuantity(QUANTITY-1);
        LoanResponseDTO expected = new LoanResponseDTO(LOAN);

        assertThat(actually).isEqualToComparingFieldByFieldRecursively(expected);

    }

    @Test
    void saveWithBookNotSupply() throws ServiceException{
        BOOK.setQuantity(0);
        when(studentService.isActive(CARNET)).thenReturn(true);
        when(loanRepository.findAllByStateIsNotAndCarnet_Carnet(LoanEnum.cancelled,CARNET)).thenReturn(new ArrayList<>(0));
        when(bookService.findByCodeNotDTO(CODE_BOOK)).thenReturn(BOOK);
        Assertions.assertThrows(LimitBookLoanStudent.class,()->loanService.save(dtoCreate));
    }
    @Test
    void saveLoanWithStudentInactive() throws ServiceException{
        when(studentService.isActive(CARNET)).thenReturn(false);
        Assertions.assertThrows(StudentInactive.class,()->loanService.save(dtoCreate));
    }
    @Test
    void saveLoanWithBookLimit() throws ServiceException{
        List<Loan> list = new ArrayList<>();
        list.add((LOAN));
        list.add((LOAN));
        list.add((LOAN));
        when(studentService.isActive(CARNET)).thenReturn(true);
        when(loanRepository.findAllByStateIsNotAndCarnet_Carnet(LoanEnum.cancelled,CARNET)).thenReturn(list);
        Assertions.assertThrows(LimitBookLoanStudent.class,()->loanService.save(dtoCreate));
    }

    @Test
    void update() throws ServiceException{
        LOAN.setState(LoanEnum.cancelled);
        when(loanRepository.save(LOAN)).thenReturn(LOAN);
        when(loanRepository.findById(ID_LOAN)).thenReturn(Optional.of(LOAN));

        LoanResponseDTO actually1 = loanService.update(ID_LOAN,LoanEnum.cancelled);
        assertThat(actually1).isEqualToComparingFieldByFieldRecursively(new LoanResponseDTO(LOAN));

        LOAN.setState(LoanEnum.penalized);
        when(loanRepository.save(LOAN)).thenReturn(LOAN);
        when(loanRepository.findById(ID_LOAN)).thenReturn(Optional.of(LOAN));

        LoanResponseDTO actually2 = loanService.update(ID_LOAN,LoanEnum.penalized);
        assertThat(actually2).isEqualToComparingFieldByFieldRecursively(new LoanResponseDTO(LOAN));

        LOAN.setState(LoanEnum.sanction);
        when(loanRepository.save(LOAN)).thenReturn(LOAN);
        when(loanRepository.findById(ID_LOAN)).thenReturn(Optional.of(LOAN));

        LoanResponseDTO actually3 = loanService.update(ID_LOAN,LoanEnum.sanction);
        assertThat(actually3).isEqualToComparingFieldByFieldRecursively(new LoanResponseDTO(LOAN));
    }

    @Test
    void findByCode() throws ServiceException{
        when(loanRepository.findById(ID_LOAN)).thenReturn(Optional.of(LOAN));
        Loan actually = loanService.findByCodeNotDto(ID_LOAN);
        assertThat(actually).isEqualToComparingFieldByFieldRecursively(LOAN);
    }

    @Test
    void findByCodeNotExist() throws  ServiceException{
        when(loanRepository.findById(ID_LOAN)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class,()->loanService.findByCodeDto(ID_LOAN));
    }
    @Test
    void findByCodeDto() throws ServiceException{
        LoanResponseDTO expected = new LoanResponseDTO(LOAN);
        when(loanRepository.findById(ID_LOAN)).thenReturn(Optional.of(LOAN));
        LoanResponseDTO actually = loanService.findByCodeDto(ID_LOAN);
        assertThat(actually).isEqualToComparingFieldByFieldRecursively(expected);
    }
    @Test
    void init() throws ServiceException{
        Collection states = Arrays.asList(LoanEnum.cancelled,LoanEnum.sanction);
        List<Loan> expected = new ArrayList<>();

        Loan loan1 = new Loan();
        loan1.setId(ID_LOAN);
        loan1.setBookCode(BOOK);
        loan1.setCarnet(STUDENT);
        loan1.setLaonDate(LAON_DATA);
        loan1.setReturnDate(RETURN_DATA_THREE_DAYS);
        loan1.setState(LoanEnum.borrowed);
        loan1.setLoan_fee(FEE_THREE_DAYS);
        expected.add(loan1);
        when(loanRepository.findAllByReturnDateLessThanAndStateNotIn(HISTORY_FEE_NOT_CONSIDERATION, states)).thenReturn(expected);
        when(feeService.findLast()).thenReturn(HISTORY_FEE_NOT_CONSIDERATION);

        when(dateNowService.getDateNow()).thenReturn(LocalDate.now());
        loanService.init();
    }
    @Test
    void updatePaymentZeroDays() throws ServiceException{
        Collection states = Arrays.asList(LoanEnum.cancelled,LoanEnum.sanction);
        List<Loan> expected = new ArrayList<>();

        Loan loan1 = new Loan();
        loan1.setId(ID_LOAN);
        loan1.setBookCode(BOOK);
        loan1.setCarnet(STUDENT);
        loan1.setLaonDate(LAON_DATA);
        loan1.setReturnDate(RETURN_DATA_THREE_DAYS);
        loan1.setState(LoanEnum.borrowed);
        loan1.setLoan_fee(FEE_THREE_DAYS);
        expected.add(loan1);

        when(loanRepository.findAllByReturnDateLessThanAndStateNotIn(HISTORY_FEE_NOT_CONSIDERATION, states)).thenReturn(expected);
        when(feeService.findLast()).thenReturn(HISTORY_FEE_NOT_CONSIDERATION);
        when(dateNowService.getDateNow()).thenReturn(HISTORY_FEE_NOT_CONSIDERATION);
        List<Loan> actually = loanService.loansUpdateFee();
        assertThat(actually.size()).isEqualTo(expected.size());
        for (int i = 0; i < actually.size(); i++) {
            assertThat(actually.get(i)).isEqualToComparingFieldByFieldRecursively(expected.get(i));
        }
    }
    @Test
    void updatePaymentNormal_ThreeDays() throws ServiceException{
        Collection states = Arrays.asList(LoanEnum.cancelled,LoanEnum.sanction);
        List<Loan> expected = new ArrayList<>();

        //Prestamo con 3 dias, se cobran los 3
        Loan loan1 = new Loan();
        loan1.setId(ID_LOAN);
        loan1.setBookCode(BOOK);
        loan1.setCarnet(STUDENT);
        loan1.setLaonDate(LAON_DATA);
        loan1.setReturnDate(RETURN_DATA_THREE_DAYS);
        loan1.setState(LoanEnum.borrowed);
        loan1.setLoan_fee(FEE_THREE_DAYS);
        expected.add(loan1);

        List<Loan> loansAnalize = new ArrayList<>();
        Loan loanAnalize1 = new Loan();
        loanAnalize1.setId(ID_LOAN);
        loanAnalize1.setBookCode(BOOK);
        loanAnalize1.setCarnet(STUDENT);
        loanAnalize1.setLaonDate(LAON_DATA);
        loanAnalize1.setReturnDate(RETURN_DATA_THREE_DAYS);
        loanAnalize1.setLoan_fee(0.0);
        loanAnalize1.setState(STATE);

        loansAnalize.add(loanAnalize1);


        when(loanRepository.findAllByReturnDateLessThanAndStateNotIn(DATA_FIND, states)).thenReturn(loansAnalize);
        when(feeService.findLast()).thenReturn(HISTORY_FEE_NOT_CONSIDERATION);
        when(loanRepository.save(any(Loan.class))).thenReturn(loanAnalize1);


        FeeUpdateHistory fee = new FeeUpdateHistory();
        fee.setId(1L);
        fee.setTotalLoans(1);
        fee.setDate(DATA_FIND);
        when(feeService.save(any(FeeUpdateHistory.class))).thenReturn(fee);
        when(dateNowService.getDateNow()).thenReturn(DATA_FIND);
        List<Loan> actually = loanService.loansUpdateFee();
        assertThat(actually.size()).isEqualTo(expected.size());
        for (int i = 0; i < actually.size(); i++) {
            assertThat(actually.get(i)).isEqualToComparingFieldByFieldRecursively(expected.get(i));
        }
    }
    @Test
    void updatePayment_TenDays()  throws ServiceException{
        Collection states = Arrays.asList(LoanEnum.cancelled,LoanEnum.sanction);
        List<Loan> expected = new ArrayList<>();

        //Prestamo con 10 dias, se cobran 7
        Loan loan1 = new Loan();
        loan1.setId(ID_LOAN);
        loan1.setBookCode(BOOK);
        loan1.setCarnet(STUDENT);
        loan1.setLaonDate(LAON_DATA);
        loan1.setReturnDate(RETURN_DATA_PENALIZED_TEN_DAYS);
        loan1.setState(LoanEnum.penalized);
        loan1.setPenalized_fee(PENALIZED_FEE_BASE_TEN_DAYS);
        expected.add(loan1);

        List<Loan> loansAnalize = new ArrayList<>();
        Loan loanAnalize1 = new Loan();
        loanAnalize1.setId(ID_LOAN);
        loanAnalize1.setBookCode(BOOK);
        loanAnalize1.setCarnet(STUDENT);
        loanAnalize1.setLaonDate(LAON_DATA);
        loanAnalize1.setReturnDate(RETURN_DATA_PENALIZED_TEN_DAYS);
        loanAnalize1.setPenalized_fee(0.0);
        loanAnalize1.setState(STATE);

        loansAnalize.add(loanAnalize1);


        when(loanRepository.findAllByReturnDateLessThanAndStateNotIn(DATA_FIND, states)).thenReturn(loansAnalize);
        when(feeService.findLast()).thenReturn(HISTORY_FEE_NOT_CONSIDERATION);
        when(loanRepository.save(any(Loan.class))).thenReturn(loanAnalize1);


        FeeUpdateHistory fee = new FeeUpdateHistory();
        fee.setId(1L);
        fee.setTotalLoans(1);
        fee.setDate(DATA_FIND);
        when(feeService.save(any(FeeUpdateHistory.class))).thenReturn(fee);
        when(dateNowService.getDateNow()).thenReturn(DATA_FIND);
        List<Loan> actually = loanService.loansUpdateFee();
        assertThat(actually.size()).isEqualTo(expected.size());
        for (int i = 0; i < actually.size(); i++) {
            assertThat(actually.get(i)).isEqualToComparingFieldByFieldRecursively(expected.get(i));
        }
    }
    @Test
    void updatePayment_OneMonth() throws ServiceException{
        Collection states = Arrays.asList(LoanEnum.cancelled,LoanEnum.sanction);
        List<Loan> expected = new ArrayList<>();

        //Prestamo con 1 mes, se cobra 150
        Loan loan1 = new Loan();
        loan1.setId(ID_LOAN);
        loan1.setBookCode(BOOK);
        loan1.setCarnet(STUDENT);
        loan1.setLaonDate(LAON_DATA);
        loan1.setReturnDate(RETURN_DATA_ONE_MONTH);
        loan1.setState(LoanEnum.sanction);
        loan1.setSanction_fee(SANCTION_FEE_BASE_ONE_MONT);
        expected.add(loan1);

        List<Loan> loansAnalize = new ArrayList<>();
        Loan loanAnalize1 = new Loan();
        loanAnalize1.setId(ID_LOAN);
        loanAnalize1.setBookCode(BOOK);
        loanAnalize1.setCarnet(STUDENT);
        loanAnalize1.setLaonDate(LAON_DATA);
        loanAnalize1.setReturnDate(RETURN_DATA_ONE_MONTH);
        loanAnalize1.setSanction_fee(0.0);
        loanAnalize1.setState(LoanEnum.penalized);

        loansAnalize.add(loanAnalize1);
        STUDENT.setStatus(0);
        when(studentService.findStudentByCarnetNotDto(CARNET)).thenReturn(STUDENT);
        when(studentService.updateNoDto(STUDENT)).thenReturn(STUDENT);

        when(loanRepository.findAllByReturnDateLessThanAndStateNotIn(DATA_FIND, states)).thenReturn(loansAnalize);
        when(feeService.findLast()).thenReturn(HISTORY_FEE_NOT_CONSIDERATION);
        when(loanRepository.save(any(Loan.class))).thenReturn(loanAnalize1);


        FeeUpdateHistory fee = new FeeUpdateHistory();
        fee.setId(1L);
        fee.setTotalLoans(1);
        fee.setDate(DATA_FIND);
        when(feeService.save(any(FeeUpdateHistory.class))).thenReturn(fee);
        when(dateNowService.getDateNow()).thenReturn(DATA_FIND);
        List<Loan> actually = loanService.loansUpdateFee();
        assertThat(actually.size()).isEqualTo(expected.size());
        for (int i = 0; i < actually.size(); i++) {
            assertThat(actually.get(i)).isEqualToComparingFieldByFieldRecursively(expected.get(i));
        }
    }
    @Test
    void updateFee_considerate_history()  throws ServiceException{
        Collection states = Arrays.asList(LoanEnum.cancelled,LoanEnum.sanction);
        List<Loan> expected = new ArrayList<>();

        //Prestamo con 3 dias, se cobra 1, por historial
        Loan loan1 = new Loan();
        loan1.setId(ID_LOAN);
        loan1.setBookCode(BOOK);
        loan1.setCarnet(STUDENT);
        loan1.setLaonDate(LAON_DATA);
        loan1.setReturnDate(RETURN_DATA_THREE_DAYS);
        loan1.setState(LoanEnum.borrowed);
        loan1.setLoan_fee(FEE_THREE_DAYS);
        expected.add(loan1);

        List<Loan> loansAnalize = new ArrayList<>();
        Loan loanAnalize1 = new Loan();
        loanAnalize1.setId(ID_LOAN);
        loanAnalize1.setBookCode(BOOK);
        loanAnalize1.setCarnet(STUDENT);
        loanAnalize1.setLaonDate(LAON_DATA);
        loanAnalize1.setReturnDate(RETURN_DATA_THREE_DAYS);
        loanAnalize1.setLoan_fee(10.0);
        loanAnalize1.setState(LoanEnum.borrowed);

        loansAnalize.add(loanAnalize1);


        when(loanRepository.findAllByReturnDateLessThanAndStateNotIn(DATA_FIND, states)).thenReturn(loansAnalize);
        when(feeService.findLast()).thenReturn(HISTORY_FEE_CONSIDERATION_NORMAL);
        when(loanRepository.save(any(Loan.class))).thenReturn(loanAnalize1);


        FeeUpdateHistory fee = new FeeUpdateHistory();
        fee.setId(1L);
        fee.setTotalLoans(1);
        fee.setDate(DATA_FIND);
        when(feeService.save(any(FeeUpdateHistory.class))).thenReturn(fee);

        when(dateNowService.getDateNow()).thenReturn(DATA_FIND);
        List<Loan> actually = loanService.loansUpdateFee();
        assertThat(actually.size()).isEqualTo(expected.size());
        for (int i = 0; i < actually.size(); i++) {
            assertThat(actually.get(i)).isEqualToComparingFieldByFieldRecursively(expected.get(i));
        }
    }
    @Test
    void updatePayment_TenDaysHistory()  throws ServiceException{
        Collection states = Arrays.asList(LoanEnum.cancelled,LoanEnum.sanction);
        List<Loan> expected = new ArrayList<>();

        //Prestamo con 10 dias, se cobra 1 por historial
        Loan loan1 = new Loan();
        loan1.setId(ID_LOAN);
        loan1.setBookCode(BOOK);
        loan1.setCarnet(STUDENT);
        loan1.setLaonDate(LAON_DATA);
        loan1.setReturnDate(RETURN_DATA_PENALIZED_TEN_DAYS);
        loan1.setState(LoanEnum.penalized);
        loan1.setPenalized_fee(PENALIZED_FEE_BASE_TEN_DAYS);
        expected.add(loan1);

        List<Loan> loansAnalize = new ArrayList<>();
        Loan loanAnalize1 = new Loan();
        loanAnalize1.setId(ID_LOAN);
        loanAnalize1.setBookCode(BOOK);
        loanAnalize1.setCarnet(STUDENT);
        loanAnalize1.setLaonDate(LAON_DATA);
        loanAnalize1.setReturnDate(RETURN_DATA_PENALIZED_TEN_DAYS);
        loanAnalize1.setPenalized_fee(90.0);
        loanAnalize1.setState(STATE);

        loansAnalize.add(loanAnalize1);


        when(loanRepository.findAllByReturnDateLessThanAndStateNotIn(DATA_FIND, states)).thenReturn(loansAnalize);
        when(feeService.findLast()).thenReturn(HISTORY_FEE_CONSIDERATION_NORMAL);
        when(loanRepository.save(any(Loan.class))).thenReturn(loanAnalize1);


        FeeUpdateHistory fee = new FeeUpdateHistory();
        fee.setId(1L);
        fee.setTotalLoans(1);
        fee.setDate(DATA_FIND);
        when(feeService.save(any(FeeUpdateHistory.class))).thenReturn(fee);

        when(dateNowService.getDateNow()).thenReturn(DATA_FIND);
        List<Loan> actually = loanService.loansUpdateFee();
        assertThat(actually.size()).isEqualTo(expected.size());
        for (int i = 0; i < actually.size(); i++) {
            assertThat(actually.get(i)).isEqualToComparingFieldByFieldRecursively(expected.get(i));
        }
    }
    @Test
    void testFindAllByCarneAndStates() throws ServiceException{
        Collection<LoanEnum> states = Arrays.asList(LoanEnum.borrowed,LoanEnum.penalized,LoanEnum.sanction);
        List<Loan> expected = new ArrayList<>();
        LOAN.setLoan_fee(FEE_THREE_DAYS);
        expected.add(LOAN);
        when(loanRepository.findAllByCarnet_CarnetAndStateIn(CARNET,states)).thenReturn(expected);
        when(studentService.findStudentByCarnetNotDto(CARNET)).thenReturn(STUDENT);
        ReportStudentNotCanlledLoanResponseDTO actually = loanService.findlAllNotCancelledByCarnet(CARNET);
        ReportStudentNotCanlledLoanResponseDTO expected1 = new ReportStudentNotCanlledLoanResponseDTO(STUDENT,expected);
        assertThat(actually.getStudent()).isEqualToComparingFieldByFieldRecursively(expected1.getStudent());
        assertThat(actually.getLoans().size()).isEqualTo(expected.size());
        for (int i = 0; i < actually.getLoans().size(); i++) {
            assertThat(actually.getLoans().get(i)).isEqualToComparingFieldByFieldRecursively(new LoanResponseDTO(expected.get(i)));
        }
    }
    @Test
    void testFindAllRetunsNow() throws ServiceException{
        List<Loan> find = new ArrayList<>();
        find.add(LOAN);
        when(loanRepository.findAllByReturnDate(LocalDate.now())).thenReturn(find);
        List<LoanResponseDTO> actually = loanService.findAllByReturnNow();
        assertThat(actually.size()).isEqualTo(find.size());
        for (int i = 0; i < actually.size(); i++) {
            assertThat(actually.get(i)).isEqualToComparingFieldByFieldRecursively(new LoanResponseDTO(find.get(i)));
        }
    }
    @Test
    void testFindAllSanction() throws ServiceException{
        List<Loan> find = new ArrayList<>();
        LOAN.setState(LoanEnum.sanction);
        find.add(LOAN);
        when(loanRepository.findAllByState(LoanEnum.sanction)).thenReturn(find);
        List<LoanResponseDTO> actually = loanService.finddAllBySanction();
        assertThat(actually.size()).isEqualTo(find.size());
        for (int i = 0; i < actually.size(); i++) {
            assertThat(actually.get(i)).isEqualToComparingFieldByFieldRecursively(new LoanResponseDTO(find.get(i)));
            assertThat(actually.get(i).getState()).isEqualTo(LoanEnum.sanction);
        }
    }
    @Test
    void allByStateAndDateBetwen(){
        List<Loan> loans = List.of(LOAN);

        Loan LOAN;
        Long ID_LOAN = 1l;
        LocalDate LAON_DATA = LocalDate.now();
        LocalDate RETURN_DATA = LocalDate.now().plusDays(3);
        LoanEnum STATE = LoanEnum.borrowed;
        Book BOOK;
        Long ID_BOOK = 1L;
        String CODE_BOOK = "CODE BOOK";
        String TITLE = "TITLE";
        String AUTH = "AUTHOR";
        Integer QUANTITY = 10;
        LocalDate DATE_PUBLICATION = LocalDate.now();

        Editorial EDITORIAL;
        Long  EDITORIAL_ID = 1L;
        String EDITORIAL_NAME = "EDITORIAL 1";
        Student STUDENT;

        Long ID_STUDENT = 1L;
        String NAME = "DAVID";
        LocalDate DATE_BIRD = LocalDate.now();
        String CARNET = "201632145";
        Integer STATUS = 1;
        Career CAREER;
        Long CARRER_ID = 1L;
        String CARRER_NAME = "CARRER";
        Double NO_FEE = 0.0;
        Payment PAYMENT;
        Long ID_PAYMENT = 1L;
        PaymentEnum type = PaymentEnum.normal;
        Double TOTAL = 100.0;
        LocalDate DATE_PAYMENT = LocalDate.parse("2024-01-01");

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

        List<Payment> payments = List.of(PAYMENT);
        List<PaymentResponseDto> paymentsDto = payments.stream().map(PaymentResponseDto::new).toList();
        when(paymentService.findAllByTypeAndDate(PaymentEnum.normal, LocalDate.now(), LocalDate.now())).thenReturn(paymentsDto);
        when(paymentService.findAllByTypeAndDate(PaymentEnum.normal, LocalDate.now(), LocalDate.now())).thenReturn(paymentsDto);
        when(loanRepository.findAllByStateAndReturnDateBetween(LoanEnum.cancelled, LocalDate.now(), LocalDate.now())).thenReturn(loans);
        ReportDatesRequestDTO request = new ReportDatesRequestDTO(LocalDate.now(), LocalDate.now());

        ReportTotalCashResponseDTO expected = new ReportTotalCashResponseDTO(loans,250.00,250.00,250.00);
        ReportTotalCashResponseDTO actually = loanService.findAllByTotalCash(request);
        assertThat(actually.getLoans().size()).isEqualTo(expected.getLoans().size());
    }

    @Test
    void findMoreCareer() throws ServiceException{
        CAREER = new Career();
        CAREER.setId(CARRER_ID);
        CAREER.setName(CARRER_NAME);

        when(loanRepository.findMoreCareer(LocalDate.now(), LocalDate.now())).thenReturn(Optional.of(LOAN));
        when(careerService.findByIdDto(CARRER_ID)).thenReturn(new CareerResponseDTO(CAREER));
        List<Loan> loans = List.of(LOAN);
        when(loanRepository.findAllByCarnet_IdCareer_Id(CARRER_ID)).thenReturn(loans);

        ReportDatesRequestDTO request = new ReportDatesRequestDTO(LocalDate.now(), LocalDate.now());
        ReportMoreCareerResponseDTO expected = new ReportMoreCareerResponseDTO(new CareerResponseDTO(CAREER),loans.stream().map(LoanResponseDTO::new).toList(),1);
        ReportMoreCareerResponseDTO actually = loanService.findMoreCareer(request);
        assertThat(actually.getCareer()).isEqualToComparingFieldByFieldRecursively(expected.getCareer());
    }
    @Test
    void findMoreCareerNotFound(){
        when(loanRepository.findMoreCareer(LocalDate.now(), LocalDate.now())).thenReturn(Optional.empty());
        ReportDatesRequestDTO request = new ReportDatesRequestDTO(LocalDate.now(), LocalDate.now());
        Assertions.assertThrows(NotFoundException.class,()->loanService.findMoreCareer(request));
    }
    @Test
    void findMoreStudentNoFound(){
        when(loanRepository.findMoreCareer(LocalDate.now(), LocalDate.now())).thenReturn(Optional.empty());
        ReportDatesRequestDTO request = new ReportDatesRequestDTO(LocalDate.now(), LocalDate.now());;
        Assertions.assertThrows(NotFoundException.class,()->loanService.findMoreStudent(request));
    }
    @Test
    void findMoreStudentLoans() throws ServiceException{
        when(loanRepository.findMoreStudent(LocalDate.now(), LocalDate.now())).thenReturn(Optional.of(LOAN));
        when(loanRepository.findAllByCarnet_Carnet(CARNET)).thenReturn(List.of(LOAN));
        ReportDatesRequestDTO request = new ReportDatesRequestDTO(LocalDate.now(), LocalDate.now());;
        ReportStudentMoreLoansResponseDTO expected = new ReportStudentMoreLoansResponseDTO(STUDENT,List.of(LOAN));
        ReportStudentMoreLoansResponseDTO actually = loanService.findMoreStudent(request);
        assertThat(actually.getStudent()).isEqualToComparingFieldByFieldRecursively(expected.getStudent());
        assertThat(actually.getTotalLoans()).isEqualTo(expected.getTotalLoans());
    }
    @Test
    void notCancelledMeNotFound() throws ServiceException{
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(CARNET);
        when(studentService.findStudentByCarnetNotDto(CARNET)).thenReturn(STUDENT);
        when(loanRepository.findAllByStateIsNotAndCarnet_Carnet(LoanEnum.cancelled,CARNET)).thenReturn(List.of());
        Assertions.assertThrows(NotFoundException.class,()->loanService.notCancelledMe());
    }
    @Test
    void notCancelledMe() throws ServiceException{
        List<Loan> loans = List.of(LOAN);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(CARNET);
        when(studentService.findStudentByCarnetNotDto(CARNET)).thenReturn(STUDENT);
        when(loanRepository.findAllByStateIsNotAndCarnet_Carnet(LoanEnum.cancelled,CARNET)).thenReturn(loans);

        ReportStudentNotCanlledLoanResponseDTO expected = new ReportStudentNotCanlledLoanResponseDTO(STUDENT,loans);
        ReportStudentNotCanlledLoanResponseDTO actually = loanService.notCancelledMe();
        assertThat(actually.getStudent()).isEqualToComparingFieldByFieldRecursively(expected.getStudent());
        assertThat(actually.getLoans().size()).isEqualTo(expected.getLoans().size());
        for (int i = 0; i < actually.getLoans().size(); i++) {
            assertThat(actually.getLoans().get(i)).isEqualToComparingFieldByFieldRecursively(new LoanResponseDTO(loans.get(i)));
        }
    }
}