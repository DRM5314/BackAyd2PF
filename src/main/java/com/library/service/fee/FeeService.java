package com.library.service.fee;

import com.library.model.FeeUpdateHistory;

import java.time.LocalDate;
import java.util.Optional;

public interface FeeService {
    FeeUpdateHistory save(FeeUpdateHistory history);
    LocalDate findLast();
}
