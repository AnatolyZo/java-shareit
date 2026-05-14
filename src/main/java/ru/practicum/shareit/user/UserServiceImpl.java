package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public Collection<UserDto> getAllUsers() {
        log.trace("Инициировано получение списка всех пользователей");
        return userStorage.getAllUsers()
                .stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(long userId) {
        log.trace("Инициировано получение пользователя с id {}", userId);
        return userStorage.getUserById(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> {
                    log.info("Пользователь с id {} отсутствует", userId);
                    return new NotFoundException(String.format("Пользователь с id %d отсутствует", userId));
                });
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.trace("Инициировано создание пользователя {}", userDto);
        User convertedRequestUser = UserMapper.mapToUser(userDto);
        User createdUser = userStorage.createUser(convertedRequestUser);
        log.debug("Создан пользователь {} и добавлен в хранилище", createdUser);
        return UserMapper.mapToUserDto(createdUser);
    }

    @Override
    public UserDto editUser(long userId, UserDto changesToUser) {
        log.trace("Инициировано редактирование пользователя с id {}", userId);
        User editingUser = UserMapper.mapToUser(getUserById(userId));
        editUserFields(editingUser, changesToUser);
        User editedUser = userStorage.editUser(userId, editingUser);
        log.debug("Отредактированы данные пользователя c id {}, стало - {}", userId, editedUser);
        return UserMapper.mapToUserDto(editedUser);
    }

    @Override
    public UserDto deleteUser(long userId) {
        log.trace("Инициировано удаление пользователя с id {}", userId);

        //Получение пользователя для проверки его наличия в хранилище
        getUserById(userId);
        User user = userStorage.deleteUser(userId);
        log.debug("Пользователь с id {} удален", userId);
        return UserMapper.mapToUserDto(user);
    }

    private static void editUserFields(User editingUser, UserDto changesToUser) {
        if (changesToUser.getName() != null) {
            editingUser.setName(changesToUser.getName());
            log.debug("У пользователя {} отредактировано поле name на {}", editingUser, changesToUser.getName());
        }

        if (changesToUser.getEmail() != null) {
            editingUser.setEmail(changesToUser.getEmail());
            log.debug("У пользователя {} отредактировано поле email на {}", editingUser, changesToUser.getEmail());
        }
    }
}
