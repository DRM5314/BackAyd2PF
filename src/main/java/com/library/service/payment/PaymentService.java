package com.library.service.payment;

import com.library.dto.payment.PaymentCreateRequestDTO;
import com.library.dto.payment.PaymentResponseDto;
import com.library.enums.PaymentEnum;
import com.library.exceptions.ServiceException;

import java.time.LocalDate;
import java.util.*;
public interface PaymentService {
    PaymentResponseDto save(PaymentCreateRequestDTO create) throws ServiceException;
    PaymentResponseDto findById(Long id) throws ServiceException;
    List<PaymentResponseDto> findAllByType(PaymentEnum type);
    List<PaymentResponseDto> findAllByTypeAndDate(PaymentEnum type, LocalDate init, LocalDate end);
}
