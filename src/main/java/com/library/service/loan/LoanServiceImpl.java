package com.library.service.loan;

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
import com.library.service.payment.PaymentService;
import com.library.service.student.StudentService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LoanServiceImpl implements LoanService{
    private final Double NORMAL_FEE = 5.0;
    private final Double PENALIZED_FEE = 15.0;
    private final Double SANCTION_FEE = 150.0;
    private StudentService studentService;
    private BookService bookService;
    private LoanRepository loanRepository;
    private FeeService feeService;
    private PaymentService paymentService;
    private CareerService careerService;
    private ObtainsDateNow dateNowService;
    @Autowired
    public LoanServiceImpl(StudentService studentService, BookService bookService,LoanRepository loanRepository,FeeService feeService,@Lazy PaymentService paymentService,@Lazy CareerService careerService,ObtainsDateNow dateNowService){
        this.studentService = studentService;
        this.bookService = bookService;
        this.loanRepository = loanRepository;
        this.feeService = feeService;
        this.paymentService = paymentService;
        this.careerService = careerService;
        this.dateNowService = dateNowService;
    }
    @PostConstruct
    @Transactional
    public void init() throws ServiceException {
        this.loansUpdateFee();
    }

    @Override
    public LoanResponseDTO save(LoanCreateRequestDTO save) throws ServiceException {
        if(!studentService.isActive(save.getCarnet())){
            throw new StudentInactive(String.format("This student with: %s, is inactive!",save.getCarnet()));
        }
        List<LoanResponseDTO> booksLoanStudent =  howManyBooksLoanByStudnet(save.getCarnet());

        if(booksLoanStudent.size()>=3){
            //Mostrar libros que tiene prestados
            throw new LimitBookLoanStudent("This student have the limit of loans");
        }
        Book book = bookService.findByCodeNotDTO(save.getBookCode());
        if(book.getQuantity()<=0){
            throw new LimitBookLoanStudent("Books in stock are not supply");
        }
        book.setQuantity(book.getQuantity() - 1);
        Student student = studentService.findStudentByCarnetNotDto(save.getCarnet());

        Loan loanSave = new Loan();
        loanSave.setBookCode(book);
        loanSave.setCarnet(student);
        loanSave.setLaonDate(save.getLaonDate());
        loanSave.setReturnDate(save.getLaonDate().plusDays(3));
        loanSave.setState(save.getStatus());
        loanSave.setLoan_fee(0.0);
        loanSave.setPenalized_fee(0.0);
        loanSave.setSanction_fee(0.0);

        loanSave = loanRepository.save(loanSave);
        var returns = new LoanResponseDTO(loanSave);
        return returns;

    }

    @Override
    public LoanResponseDTO update(Long id, LoanEnum state) throws ServiceException {
        Loan updateLoan = findByCodeNotDto(id);
        updateLoan.setState(state);
        updateLoan = loanRepository.save(updateLoan);
        return new LoanResponseDTO(updateLoan);
    }


    @Override
    public Loan findByCodeNotDto(Long id) throws ServiceException {
        return loanRepository.findById(id).orElseThrow(()->
                new NotFoundException(String.format("Loan with id: %s, not exist",id)
                ));
    }

    @Override
    public LoanResponseDTO findByCodeDto(Long id) throws ServiceException{
        return new LoanResponseDTO(findByCodeNotDto(id));
    }
    @Override
    public List<LoanResponseDTO> howManyBooksLoanByStudnet(String carnet) throws ServiceException {
        return loanRepository.findAllByStateIsNotAndCarnet_Carnet(LoanEnum.cancelled,carnet).stream().map
                (LoanResponseDTO::new).collect(Collectors.toList());
    }

//    @Before("execution(* com.library.service.loan.*.*(..))")
//    @Scheduled(cron = "0 * * * * ?")
//    public void saluda(){
//        System.out.println("Hola mundo");
//    }

    @Scheduled(cron = "0 0 4 * * ?")
    @Transactional
    @Override
    public List<Loan> loansUpdateFee() throws ServiceException{
        System.out.println("Ejecuto actualizar tareas de forma automatica");
        //Para actualizar las tasas de los prestamos no se incluyen los prestamos cancelados y sancionados
        List<LoanEnum> notFind = Arrays.asList(LoanEnum.cancelled,LoanEnum.sanction);
        List<Loan> loans = loanRepository.findAllByReturnDateLessThanAndStateNotIn(dateNowService.getDateNow(),notFind);

        //Se consulta cuando fue la ultima actualizacion de tasas de prestamos
        LocalDate dateNow = dateNowService.getDateNow();
        LocalDate lastRegister = feeService.findLast();


        for (int i = 0; i < loans.size(); i++) {
            Loan loan = loans.get(i);
            //Se obtienen los dias que han pasado desde el ultimo registro
            Long daysLastRegister = ChronoUnit.DAYS.between(loan.getReturnDate(), lastRegister);

            /*
            Si es negativo quiere decir que ya se tiene una actualizacion de tasas previo a esa fecha,
            de lo contrario se tomaran en cuenta los dias que han pasado desde la fecha de devolucion hasta la fecha actual
            para tener un registro directo de los dias que no se tuvo en cuenta en la ultima actualizacion
             */
            if(daysLastRegister < 0) daysLastRegister = 0L;

            //Segun la logica de negocio se tienen 3 tipos de tasas, normal, penalizada y sancionada
            Long days = ChronoUnit.DAYS.between(loan.getReturnDate(), dateNow);
            Long months = ChronoUnit.MONTHS.between(loan.getReturnDate(), dateNow);
            LoanEnum enumType = loan.getState();

            Double newFee;
            //Tasa normal indica que si no pasan mas de 3 dias desde la fecha de devolucion se cobrara una tasa normal
            if (days <= 3 && days >= 1 && enumType == LoanEnum.borrowed) {
                days = days - daysLastRegister;
                newFee = loan.getLoan_fee() + (NORMAL_FEE * days.intValue());
                loan.setLoan_fee(newFee);
                //Tasa penalizada por cada dia pasando de los 3 dias se cobrara una tasa penalizada
            } else if (days > 3 && months == 0 && (enumType == LoanEnum.borrowed || enumType == LoanEnum.penalized)) {
                days = days - daysLastRegister;
                if(daysLastRegister == 0)days = days - 3;
                newFee = loan.getPenalized_fee() + (PENALIZED_FEE * days.intValue());
                loan.setPenalized_fee(newFee);
                loan.setState(LoanEnum.penalized);
                //Si pasan mas de 1 mes desde la fecha de devolucion se cobrara una tasa sancionada
            } else if (months >= 1 && enumType == LoanEnum.penalized) {
                //Se desactiva al usuario si llega a caer en penalizacion
                Student student = studentService.findStudentByCarnetNotDto(loan.getCarnet().getCarnet());
                student.setStatus(0);
                studentService.updateNoDto(student);
                loan.setSanction_fee(SANCTION_FEE);
                loan.setState(LoanEnum.sanction);
            }
            loanRepository.save(loan);
        }
        //Se guarda un registro de la actualizacion de tasas
        FeeUpdateHistory feeUpdateHistory = new FeeUpdateHistory();
        feeUpdateHistory.setTotalLoans(loans.size());
        feeUpdateHistory.setDate(LocalDate.now());
        feeService.save(feeUpdateHistory);
        return loans;
    }

    @Override
    public ReportStudentNotCanlledLoanResponseDTO findlAllNotCancelledByCarnet(String carnet) throws ServiceException {
        Collection<LoanEnum> state = Arrays.asList(LoanEnum.borrowed,LoanEnum.penalized,LoanEnum.sanction);
        Student student = studentService.findStudentByCarnetNotDto(carnet);
        List<Loan> loans = loanRepository.findAllByCarnet_CarnetAndStateIn(carnet, state);
        return new ReportStudentNotCanlledLoanResponseDTO(student,loans);
    }

    @Override
    public List<LoanResponseDTO> findAllByReturnNow() throws ServiceException {
        List<Loan> loans = loanRepository.findAllByReturnDate(LocalDate.now());
        return loans.stream().map(LoanResponseDTO::new).collect(Collectors.toList());
    }

    @Override
    public List<LoanResponseDTO> finddAllBySanction() throws ServiceException {
        List<Loan> loans = loanRepository.findAllByState(LoanEnum.sanction);
        return loans.stream().map(LoanResponseDTO::new).collect(Collectors.toList());
    }

    @Override
    public ReportTotalCashResponseDTO findAllByTotalCash(ReportDatesRequestDTO request) {
        List<Loan> loans = loanRepository.findAllByStateAndReturnDateBetween(LoanEnum.cancelled,request.getInit(),request.getEnd());
        Double totalCash = paymentService.findAllByTypeAndDate(PaymentEnum.normal,request.getInit(),request.getEnd()).stream().map(PaymentResponseDto::getTotal).reduce(0.0, Double::sum);
        Double totalCashSanction = paymentService.findAllByType(PaymentEnum.sanction).stream().map(PaymentResponseDto::getTotal).reduce(0.0, Double::sum);
        Double totalCashPenalized = paymentService.findAllByType(PaymentEnum.penalized).stream().map(PaymentResponseDto::getTotal).reduce(0.0, Double::sum);
        return new ReportTotalCashResponseDTO(loans, totalCashSanction, totalCash,totalCashPenalized);
    }

    @Override
    public ReportMoreCareerResponseDTO findMoreCareer(ReportDatesRequestDTO request) throws ServiceException{
        Loan loan = loanRepository.findMoreCareer(request.getInit(),request.getEnd()).orElseThrow(()->
                new NotFoundException(String.format("Not reports!")
                ));
        CareerResponseDTO careerResponseDTO = careerService.findByIdDto(loan.getCarnet().getIdCareer().getId());
        List<LoanResponseDTO> loans = loanRepository.findAllByCarnet_IdCareer_Id(loan.getCarnet().getIdCareer().getId()).stream().map(LoanResponseDTO::new).collect(Collectors.toList());
        return new ReportMoreCareerResponseDTO(careerResponseDTO,loans,loans.size());
    }

    @Override
    public ReportStudentMoreLoansResponseDTO findMoreStudent(ReportDatesRequestDTO request) throws ServiceException {
        Loan loan = loanRepository.findMoreStudent(request.getInit(),request.getEnd()).orElseThrow(()->
                new NotFoundException(String.format("Not reports!")
                ));
        Student student = loan.getCarnet();
        List<Loan> loans = loanRepository.findAllByCarnet_Carnet(student.getCarnet());
        return new ReportStudentMoreLoansResponseDTO(student,loans);
    }
}
