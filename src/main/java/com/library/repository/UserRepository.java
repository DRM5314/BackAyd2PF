package com.library.repository;

import com.library.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
public interface UserRepository extends CrudRepository<User,Long> {
    Optional <User> findByUsername(String userName);
}
