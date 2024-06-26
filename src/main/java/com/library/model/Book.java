package com.library.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "Book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Column(name = "title", nullable = false, length = 60)
    private String title;

    @Column(name = "auth", nullable = false, length = 60)
    private String auth;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "datePublication", nullable = false)
    private LocalDate datePublication;

    @ManyToOne()
    @JoinColumn(name = "idEditorial", nullable = false)
    private Editorial idEditorial;

}