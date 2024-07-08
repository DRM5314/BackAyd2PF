package library.service.service;

import com.library.dto.loan.LoanCreateRequestDTO;
import com.library.dto.loan.LoanResponseDTO;
import com.library.enums.LoanEnum;
import com.library.exceptions.LimitBookLoanStudent;
import com.library.exceptions.NotFoundException;
import com.library.exceptions.ServiceException;
import com.library.exceptions.StudentInactive;
import com.library.model.*;
import com.library.repository.LoanRepository;
import com.library.service.book.BookService;
import com.library.service.fee.FeeService;
import com.library.service.loan.LoanServiceImpl;
import com.library.service.student.StudentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        loanService = new LoanServiceImpl(studentService, bookService, loanRepository,feeService);

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
        when(loanRepository.findAllByStateAndCarnet_Carnet(LoanEnum.borrowed,CARNET)).thenReturn(response);
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
        when(loanRepository.findAllByStateAndCarnet_Carnet(LoanEnum.borrowed,CARNET)).thenReturn(new ArrayList<>(0));
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
        when(loanRepository.findAllByStateAndCarnet_Carnet(LoanEnum.borrowed,CARNET)).thenReturn(list);
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
        List<Loan> actually = loanService.loansUpdateFee(HISTORY_FEE_NOT_CONSIDERATION);
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

        List<Loan> actually = loanService.loansUpdateFee(DATA_FIND);
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

        List<Loan> actually = loanService.loansUpdateFee(DATA_FIND);
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

        List<Loan> actually = loanService.loansUpdateFee(DATA_FIND);
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

        List<Loan> actually = loanService.loansUpdateFee(DATA_FIND);
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

        List<Loan> actually = loanService.loansUpdateFee(DATA_FIND);
        assertThat(actually.size()).isEqualTo(expected.size());
        for (int i = 0; i < actually.size(); i++) {
            assertThat(actually.get(i)).isEqualToComparingFieldByFieldRecursively(expected.get(i));
        }
    }
}