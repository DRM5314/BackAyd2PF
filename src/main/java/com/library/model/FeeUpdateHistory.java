package com.library.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "fee_update_history")
public class FeeUpdateHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "total_loans", nullable = false)
    private Integer totalLoans;

    @Column(name = "date", nullable = false)
    private LocalDate date;

}