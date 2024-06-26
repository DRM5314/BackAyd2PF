package library.service.service;

import com.library.repository.PaymentRepository;
import com.library.service.loan.LoanService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PaymentServiceImplTest {
    private LoanService loanService = Mockito.mock(LoanService.class);
    private PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);


}
