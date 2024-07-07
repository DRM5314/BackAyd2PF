package library.service.service;

import com.library.dto.career.CareerCreateRequestDTO;
import com.library.dto.career.CareerResponseDTO;
import com.library.dto.career.CareerUpdateRequestDTO;
import com.library.exceptions.DuplicatedEntityException;
import com.library.exceptions.NotFoundException;
import com.library.exceptions.ServiceException;
import com.library.model.Career;
import com.library.repository.CareerRepository;
import com.library.service.career.CareerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.List;
public class CareerServiceImplTest {
    private CareerServiceImpl careerService;
    private CareerRepository careerRepository = mock(CareerRepository.class);
    private Career CAREER;

    private final String NAME = "CAREER";
    private final Long ID = 1L;

    @BeforeEach
    public void SetUp(){
        careerService = new CareerServiceImpl(careerRepository);

        CAREER = new Career();
        CAREER.setName(NAME);
        CAREER.setId(ID);
    }

    @Test
    public void saveWithNotNameExist() throws ServiceException {
        CareerResponseDTO expected = new CareerResponseDTO(this.CAREER);

        CareerCreateRequestDTO dtoCreate = new CareerCreateRequestDTO(NAME);

        when(careerRepository.existsByName(NAME)).thenReturn(false);
        when(careerRepository.save(any(Career.class))).thenReturn(this.CAREER);
        CareerResponseDTO actually = careerService.save(dtoCreate.getName());

        assertThat(actually).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    public void saveWithNameDuplicated() throws ServiceException{
        when(careerRepository.existsByName(NAME)).thenReturn(true);
        CareerCreateRequestDTO dtoCreate = new CareerCreateRequestDTO(NAME);
        assertThrows(DuplicatedEntityException.class,()->careerService.save(dtoCreate.getName()));
    }

    @Test
    public void findByIdDtoExists() throws ServiceException{
        CareerResponseDTO expected = new CareerResponseDTO(this.CAREER);
        when(careerRepository.findById(ID)).thenReturn(Optional.of(this.CAREER));
        CareerResponseDTO actually = careerService.findByIdDto(ID);
        assertThat(actually).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    public void findByIdNotExist() throws ServiceException{
        when(careerRepository.findById(ID)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,()->careerService.findByIdDto(ID));
    }

    @Test
    public void findByIdNotDtoExist() throws ServiceException{
        when(careerRepository.findById(ID)).thenReturn(Optional.of(this.CAREER));
        Career actually = careerService.findByIdNoDto(ID);
        assertThat(actually).isEqualToComparingFieldByFieldRecursively(this.CAREER);
    }
    @Test
    public void findByIdNotDtoExist_NoExist() throws ServiceException{
        when(careerRepository.findById(ID)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,()->careerService.findByIdNoDto(ID));
    }
    @Test
    public void update() throws ServiceException{

        when(careerRepository.findById(ID)).thenReturn(Optional.of(CAREER));
        when(careerRepository.existsByNameAndIdIsNot(NAME,ID)).thenReturn(false);


        CareerUpdateRequestDTO updateDto = new CareerUpdateRequestDTO(ID,"C2");
        CAREER.setName("C2");
        when(careerRepository.save(CAREER)).thenReturn(CAREER);

        CareerResponseDTO expected = new CareerResponseDTO(CAREER);
        CareerResponseDTO actually = careerService.update(ID,updateDto);
        assertThat(actually).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    void updateWithExistName(){
        CAREER.setName(NAME);
        when(careerRepository.findById(ID)).thenReturn(Optional.of(CAREER));
        when(careerRepository.existsByNameAndIdIsNot(NAME,ID)).thenReturn(true);
        CareerUpdateRequestDTO updateDto = new CareerUpdateRequestDTO(ID,NAME);
        assertThrows(DuplicatedEntityException.class,()->careerService.update(ID,updateDto));
    }
    @Test
    void findAll(){
        List<Career> list = List.of(CAREER);
        when(careerRepository.findAll()).thenReturn(list);
        List<CareerResponseDTO> expected = List.of(new CareerResponseDTO(CAREER));
        List<CareerResponseDTO> actually = careerService.findAll();
        assertThat(actually.size()).isEqualTo(expected.size());
        for (int i = 0; i < actually.size(); i++) {
            assertThat(actually.get(i)).isEqualToComparingFieldByFieldRecursively(expected.get(i));
        }
    }
}
