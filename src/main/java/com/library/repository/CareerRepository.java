package com.library.repository;

import com.library.model.Career;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CareerRepository extends CrudRepository<Career,Long> {
    @Override
    List<Career> findAll();
    Optional<Career> findById(Long aLong);
    Boolean existsByName(String name);
    Boolean existsByNameAndIdIsNot(String name, Long id);
}
