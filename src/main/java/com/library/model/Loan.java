package com.library.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.library.enums.LoanEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Loan")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bookCode", nullable = false, referencedColumnName = "code")
    private Book bookCode;

    @ManyToOne()
    @JoinColumn(name = "carnet", nullable = false)
    private Student carnet;

    @Column(name = "laonDate", nullable = false)
    private LocalDate laonDate;

    @Column(name = "returnDate", nullable = false)
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private LoanEnum state;

    @Column(name = "loan_fee")
    private Double loan_fee;

    @Column(name = "penalized_fee")
    private Double penalized_fee;

    @Column(name = "sanction_fee")
    private Double sanction_fee;

    @OneToMany(mappedBy = "loan")
    @JsonIgnore
    private Set<Payment> loanPayments = new LinkedHashSet<>();
}