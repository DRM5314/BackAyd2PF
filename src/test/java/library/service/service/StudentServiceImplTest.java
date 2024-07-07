package library.service.service;

import com.library.dto.student.StudentCreateRequestDTO;
import com.library.dto.student.StudentResponseDTO;
import com.library.dto.student.StudentUpdateRequestDTO;
import com.library.exceptions.DuplicatedEntityException;
import com.library.exceptions.NotFoundException;
import com.library.exceptions.ServiceException;
import com.library.model.Career;
import com.library.model.Student;
import com.library.repository.StudentRepository;
import com.library.service.career.CareerService;
import com.library.service.student.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class StudentServiceImplTest {
    private StudentServiceImpl studentService;
    private StudentRepository studentRepository = Mockito.mock(StudentRepository.class);
    private CareerService careerService = Mockito.mock(CareerService.class);

    private Long ID = 1L;
    private String NAME = "DAVID";
    private LocalDate DATE_BIRD = LocalDate.now();
    private String CARNET = "201632145";
    private Integer STATUS = 1;
    private Career CAREER;
    private Long CARRER_ID = 1L;
    private String CARRER_NAME = "CARRER";
    private Student STUDENT;

    @BeforeEach
    void setUp() {
        studentService = new StudentServiceImpl(studentRepository,careerService);

        CAREER = new Career();
        CAREER.setId(CARRER_ID);
        CAREER.setName(CARRER_NAME);

        STUDENT = new Student();
        STUDENT.setId(ID);
        STUDENT.setName(NAME);
        STUDENT.setIdCareer(CAREER);
        STUDENT.setDteBirth(DATE_BIRD);
        STUDENT.setCarnet(CARNET);
        STUDENT.setStatus(STATUS);
    }

    @Test
    void save() throws ServiceException {
        StudentCreateRequestDTO requestDTO = new StudentCreateRequestDTO(NAME,CARRER_ID,DATE_BIRD,CARNET);

        when(studentRepository.existsByName(NAME)).thenReturn(false);
        when(studentRepository.existsByCarnet(CARNET)).thenReturn(false);
        when(careerService.findByIdNoDto(CARRER_ID)).thenReturn(CAREER);
        when(studentRepository.save(any(Student.class))).thenReturn(STUDENT);

        StudentResponseDTO expected = new StudentResponseDTO(STUDENT);
        StudentResponseDTO actually = studentService.save(requestDTO);

        assertThat(actually).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    public void saveWithStudentNameDuplicated() throws ServiceException{
        StudentCreateRequestDTO requestDTO = new StudentCreateRequestDTO(NAME,CARRER_ID,DATE_BIRD,CARNET);

        when(studentRepository.existsByName(NAME)).thenReturn(true);
        assertThrows(DuplicatedEntityException.class,()->studentService.save(requestDTO));
    }
    @Test
    public void saveWithStudentCarnetDuplicated() throws ServiceException{
        StudentCreateRequestDTO requestDTO = new StudentCreateRequestDTO(NAME,CARRER_ID,DATE_BIRD,CARNET);

        when(studentRepository.existsByCarnet(CARNET)).thenReturn(true);
        assertThrows(DuplicatedEntityException.class,()->studentService.save(requestDTO));
    }

    @Test
    void findStudentByCarnet() throws ServiceException{
        StudentResponseDTO expected = new StudentResponseDTO(STUDENT);

        when(studentRepository.findByCarnet(CARNET)).thenReturn(Optional.of(STUDENT));

        StudentResponseDTO actually = studentService.findStudentByCarnet(CARNET);
        assertThat(actually).isEqualToComparingFieldByFieldRecursively(expected);
    }
    @Test
    void findStudentByCarnetNoExist() throws ServiceException{
        when(studentRepository.findByCarnet(CARNET)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,()->studentService.findStudentByCarnet(CARNET));
    }

    @Test
    void isActive() throws ServiceException {
        when(studentRepository.findByCarnet(CARNET)).thenReturn(Optional.of(STUDENT));
        assertThat(studentService.isActive(CARNET)).isTrue();
        STUDENT.setStatus(0);
        assertThat(studentService.isActive(CARNET)).isFalse();
    }
    @Test
    void isActiveNotExist() throws ServiceException {
        when(studentRepository.findByCarnet(CARNET)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,()->studentService.isActive(CARNET));
    }

    @Test
    void update() throws ServiceException{
        when(studentRepository.findByCarnet(CARNET)).thenReturn(Optional.of(STUDENT));
        when(careerService.findByIdNoDto(CARRER_ID)).thenReturn(CAREER);
        when(studentRepository.existsByNameAndCarnetIsNot(NAME,CARNET)).thenReturn(false);
        when(studentRepository.save(STUDENT)).thenReturn(STUDENT);

        STUDENT.setStatus(1);
        STUDENT.setName("DAVID");
        StudentResponseDTO expected = new StudentResponseDTO(STUDENT);

        StudentUpdateRequestDTO updateDto = new StudentUpdateRequestDTO("DAVID",CARRER_ID,DATE_BIRD,1);
        StudentResponseDTO actually = studentService.update(CARNET,updateDto);


        assertThat(actually).isEqualToComparingFieldByFieldRecursively(expected);
        STUDENT.setStatus(STATUS);
        STUDENT.setName(NAME);
    }
    @Test
    void updateFailNameExist() throws ServiceException{
        when(studentRepository.findByCarnet(CARNET)).thenReturn(Optional.of(STUDENT));
        when(careerService.findByIdNoDto(CARRER_ID)).thenReturn(CAREER);
        when(studentRepository.existsByNameAndCarnetIsNot(NAME,CARNET)).thenReturn(true);

        STUDENT.setStatus(1);
        STUDENT.setName("DAVID");

        StudentUpdateRequestDTO updateDto = new StudentUpdateRequestDTO("DAVID",CARRER_ID,DATE_BIRD,1);

        assertThrows(DuplicatedEntityException.class,()->studentService.update(CARNET,updateDto));
    }

    @Test
    void findAll(){
        List<Student> list = List.of(STUDENT);
        when(studentRepository.findAll()).thenReturn(list);
        List<StudentResponseDTO> expected = List.of(new StudentResponseDTO(STUDENT));
        List<StudentResponseDTO> actually = studentService.findAll();
        assertThat(actually.size()).isEqualTo(expected.size());
        for (int i = 0; i < actually.size(); i++) {
            assertThat(actually.get(i)).isEqualToComparingFieldByFieldRecursively(expected.get(i));
        }
    }
}