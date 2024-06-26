package com.library.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 60)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idCareer", nullable = false)
    private Career idCareer;

    @Column(name = "dteBirth", nullable = false)
    private LocalDate dteBirth;

    @Column(name = "carnet", nullable = false, length = 10)
    private String carnet;

    @Column(name = "status")
    private Integer status;

    @OneToMany(mappedBy = "carnet")
    @JsonIgnore
    private Set<Loan> loans = new LinkedHashSet<>();

}