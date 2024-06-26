package com.library.repository;

import com.library.enums.LoanEnum;
import com.library.model.Loan;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends CrudRepository<Loan,Long> {
    Optional<Loan> findById(Long id);
    //1 Reporte de prestamos que deben ser devueltos
    List<Loan> findAllByReturnDate(LocalDate returns);
    //2 Prestamos en mora
    List<Loan> findAllByState(LoanEnum state);
    //3 Dinero recaudado por prestamo y mora
    List<Loan> findAllByStateAndReturnDateBetween(LoanEnum state,LocalDate init, LocalDate end);
    //4 Moras de un estudiante pagados
    List<Loan> findAllByCarnet_CarnetAndStateAndReturnDateBetween(String carnet, LoanEnum state, LocalDate init, LocalDate end);
    //7 Prestamos activos de un estudiante - 10 Estudiantes que estan en sancion
    List<Loan> findAllByStateAndCarnet_Carnet(LoanEnum state,String carnet);
    List<Loan> findAllByReturnDateLessThanAndStateNotIn(LocalDate returnDate, Collection<LoanEnum> state);
}
