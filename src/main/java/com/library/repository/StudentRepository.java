package com.library.repository;

import com.library.model.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends CrudRepository<Student,Long> {
    Optional<Student> findById(Long aLong);

    Optional<Student> findByCarnet(String carne);
    @Override
    List<Student> findAll();
    Boolean existsByName(String name);
    Boolean existsByNameAndCarnetIsNot(String name,String carnet);
    Boolean existsByCarnet(String carne);
}
