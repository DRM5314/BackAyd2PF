package com.library.repository;

import com.library.model.Book;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends CrudRepository<Book,Long> {
    @Override
    List<Book> findAll();
    Optional <Book> findByCode(String code);
    Boolean existsByTitleAndCodeIsNot(String title, String code);
    Boolean existsByTitle(String title);
}
