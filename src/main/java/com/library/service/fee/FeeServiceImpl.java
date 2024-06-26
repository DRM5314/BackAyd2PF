package com.library.service.fee;

import com.library.model.FeeUpdateHistory;
import com.library.repository.FeeHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class FeeServiceImpl implements FeeService{
    private FeeHistoryRepository feeHistoryRepository;
    @Autowired
    public FeeServiceImpl(FeeHistoryRepository feeHistoryRepository){
        this.feeHistoryRepository = feeHistoryRepository;
    }
    @Override
    public FeeUpdateHistory save(FeeUpdateHistory history) {
        return feeHistoryRepository.save(history);
    }

    @Override
    public LocalDate findLast() {
        FeeUpdateHistory returns = feeHistoryRepository.findTopByOrderByIdDesc();
        if(returns != null){
            return returns.getDate();
        }
        return LocalDate.now();
    }
}
