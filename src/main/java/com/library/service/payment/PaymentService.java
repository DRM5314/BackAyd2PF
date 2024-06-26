package com.library.service.payment;

import com.library.dto.payment.PaymentCreateRequestDTO;
import com.library.dto.payment.PaymentResponseDto;
import com.library.exceptions.ServiceException;

public interface PaymentService {
    PaymentResponseDto save(PaymentCreateRequestDTO create) throws ServiceException;
    PaymentResponseDto findById(Long id) throws ServiceException;
}
