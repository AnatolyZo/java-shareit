package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.util.Collection;

public interface UserService {
    Collection<UserDtoResponse> getAllUsers();

    UserDtoResponse getUserDtoById(long userId);

    UserDtoResponse createUser(UserDto userDto);

    UserDtoResponse editUser(long userId, UserDto userDto);

    UserDtoResponse deleteUser(long userId);
}
