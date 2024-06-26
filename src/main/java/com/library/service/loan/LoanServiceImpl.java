package com.library.service.loan;

import com.library.dto.loan.LoanCreateRequestDTO;
import com.library.dto.loan.LoanResponseDTO;
import com.library.enums.LoanEnum;
import com.library.exceptions.LimitBookLoanStudent;
import com.library.exceptions.NotFoundException;
import com.library.exceptions.ServiceException;
import com.library.exceptions.StudentInactive;
import com.library.model.Book;
import com.library.model.FeeUpdateHistory;
import com.library.model.Loan;
import com.library.model.Student;
import com.library.repository.LoanRepository;
import com.library.service.book.BookService;
import com.library.service.fee.FeeService;
import com.library.service.student.StudentService;
import jakarta.transaction.Transactional;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
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
    @Autowired
    public LoanServiceImpl(StudentService studentService, BookService bookService,LoanRepository loanRepository,FeeService feeService){
        this.studentService = studentService;
        this.bookService = bookService;
        this.loanRepository = loanRepository;
        this.feeService = feeService;
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
        return loanRepository.findAllByStateAndCarnet_Carnet(LoanEnum.borrowed,carnet).stream().map
                (LoanResponseDTO::new).collect(Collectors.toList());
    }

    @Before("execution(* com.library.service.loan.*.*(..))")
    @Scheduled(cron = "0 0 4 * * ?")
    @Transactional
    @Override
    public List<Loan> loansUpdateFee(LocalDate actuallyDate) {
        System.out.println("Ejecuto actualizar tareas de forma automatica");
        List<LoanEnum> notFind = Arrays.asList(LoanEnum.cancelled,LoanEnum.sanction);

        List<Loan> loans = loanRepository.findAllByReturnDateLessThanAndStateNotIn(actuallyDate,notFind);

        LocalDate dateNow = actuallyDate;
        LocalDate lastRegister = feeService.findLast();

        loans.forEach(loan -> {
            Long daysLastRegister = ChronoUnit.DAYS.between(loan.getReturnDate(), lastRegister);

            if(daysLastRegister < 0) daysLastRegister = 0L;

            Long days = ChronoUnit.DAYS.between(loan.getReturnDate(), dateNow);
            Long months = ChronoUnit.MONTHS.between(loan.getReturnDate(), dateNow);
            LoanEnum enumType = loan.getState();

            Double newFee;

            if (days <= 3 && days >= 1 && enumType == LoanEnum.borrowed) {
                days = days - daysLastRegister;
                newFee = loan.getLoan_fee() + (NORMAL_FEE * days.intValue());
                loan.setLoan_fee(newFee);
            } else if (days > 3 && months == 0 && (enumType == LoanEnum.borrowed || enumType == LoanEnum.penalized)) {
                days = days - daysLastRegister;
                if(daysLastRegister == 0)days = days - 3;
                newFee = loan.getPenalized_fee() + (PENALIZED_FEE * days.intValue());
                loan.setPenalized_fee(newFee);
                loan.setState(LoanEnum.penalized);
            } else if (months >= 1 && enumType == LoanEnum.penalized) {
                loan.setSanction_fee(SANCTION_FEE);
                loan.setState(LoanEnum.sanction);
            }
            loanRepository.save(loan);
        });
        FeeUpdateHistory feeUpdateHistory = new FeeUpdateHistory();
        feeUpdateHistory.setTotalLoans(loans.size());
        feeUpdateHistory.setDate(LocalDate.now());
        feeService.save(feeUpdateHistory);
        return loans;
    }
}
