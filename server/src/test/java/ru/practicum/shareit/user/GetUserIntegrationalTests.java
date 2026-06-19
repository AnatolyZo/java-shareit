package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.DuplicateDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GetUserIntegrationalTests {
    private final UserService userService;

    @Test
    void createAndGetUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Name1");
        userDto.setEmail("name1@mail.ru");

        UserDtoResponse createdUser = userService.createUser(userDto);

        long createdUserId = createdUser.getId();

        UserDtoResponse retrievedUser = userService.getUserDtoById(createdUserId);

        UserDtoResponse expectedUser = new UserDtoResponse();
        expectedUser.setId(createdUserId);
        expectedUser.setName(userDto.getName());
        expectedUser.setEmail(userDto.getEmail());

        assertThat(retrievedUser, equalTo(expectedUser));
    }

    @Test
    void createUserWithDuplicateEmail() {
        UserDto userDto = new UserDto();
        userDto.setName("Name2");
        userDto.setEmail("name2@mail.ru");

        UserDto userDto2 = new UserDto();
        userDto2.setName("Name3");
        userDto2.setEmail("name2@mail.ru");

        UserDtoResponse createdUser = userService.createUser(userDto);
        long createdUserId = createdUser.getId();

        UserDtoResponse retrievedUser = userService.getUserDtoById(createdUserId);

        assertThat(retrievedUser, equalTo(createdUser));

        final DuplicateDataException exception = assertThrows(DuplicateDataException.class,
                () -> userService.createUser(userDto2));

        assertEquals("Такой адрес электронной почты уже занят", exception.getMessage());

        //Проверяем, что пользователь с дублированным адресом электронной почты не добавился в БД под следующим id
        final NotFoundException exception2 = assertThrows(NotFoundException.class,
                () -> userService.getUserDtoById(createdUserId + 1));

        assertEquals(String.format("Пользователь с id %d отсутствует", createdUserId + 1), exception2.getMessage());
    }

    @Test
    void getNotExistentUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Name4");
        userDto.setEmail("name4@mail.ru");

        UserDtoResponse createdUser = userService.createUser(userDto);
        long createdUserId = createdUser.getId();

        //Пытаемся создать пользователя со следующим id относительно последнего созданного
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getUserDtoById(createdUserId + 1));

        assertEquals(String.format("Пользователь с id %d отсутствует", createdUserId + 1), exception.getMessage());
    }
}
