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

public class CareerTest {
    private CareerServiceImpl careerService;
    private CareerRepository careerRepository = mock(CareerRepository.class);
    private Career career;

    private final String NAME = "CAREER";
    private final Long ID = 1L;

    @BeforeEach
    public void SetUp(){
        careerService = new CareerServiceImpl(careerRepository);

        career = new Career();
        career.setName(NAME);
        career.setId(ID);
    }

    @Test
    public void saveWithNotNameExist() throws ServiceException {
        CareerResponseDTO expected = new CareerResponseDTO(this.career);

        CareerCreateRequestDTO dtoCreate = new CareerCreateRequestDTO(NAME);

        when(careerRepository.existsByName(NAME)).thenReturn(false);
        when(careerRepository.save(any(Career.class))).thenReturn(this.career);
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
        CareerResponseDTO expected = new CareerResponseDTO(this.career);
        when(careerRepository.findById(ID)).thenReturn(Optional.of(this.career));
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
        when(careerRepository.findById(ID)).thenReturn(Optional.of(this.career));
        Career actually = careerService.findByIdNoDto(ID);
        assertThat(actually).isEqualToComparingFieldByFieldRecursively(this.career);
    }
    @Test
    public void findByIdNotDtoExist_NoExist() throws ServiceException{
        when(careerRepository.findById(ID)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,()->careerService.findByIdNoDto(ID));
    }
    @Test
    public void update() throws ServiceException{

        when(careerRepository.findById(ID)).thenReturn(Optional.of(career));
        when(careerRepository.existsByNameAndIdIsNot(NAME,ID)).thenReturn(false);


        CareerUpdateRequestDTO updateDto = new CareerUpdateRequestDTO(ID,"C2");
        career.setName("C2");
        when(careerRepository.save(career)).thenReturn(career);

        CareerResponseDTO expected = new CareerResponseDTO(career);
        CareerResponseDTO actually = careerService.update(ID,updateDto);
        assertThat(actually).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    void updateWithExistName(){
        career.setName(NAME);
        when(careerRepository.findById(ID)).thenReturn(Optional.of(career));
        when(careerRepository.existsByNameAndIdIsNot(NAME,ID)).thenReturn(true);
        CareerUpdateRequestDTO updateDto = new CareerUpdateRequestDTO(ID,NAME);
        assertThrows(DuplicatedEntityException.class,()->careerService.update(ID,updateDto));
    }
}
