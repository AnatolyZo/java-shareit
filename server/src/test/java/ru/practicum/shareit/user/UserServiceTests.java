package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.DuplicateDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.validation.EntityExistsValidationService;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityExistsValidationService entityExistsValidationService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser() {
        UserDto request = formUserDto();
        UserDtoResponse expectedUser = formExpectedUser();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);

        User savedUser = formUser();
        when(userRepository.save(any())).thenReturn(savedUser);

        UserDtoResponse result = userService.createUser(request);

        assertThat(result, equalTo(expectedUser));
    }

    @Test
    void createUserWithDuplicateEmail() {
        UserDto request = formUserDto();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        final DuplicateDataException exception = assertThrows(DuplicateDataException.class,
                () -> userService.createUser(request));

        assertEquals("Такой адрес электронной почты уже занят", exception.getMessage());
    }

    @Test
    void editUser() {
        long userId = 1L;
        UserDto request = formUserDto();
        User editingUser = formUserToEdit();
        UserDtoResponse expectedUser = formExpectedUser();

        when(entityExistsValidationService.getUserByIdOrThrow(userId)).thenReturn(editingUser);

        User savedUser = formUser();
        when(userRepository.save(any())).thenReturn(savedUser);

        UserDtoResponse result = userService.editUser(userId, request);

        assertThat(result, equalTo(expectedUser));
    }

    @Test
    void editNotExistentUser() {
        long userId = 999L;
        UserDto request = formUserDto();

        when(entityExistsValidationService.getUserByIdOrThrow(userId))
                .thenThrow(new NotFoundException(String.format("Пользователь с id %d отсутствует", userId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.editUser(userId, request));

        assertEquals(String.format("Пользователь с id %d отсутствует", userId), exception.getMessage());
    }

    @Test
    void deleteUser() {
        long userId = 1L;
        UserDtoResponse expectedUser = formExpectedUser();
        User deletingUser = formUser();

        when(entityExistsValidationService.getUserByIdOrThrow(userId)).thenReturn(deletingUser);

        UserDtoResponse result = userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
        assertThat(result, equalTo(expectedUser));
    }

    @Test
    void deleteNotExistentUser() {
        long userId = 999L;

        when(entityExistsValidationService.getUserByIdOrThrow(userId))
                .thenThrow(new NotFoundException(String.format("Пользователь с id %d отсутствует", userId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.deleteUser(userId));

        assertEquals(String.format("Пользователь с id %d отсутствует", userId), exception.getMessage());
    }

    @Test
    void getUserById() {
        long userId = 1L;
        UserDtoResponse expectedUser = formExpectedUser();

        User user = formUser();
        when(entityExistsValidationService.getUserByIdOrThrow(userId)).thenReturn(user);

        UserDtoResponse result = userService.getUserDtoById(userId);

        assertThat(result, equalTo(expectedUser));
    }

    @Test
    void getNotExistentUser() {
        long userId = 999L;

        when(entityExistsValidationService.getUserByIdOrThrow(userId))
                .thenThrow(new NotFoundException(String.format("Пользователь с id %d отсутствует", userId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getUserDtoById(userId));

        assertEquals(String.format("Пользователь с id %d отсутствует", userId), exception.getMessage());
    }

    @Test
    void getAllUsers() {
        List<UserDtoResponse> expectedUsers = List.of(formExpectedUser());

        List<User> users = List.of(formUser());
        when(userRepository.findAll()).thenReturn(users);

        Collection<UserDtoResponse> result = userService.getAllUsers();

        assertThat(result, equalTo(expectedUsers));
    }

    private UserDto formUserDto() {
        UserDto userDto = new UserDto();
        userDto.setName("Name");
        userDto.setEmail("name@mail.ru");

        return userDto;
    }

    private User formUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Name");
        user.setEmail("name@mail.ru");

        return user;
    }

    private UserDtoResponse formExpectedUser() {
        UserDtoResponse userDtoResponse = new UserDtoResponse();
        userDtoResponse.setId(1L);
        userDtoResponse.setName("Name");
        userDtoResponse.setEmail("name@mail.ru");

        return userDtoResponse;
    }

    private User formUserToEdit() {
        User user = new User();
        user.setId(1L);
        user.setName("Name2");
        user.setEmail("name2@mail.ru");

        return user;
    }
}
