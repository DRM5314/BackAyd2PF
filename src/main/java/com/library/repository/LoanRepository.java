package com.library.repository;

import com.library.enums.LoanEnum;
import com.library.model.Loan;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

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
    List<Loan> findAllByCarnet_CarnetAndStateIn(String carnet, Collection<LoanEnum> state);
    @Query ("SELECT l, COUNT(l) as suma FROM Loan l INNER JOIN Student s on l.carnet.idCareer.id = s.idCareer.id where  l.returnDate between :i and :e group by l.carnet.idCareer.id order by suma desc limit 1")
    Optional<Loan> findMoreCareer(@Param("i") LocalDate init,@Param("e") LocalDate end);
    List<Loan> findAllByCarnet_IdCareer_Id(Long id);
}
