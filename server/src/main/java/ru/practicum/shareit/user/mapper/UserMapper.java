package ru.practicum.shareit.user.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoResponse;

@Slf4j
public class UserMapper {
    public static User mapToUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        log.debug("Пользователь класса UserDto {} преобразована в объект класса User {}", userDto, user);

        return user;
    }

    public static UserDtoResponse mapToUserDtoResponse(User user) {
        UserDtoResponse userDtoResponse = new UserDtoResponse();
        userDtoResponse.setId(user.getId());
        userDtoResponse.setName(user.getName());
        userDtoResponse.setEmail(user.getEmail());

        log.debug("Пользователь класса User {} преобразована в объект класса UserDtoResponse {}", user, userDtoResponse);

        return userDtoResponse;
    }
}
