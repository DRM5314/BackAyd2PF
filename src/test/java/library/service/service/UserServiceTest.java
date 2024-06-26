package library.service.service;

import com.library.dto.user.UserCreateRequestDTO;
import com.library.dto.user.UserResponseDto;
import com.library.enums.Rol;
import com.library.model.User;
import com.library.repository.UserRepository;
import com.library.service.user.UserServiceImpl;
import com.library.exceptions.DuplicatedEntityException;
import com.library.exceptions.NotFoundException;
import com.library.exceptions.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private final Long ID = 1L;
    private final String NAME = "David";
    private final String EMAIL = "david@gmail.com";
    private final String USER_NAME = "david123";
    private final String PASSWORD = "bbcito123";
    private final int STATUS = 1;
    private final Rol ROL = Rol.ADMIN;

    private final String USER_NAME_FIND_EXIST = USER_NAME + "F";
    private final String USER_NAME_NOT_FOUD = USER_NAME + "NF";
    private final String USER_NAME_SAVE = USER_NAME + "S";
    private final String USER_NAME_SAVE_DUPLICATED = USER_NAME + "D";

    private UserServiceImpl userService;
    private UserRepository mockRepositoryUser = mock(UserRepository.class);

    private User user;
    @BeforeEach
    void setUp(){
        userService = new UserServiceImpl(mockRepositoryUser);

        user = new User();
        user.setId(ID);
        user.setEmail(EMAIL);
        user.setName(NAME);
        user.setPassword(PASSWORD);
        user.setStatus(STATUS);
        user.setRole(ROL);
    }

    @Test
    public void findUserByUserNameExist()throws ServiceException{
        //UserName exist
        user.setUsername(USER_NAME_FIND_EXIST);
        when(mockRepositoryUser.findByUsername(USER_NAME_FIND_EXIST)).thenReturn(Optional.of(user));

        User userExpected = new User();
        userExpected.setId(ID);
        userExpected.setName(NAME);
        userExpected.setEmail(EMAIL);
        userExpected.setUsername(USER_NAME_FIND_EXIST);
        userExpected.setPassword(PASSWORD);
        userExpected.setStatus(STATUS);
        userExpected.setRole(ROL);

        UserResponseDto userExpected1 = new UserResponseDto(userExpected);
        UserResponseDto user = userService.findUserByUserName(USER_NAME_FIND_EXIST);

        assertAll(
                ()->assertEquals(userExpected1.getUserId(),user.getUserId()),
                ()->assertEquals(userExpected1.getUsername(),user.getUsername()),
                ()->assertEquals(userExpected1.getName(),user.getName()),
                ()->assertEquals(userExpected1.getRole(),user.getRole()),
                ()->assertEquals(userExpected1.getEmail(),user.getEmail()),
                ()->assertEquals(userExpected1.getUsername(),user.getUsername()),
                ()->assertEquals(userExpected1.getStatus(),user.getStatus())
        );
    }

    @Test
    public void findUserByUserNameNotExist() throws ServiceException{
        //Save user
        user.setUsername(USER_NAME_SAVE);
        when(mockRepositoryUser.findByUsername(USER_NAME_SAVE)).thenReturn(Optional.empty());
        when(mockRepositoryUser.save(any(User.class))).thenReturn(user);

        //UserName dont exist
        when(mockRepositoryUser.findByUsername(USER_NAME_NOT_FOUD)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.findUserByUserName(USER_NAME_NOT_FOUD));
    }
    @Test
    public void createUserDontExists() throws ServiceException{
        user.setUsername(USER_NAME_SAVE);
        UserResponseDto expected = new UserResponseDto(user);

        when(mockRepositoryUser.findByUsername(USER_NAME_SAVE)).thenReturn(Optional.empty());
        when(mockRepositoryUser.save(any(User.class))).thenReturn(user);

        UserCreateRequestDTO userEntry = new UserCreateRequestDTO(Rol.ADMIN,NAME,EMAIL,USER_NAME_SAVE,PASSWORD);
        UserResponseDto userSaved = userService.save(userEntry);
        assertAll(
                ()->assertEquals(expected.getUserId(),userSaved.getUserId()),
                ()->assertEquals(expected.getUsername(),userSaved.getUsername()),
                ()->assertEquals(expected.getName(),userSaved.getName()),
                ()->assertEquals(expected.getRole(),userSaved.getRole()),
                ()->assertEquals(expected.getEmail(),userSaved.getEmail()),
                ()->assertEquals(expected.getUsername(),userSaved.getUsername()),
                ()->assertEquals(expected.getStatus(),userSaved.getStatus())
        );
    }

    @Test
    public void createUserWithExist() throws ServiceException{
        //Save user duplicated
        user.setUsername(USER_NAME_SAVE);
        when(mockRepositoryUser.findByUsername(USER_NAME_SAVE_DUPLICATED)).thenReturn(Optional.of(user));
        UserCreateRequestDTO userEntry = new UserCreateRequestDTO(Rol.ADMIN,NAME,EMAIL,USER_NAME_SAVE_DUPLICATED,PASSWORD);
        assertThrows(DuplicatedEntityException.class,()-> userService.save(userEntry));
    }
}
