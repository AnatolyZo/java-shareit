package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getAllUsers();

    UserDto getUserById(long userId);

    UserDto createUser(UserDto userDto);

    UserDto editUser(long userId, UserDto userDto);

    UserDto deleteUser(long userId);
}
