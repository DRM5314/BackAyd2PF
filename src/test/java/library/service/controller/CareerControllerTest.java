package library.service.controller;

import com.library.controller.CareerController;
import com.library.controller.exceptionhandler.GlobalExceptionHandler;
import com.library.dto.book.BookUpdateRequestDTO;
import com.library.dto.career.CareerResponseDTO;
import com.library.dto.career.CareerUpdateRequestDTO;
import com.library.exceptions.DuplicatedEntityException;
import com.library.exceptions.NotFoundException;
import com.library.model.Career;
import com.library.service.career.CareerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.ArgumentMatchers.any;
import java.util.List;
@ContextConfiguration(classes = {CareerController.class, CareerService.class, GlobalExceptionHandler.class})
public class CareerControllerTest extends AbstractMvcTest{
    private final Long ID = 1L;
    private final String NAME = "Career1";
    private Career CAREER;
    @MockBean
    private CareerService careerService;

    @BeforeEach
    public void setUp(){
        this.CAREER = new Career();
        this.CAREER.setId(ID);
        this.CAREER.setName(NAME);
    }

    @Test
    @WithMockUser("ADMIN")
    void testCreateRepeated() throws Exception{
        Mockito.doThrow(DuplicatedEntityException.class).when(careerService).save(NAME);
        mockMvc.perform(post("/career")
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }
    @Test
    @WithMockUser("ADMIN")
    void testCreate() throws Exception{
        CareerResponseDTO expected = new CareerResponseDTO(CAREER);
        Mockito.when(careerService.save(any(String.class))).thenReturn(expected);
        mockMvc.perform(post("/career")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(NAME))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    CareerResponseDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(), CareerResponseDTO.class);
                    assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
                });
    }
    @Test
    @WithMockUser(roles = {"ADMIN","USER"})
    void testFindAll() throws Exception{
        List<CareerResponseDTO> expected = List.of(new CareerResponseDTO(CAREER));
        Mockito.when(careerService.findAll()).thenReturn(expected);
        mockMvc.perform(get("/career/findAll"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    List<CareerResponseDTO> actual = objectMapper.readValue(result.getResponse().getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, CareerResponseDTO.class));
                    assertThat(actual.size()).isEqualTo(expected.size());
                    for (int i = 0; i < expected.size(); i++) {
                        assertThat(actual.get(i)).isEqualToComparingFieldByFieldRecursively(expected.get(i));
                    }
                });
    }
    @Test
    @WithMockUser("ADMIN")
    void testUpdateNotFound() throws Exception{
        Mockito.doThrow(NotFoundException.class).when(careerService).update(ID,new CareerUpdateRequestDTO(ID,NAME));
        mockMvc.perform(put("/career")
                .with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser("ADMIN")
    void testUpdateSuccessful() throws Exception{

        String newName = "Career2";
        CAREER.setName(newName);

        CareerResponseDTO expected = new CareerResponseDTO(CAREER);
        Mockito.when(careerService.update(any(Long.class),any(CareerUpdateRequestDTO.class))).thenReturn(expected);
        mockMvc.perform(put("/career/{careerId}",ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CareerUpdateRequestDTO(ID,newName)))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    CareerResponseDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(), CareerResponseDTO.class);
                    assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
                });
    }
}
