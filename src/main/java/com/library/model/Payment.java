package com.library.model;

import com.library.enums.PaymentEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "Payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "loan", nullable = false)
    private Loan loan;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private PaymentEnum type;

    @Column(name = "total", nullable = false)
    private Double total;

    @Column(name = "datePayment", nullable = false)
    private LocalDate datePayment;
}