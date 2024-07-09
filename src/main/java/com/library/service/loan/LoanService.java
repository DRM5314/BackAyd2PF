package com.library.service.loan;

import com.library.dto.loan.LoanCreateRequestDTO;
import com.library.dto.loan.LoanResponseDTO;
import com.library.dto.loan.ReportByCashAndDateRequestDTO;
import com.library.dto.loan.ReportTotalCashResponseDTO;
import com.library.enums.LoanEnum;
import com.library.exceptions.ServiceException;
import com.library.model.Loan;

import java.time.LocalDate;
import java.util.List;

public interface LoanService {
    LoanResponseDTO save(LoanCreateRequestDTO save) throws ServiceException;
    LoanResponseDTO update(Long id, LoanEnum state) throws ServiceException;
    Loan findByCodeNotDto(Long id) throws ServiceException;
    LoanResponseDTO findByCodeDto(Long id) throws ServiceException;
    List<LoanResponseDTO> howManyBooksLoanByStudnet(String carnet) throws ServiceException;
    List<Loan> loansUpdateFee(LocalDate dateNow) throws ServiceException;
    List<LoanResponseDTO> findlAllNotCancelledByCarnet(String carnet) throws ServiceException;
    List<LoanResponseDTO> findAllByReturnNow() throws ServiceException;
    List<LoanResponseDTO> finddAllBySanction() throws ServiceException;
    ReportTotalCashResponseDTO findAllByTotalCash(ReportByCashAndDateRequestDTO request);
}
