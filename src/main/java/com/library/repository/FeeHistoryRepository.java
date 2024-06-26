package com.library.repository;

import com.library.model.FeeUpdateHistory;
import org.springframework.data.repository.CrudRepository;

public interface FeeHistoryRepository extends CrudRepository<FeeUpdateHistory,Long> {
    FeeUpdateHistory findTopByOrderByIdDesc();
}
