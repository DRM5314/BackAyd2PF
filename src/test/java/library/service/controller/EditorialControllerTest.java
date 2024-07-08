package library.service.controller;

import com.library.controller.EditorialController;
import com.library.controller.exceptionhandler.GlobalExceptionHandler;
import com.library.dto.book.BookUpdateRequestDTO;
import com.library.dto.career.CareerResponseDTO;
import com.library.dto.editorial.EditorialResponseDTO;
import com.library.dto.editorial.EditorialUpdateRequestDTO;
import com.library.exceptions.DuplicatedEntityException;
import com.library.exceptions.NotFoundException;
import com.library.model.Editorial;
import com.library.service.editorial.EditorialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;
@ContextConfiguration(classes = {EditorialController.class, EditorialService.class, GlobalExceptionHandler.class})
public class EditorialControllerTest extends AbstractMvcTest{
    private final Long ID = 1L;
    private final String NAME = "Editorial1";
    private Editorial EDITORIAL;

    @MockBean
    private EditorialService editorialService;

    @BeforeEach
    public void setUp(){
        this.EDITORIAL = new Editorial();
        this.EDITORIAL.setId(ID);
        this.EDITORIAL.setName(NAME);
    }

    @Test
    @WithMockUser("ADMIN")
    void testCreteCareerError() throws Exception{
        Mockito.doThrow(DuplicatedEntityException.class).when(editorialService).save(NAME);
        mockMvc.perform(post("/editorial")
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }
    @Test
    @WithMockUser("ADMIN")
    void testCreateCareerSuccessful() throws Exception{
        EditorialResponseDTO expected = new EditorialResponseDTO(EDITORIAL);
        when(editorialService.save(any(String.class))).thenReturn(expected);
        mockMvc.perform(post("/editorial")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(NAME))
                .with(csrf()))
                .andExpect(status().isOk());
    }@Test
    @WithMockUser("ADMIN")
    void testFindAll() throws Exception{
        List<EditorialResponseDTO> expected = List.of(new EditorialResponseDTO(EDITORIAL));
        when(editorialService.findAll()).thenReturn(expected);

        mockMvc.perform(get("/editorial/findAll"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    List<EditorialResponseDTO> actual = objectMapper.readValue(result.getResponse().getContentAsString(), objectMapper.getTypeFactory().constructCollectionType(List.class, EditorialResponseDTO.class));
                    assertThat(actual.size()).isEqualTo(expected.size());
                    for (int i = 0; i < actual.size(); i++) {
                        assertThat(actual.get(i)).isEqualToComparingFieldByFieldRecursively(expected.get(i));
                    }
                });
    }

    @Test
    @WithMockUser("ADMIN")
    void testUpdateNotExist() throws Exception{
        Mockito.doThrow(NotFoundException.class).when(editorialService).update(any(Long.class),any(EditorialUpdateRequestDTO.class));
        mockMvc.perform(put("/editorial/"+ID)
                .with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser("ADMIN")
    void updateSuccessful() throws Exception{
        String newName = "Name Update";

        EDITORIAL.setName(newName);

        EditorialUpdateRequestDTO update = new EditorialUpdateRequestDTO(ID, newName);

        when(editorialService.update(any(Long.class),any(EditorialUpdateRequestDTO.class))).thenReturn(new EditorialResponseDTO(EDITORIAL));

        EditorialResponseDTO expected = new EditorialResponseDTO(EDITORIAL);
        mockMvc.perform(put("/editorial/"+ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    EditorialResponseDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(),EditorialResponseDTO.class);
                    assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
                });

    }
}
