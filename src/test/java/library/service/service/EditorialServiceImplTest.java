package library.service.service;

import com.library.dto.editorial.EditorialCreateRequestDTO;
import com.library.dto.editorial.EditorialResponseDTO;
import com.library.dto.editorial.EditorialUpdateRequestDTO;
import com.library.exceptions.DuplicatedEntityException;
import com.library.exceptions.NotFoundException;
import com.library.exceptions.ServiceException;
import com.library.model.Editorial;
import com.library.repository.EditorialRepository;
import com.library.service.editorial.EditorialServiceImpl;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.List;

class EditorialServiceImplTest {
    private final Long ID = 1L;
    private final String NAME = "editorial1";
    private EditorialServiceImpl editorialServiceImpl;
    private EditorialRepository editorialRepository = mock(EditorialRepository.class);

    private Editorial editorial;
    @BeforeEach
    void setUp() {
        editorialServiceImpl = new EditorialServiceImpl(editorialRepository);

        editorial = new Editorial();
        editorial.setName(NAME);
        editorial.setId(ID);
    }

    @Test
    void save() throws Exception{
        Editorial editorial = new Editorial();
        editorial.setId(ID);
        editorial.setName(NAME);

        EditorialResponseDTO expected = new EditorialResponseDTO(editorial);
        when(editorialRepository.existsByName(any(String.class))).thenReturn(false);

        when(editorialRepository.save(any(Editorial.class))).thenReturn(this.editorial);

        EditorialCreateRequestDTO dtoCreate = new EditorialCreateRequestDTO(NAME);
        EditorialResponseDTO actual = editorialServiceImpl.save(dtoCreate.getName());
        assertAll(
                ()->assertEquals(expected.getName(),actual.getName()),
                ()->assertEquals(expected.getId(),actual.getId())
        );
    }

    @Test
    void saveWithExistError() throws Exception{
        when(editorialRepository.existsByName(NAME)).thenReturn(true);
        EditorialCreateRequestDTO createDto = new EditorialCreateRequestDTO(NAME);
        assertThrows(DuplicatedEntityException.class,()->editorialServiceImpl.save(createDto.getName()));
    }

    @Test
    void findByCodeExist() throws ServiceException {
        when(editorialRepository.findById(ID)).thenReturn(Optional.of(this.editorial));
        EditorialResponseDTO expected = new EditorialResponseDTO(this.editorial);
        EditorialResponseDTO actually = editorialServiceImpl.findByCode(ID);
        assertAll(
                ()->assertEquals(expected.getId(),actually.getId()),
                ()->assertEquals(expected.getName(),actually.getName())
        );
    }

    @Test
    void findByCodeNotExist()throws ServiceException{
        when(editorialRepository.findById(ID)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,()->editorialServiceImpl.findByCode(ID));
    }

    @Test
    void findAll() {
        List<Editorial> editorials = List.of(editorial);
        when(editorialRepository.findAll()).thenReturn(editorials);
        List<EditorialResponseDTO> expected = List.of(new EditorialResponseDTO(editorial));
        List<EditorialResponseDTO> actually = editorialServiceImpl.findAll();
        assertThat(actually.size()).isEqualTo(expected.size());
        for (int i = 0; i < actually.size(); i++) {
            assertThat(actually.get(i)).isEqualToComparingFieldByFieldRecursively(expected.get(i));
        }
    }

    @Test
    void findByCodeNotDto() throws ServiceException{
        Editorial expected = new Editorial();
        expected.setId(ID);
        expected.setName(NAME);

        when(editorialRepository.findById(ID)).thenReturn(Optional.of(this.editorial));
        Editorial actually = editorialServiceImpl.findByCodeNotDto(ID);

        assertAll(
                ()->assertEquals(expected.getId(),actually.getId()),
                ()->assertEquals(expected.getName(),actually.getName())
        );
    }

    @Test
    void finByCodeNotDtoNotFound() throws ServiceException{
        when(editorialRepository.findById(ID)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,()->editorialServiceImpl.findByCodeNotDto(ID));
    }
    @Test
    public void updateTest() throws ServiceException{
        when(editorialRepository.existsByNameAndIdIsNot("CARLOS",ID)).thenReturn(false);
        when(editorialRepository.existsByName("CARLOS")).thenReturn(false);
        when(editorialRepository.findById(ID)).thenReturn(Optional.of(editorial));
        editorial.setName("CARLOS");
        when(editorialRepository.save(editorial)).thenReturn(editorial);

        EditorialUpdateRequestDTO updateDtp = new EditorialUpdateRequestDTO(ID,"CARLOS");

        EditorialResponseDTO expected = new EditorialResponseDTO(editorial);
        EditorialResponseDTO actually = editorialServiceImpl.update(ID,updateDtp);

        assertThat(actually).isEqualToComparingFieldByFieldRecursively(expected);

    }
    @Test
    void updateWithNameRepeated() throws ServiceException{
        editorial.setName(NAME);
        when(editorialRepository.existsByNameAndIdIsNot(NAME,ID)).thenReturn(true);

        EditorialUpdateRequestDTO updateDtp = new EditorialUpdateRequestDTO(ID,NAME);
        assertThrows(DuplicatedEntityException.class,()->editorialServiceImpl.update(ID,updateDtp));
    }
}