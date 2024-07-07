package library.service.service;

import com.library.model.FeeUpdateHistory;
import com.library.repository.FeeHistoryRepository;
import com.library.service.fee.FeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FeeServiceImplTest {
    private FeeServiceImpl feeServiceImpl;
    private final FeeHistoryRepository feeHistoryRepository = mock(FeeHistoryRepository.class);
    private FeeUpdateHistory FEE_UPDATE_HISTORY;
    private Long ID = 1L;
    private Integer TOTAL_LOANS = 1;
    private LocalDate DATE = LocalDate.parse("2024-01-01");
    @BeforeEach
    void setUp(){
        feeServiceImpl = new FeeServiceImpl(feeHistoryRepository);
        FEE_UPDATE_HISTORY = new FeeUpdateHistory();
        FEE_UPDATE_HISTORY.setId(ID);
        FEE_UPDATE_HISTORY.setTotalLoans(TOTAL_LOANS);
        FEE_UPDATE_HISTORY.setDate(DATE);
    }

    @Test
    void save(){
        when(feeHistoryRepository.save(FEE_UPDATE_HISTORY)).thenReturn(FEE_UPDATE_HISTORY);
        FeeUpdateHistory actual = feeServiceImpl.save(FEE_UPDATE_HISTORY);
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(FEE_UPDATE_HISTORY);
    }
    @Test
    void findLastNotRegister(){
        when(feeHistoryRepository.findTopByOrderByIdDesc()).thenReturn(null);
        LocalDate expected = LocalDate.now();
        LocalDate actually = feeServiceImpl.findLast();
        assertEquals(expected,actually);
    }
    @Test
    void findLast(){
        when(feeHistoryRepository.findTopByOrderByIdDesc()).thenReturn(FEE_UPDATE_HISTORY);
        LocalDate expected = FEE_UPDATE_HISTORY.getDate();
        LocalDate actually = feeServiceImpl.findLast();
        assertEquals(expected,actually);
    }
}
