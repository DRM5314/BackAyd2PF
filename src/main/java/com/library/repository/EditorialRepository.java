package com.library.repository;

import com.library.model.Editorial;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface EditorialRepository extends CrudRepository<Editorial,Long> {
    @Override
    List<Editorial> findAll();
    Optional<Editorial> findById(Long id);
    Boolean existsByName(String name);
    Boolean existsByNameAndIdIsNot(String name,Long Id);

}
