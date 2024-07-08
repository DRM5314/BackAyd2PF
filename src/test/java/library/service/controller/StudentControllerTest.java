package library.service.controller;
import com.library.controller.StudentController;
import com.library.controller.exceptionhandler.GlobalExceptionHandler;
import com.library.dto.student.StudentCreateRequestDTO;
import com.library.dto.student.StudentResponseDTO;
import com.library.dto.student.StudentUpdateRequestDTO;
import com.library.exceptions.DuplicatedEntityException;
import com.library.exceptions.NotFoundException;
import com.library.model.Career;
import com.library.model.Student;
import com.library.service.student.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.util.List;
@ContextConfiguration (classes = {StudentController.class, StudentService.class, GlobalExceptionHandler.class})
public class StudentControllerTest extends AbstractMvcTest{
    @MockBean
    StudentService studentService;

    private final Long ID = 1L;
    private final String NAME = "DAVID";
    private final LocalDate DATE_BIRD = LocalDate.now();
    private final String CARNET = "201632145";
    private final Integer STATUS = 1;
    private Career CAREER;
    private final Long CARRER_ID = 1L;
    private final String CARRER_NAME = "CARRER";
    private Student STUDENT;

    @BeforeEach
    void setUp() {
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
    @WithMockUser(authorities = {"ADMIN"})
    void createWithCarnetExist() throws Exception{
        Mockito.doThrow(DuplicatedEntityException.class).when(studentService).save(any(StudentCreateRequestDTO.class));
        mockMvc.perform(post("/student")
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void createSuccessful() throws Exception{
        StudentCreateRequestDTO requestDTO = new StudentCreateRequestDTO(NAME,CARRER_ID,DATE_BIRD,CARNET);
        StudentResponseDTO expected = new StudentResponseDTO(STUDENT);

        Mockito.when(studentService.save(any(StudentCreateRequestDTO.class))).thenReturn(expected);
        mockMvc.perform(post("/student")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDTO))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    StudentResponseDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(), StudentResponseDTO.class);
                    assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
                });
                ;
    }
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void findByCarneNotExist() throws Exception{
        Mockito.doThrow(NotFoundException.class).when(studentService).findStudentByCarnet(CARNET);
        mockMvc.perform(get("/student/"+CARNET)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void findByCarneSuccessful() throws Exception{
        StudentResponseDTO expected = new StudentResponseDTO(STUDENT);
        Mockito.when(studentService.findStudentByCarnet(CARNET)).thenReturn(expected);
        mockMvc.perform(get("/student/"+CARNET)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    StudentResponseDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(), StudentResponseDTO.class);
                    assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
                });
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void updateNotExist() throws Exception{
        StudentUpdateRequestDTO request = new StudentUpdateRequestDTO(NAME,CARRER_ID,DATE_BIRD,STATUS);
        Mockito.doThrow(NotFoundException.class).when(studentService).update(CARNET, request);
        mockMvc.perform(put("/student")
                .with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void updateSuccessful() throws Exception{
        String newName = "Student new name";
        Long newCareerId = 2L;
        LocalDate newDateBird = LocalDate.now();
        Integer newStatus = 0;


        STUDENT.setName(newName);
        CAREER.setId(newCareerId);
        STUDENT.setIdCareer(CAREER);
        STUDENT.setDteBirth(newDateBird);
        STUDENT.setStatus(newStatus);

        StudentUpdateRequestDTO request = new StudentUpdateRequestDTO(newName,newCareerId,newDateBird,newStatus);
        StudentResponseDTO expected = new StudentResponseDTO(STUDENT);
        Mockito.when(studentService.update(any(String.class),any(StudentUpdateRequestDTO.class))).thenReturn(expected);
        mockMvc.perform(put("/student/{carne}",CARNET)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    StudentResponseDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(), StudentResponseDTO.class);
                    assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
                });
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void findAll() throws Exception{
        List<StudentResponseDTO> expected = List.of(new StudentResponseDTO(STUDENT));
        Mockito.when(studentService.findAll()).thenReturn(expected);
        mockMvc.perform(get("/student/findAll")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    List<StudentResponseDTO> actual = objectMapper.readValue(result.getResponse().getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, StudentResponseDTO.class));
                    assertThat(actual.size()).isEqualTo(expected.size());
                    for (int i = 0; i < actual.size(); i++) {
                        assertThat(actual.get(i)).isEqualToComparingFieldByFieldRecursively(expected.get(i));
                    }
                });
    }
}
