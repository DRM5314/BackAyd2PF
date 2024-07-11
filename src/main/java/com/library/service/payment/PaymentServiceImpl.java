package com.library.service.payment;

import com.library.dto.loan.ReportDatesAndCarnetRequestDTO;
import com.library.dto.payment.ReportPaymentSanctionVsLoanResponseDTO;
import com.library.dto.payment.PaymentCreateRequestDTO;
import com.library.dto.payment.PaymentResponseDto;
import com.library.enums.LoanEnum;
import com.library.enums.PaymentEnum;
import com.library.exceptions.DuplicatedEntityException;
import com.library.exceptions.NotFoundException;
import com.library.exceptions.ServiceException;
import com.library.model.Loan;
import com.library.model.Payment;
import com.library.model.Student;
import com.library.repository.PaymentRepository;
import com.library.service.book.BookService;
import com.library.service.loan.LoanService;
import com.library.service.student.StudentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService{
    private PaymentRepository paymentRepository;
    private LoanService loanService;
    private BookService bookService;
    private StudentService studentService;
    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, LoanService loanService, BookService bookService, StudentService studentService){
        this.paymentRepository = paymentRepository;
        this.loanService = loanService;
        this.bookService = bookService;
        this.studentService = studentService;
    }

    @Override
    @Transactional
    public PaymentResponseDto save(PaymentCreateRequestDTO create) throws ServiceException {
        Loan loan = loanService.findByCodeNotDto(create.getLoan());
        if(loan.getState().equals(LoanEnum.cancelled)){
            throw new DuplicatedEntityException("This loan is already cancelled");
        }

        Double total = loan.getLoan_fee() + loan.getPenalized_fee() + loan.getSanction_fee();

        Payment paymentNew = new Payment();
        paymentNew.setLoan(loan);
        paymentNew.setType(this.mapLoanToPayment(loan.getState()));
        paymentNew.setTotal(total);
        paymentNew.setDatePayment(LocalDate.now());

        if(loan.getState().equals(LoanEnum.penalized)){
            Student student = studentService.findStudentByCarnetNotDto(loan.getCarnet().getCarnet());
            student.setStatus(1);
            studentService.updateNoDto(student);
        }
        loanService.update(loan.getId(),LoanEnum.cancelled);
        bookService.updateReturn(loan.getBookCode());
        paymentNew = paymentRepository.save(paymentNew);
        return new PaymentResponseDto(paymentNew);
    }

    @Override
    public PaymentResponseDto findById(Long id) throws ServiceException {
        Payment returns = paymentRepository.findById(id).orElseThrow(()->
                new NotFoundException(String.format("This payment with id: %s, dont exist", id))
                );
        return new PaymentResponseDto(returns);
    }

    @Override
    public List<PaymentResponseDto> findAllByType(PaymentEnum type) {
        List<Payment> payments = paymentRepository.findAllByType(type);
        return payments.stream().map(PaymentResponseDto::new).toList();
    }
    @Override
    public List<PaymentResponseDto> findAllByTypeAndDate(PaymentEnum type, LocalDate init, LocalDate end) {
        List<Payment> payments = paymentRepository.findAllByTypeAndDatePaymentBetween(type,init,end);
        return payments.stream().map(PaymentResponseDto::new).toList();
    }

    @Override
    public ReportPaymentSanctionVsLoanResponseDTO findMoreStudent(ReportDatesAndCarnetRequestDTO request) throws ServiceException {
        List<Payment> payments = paymentRepository.findMoreStudent(request.getCarnet(),PaymentEnum.sanction,request.getInit(),request.getEnd());
        if (payments.isEmpty()){
            throw new NotFoundException("Etudiante no tiene moras pagadas en el rango de fechas seleccionado");
        }
        return new ReportPaymentSanctionVsLoanResponseDTO(payments);
    }

    public PaymentEnum mapLoanToPayment(LoanEnum loanStatus) {
        return switch (loanStatus) {
            case penalized -> PaymentEnum.penalized;
            case sanction -> PaymentEnum.sanction;
            case borrowed, cancelled -> PaymentEnum.normal;
        };
    }
}
