package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public Collection<User> getAllUsers() {
        log.trace("Инициировано получение списка всех пользователей");
        return userStorage.getAllUsers();
    }

    @Override
    public User getUserById(long userId) {
        log.trace("Инициировано получение пользователя с id {}", userId);
        return userStorage.getUserById(userId)
                .orElseThrow(() -> {
                    log.info("Пользователь с id {} отсутствует", userId);
                    return new NotFoundException(String.format("Пользователь с id %d отсутствует", userId));
                });
    }

    @Override
    public User createUser(User user) {
        log.trace("Инициировано создание пользователя {}", user);
        User createdUser = userStorage.createUser(user);
        log.debug("Создан пользователь {} и добавлен в хранилище", createdUser);
        return createdUser;
    }

    @Override
    public User editUser(long userId, User changesToUser) {
        log.trace("Инициировано редактирование пользователя с id {}", userId);
        User editingUser = getUserById(userId);
        UserMapper.editUserFields(editingUser, changesToUser);
        log.debug("Отредактированы данные пользователя c id {}, стало - {}", userId, editingUser);
        return userStorage.editUser(userId, editingUser);
    }

    @Override
    public User deleteUser(long userId) {
        log.trace("Инициировано удаление пользователя с id {}", userId);

        //Получение пользователя для проверки его наличия в хранилище
        getUserById(userId);
        User user = userStorage.deleteUser(userId);
        log.debug("Пользователь с id {} удален", userId);
        return user;
    }
}
