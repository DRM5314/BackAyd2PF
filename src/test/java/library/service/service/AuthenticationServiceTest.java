package library.service.service;

import com.library.dto.user.SignInRequestDTO;
import com.library.dto.user.UserCreateRequestDTO;
import com.library.dto.user.UserResponseDto;
import com.library.enums.Rol;
import com.library.model.User;
import com.library.service.security.AuthenticationServiceImpl;
import com.library.service.security.JwtService;
import com.library.service.user.UserService;
import com.library.exceptions.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticationServiceTest {
    private final Long ID = 1L;
    private final String NAME =  "DAVID";
    private final Rol ROLE = Rol.ADMIN;
    private final String EMAIL = "david@gamil.com";
    private final String USER_NAME = "david123";
    private final int STATUS = 1;
    private final String PASSWORD = "bbcito123";
    private final String token = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoiZGF2aWQxMjMiLCJyb2wiOiJBRE1JTiIsImp0aSI6InctNjYzMjMwMTg2NDU2NDYwNDE4NyIsInN1YiI6ImRhdmlkMTIzIiwiYXVkIjoiQ1VOT0MiLCJpYXQiOjE3MTYwOTg0NDksImV4cCI6MTcxNjEwMDI0OX0.Hygbtu33WrgsB2fjQmPdVwO5lXOFI9FtQdwDfl1mbRQ";



    private AuthenticationServiceImpl authenticationService;
    private JwtService jwtService = mock(JwtService.class);
    private UserService userService = mock(UserService.class);
    private AuthenticationManager authenticationManager = mock(AuthenticationManager.class);

    private UserResponseDto userResponseDto;
    @BeforeEach
    void SetUp(){
        authenticationService = new AuthenticationServiceImpl(userService,jwtService,authenticationManager);
        User user = new User();
        user.setId(ID);
        user.setName(NAME);
        user.setRole(ROLE);
        user.setEmail(EMAIL);
        user.setUsername(USER_NAME);
        user.setStatus(STATUS);

        userResponseDto = new UserResponseDto(user);
    }


    @Test
    public void signUp() throws ServiceException{
        when(userService.save(any(UserCreateRequestDTO.class))).thenReturn(userResponseDto);
        when(jwtService.generateToken(userResponseDto)).thenReturn(token);

        UserCreateRequestDTO userCreate = new UserCreateRequestDTO(ROLE,NAME,EMAIL,USER_NAME,PASSWORD);
        String tokenExpected = authenticationService.signup(userCreate).getToken();
        assertEquals(tokenExpected,token);
    }

    @Test
    public void signIn()throws ServiceException {
        SignInRequestDTO requestDTO = new SignInRequestDTO();
        requestDTO.setUser(USER_NAME);
        requestDTO.setPassword(PASSWORD);

        when(userService.findUserByUserName(USER_NAME)).thenReturn(userResponseDto);
        when(jwtService.generateToken(userResponseDto)).thenReturn(token);

        String tokenExpected = authenticationService.signin(requestDTO).getToken();
        assertEquals(tokenExpected,token);
    }

}
