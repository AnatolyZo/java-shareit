package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.DuplicateDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.validation.EntityExistsValidationService;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EntityExistsValidationService entityExistsValidationService;

    @Override
    public Collection<UserDtoResponse> getAllUsers() {
        log.trace("Инициировано получение списка всех пользователей");
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDtoResponse)
                .toList();
    }

    @Override
    public UserDtoResponse getUserDtoById(long userId) {
        log.trace("Инициировано получение пользователя с id {}", userId);
        User user = entityExistsValidationService.getUserByIdOrThrow(userId);
        return UserMapper.mapToUserDtoResponse(user);
    }

    @Override
    @Transactional
    public UserDtoResponse createUser(UserDto userDto) {
        log.trace("Инициировано создание пользователя {}", userDto);
        checkDuplicateEmail(userDto.getEmail());
        User convertedRequestUser = UserMapper.mapToUser(userDto);
        User createdUser = userRepository.save(convertedRequestUser);
        log.debug("Создан пользователь {} и добавлен в хранилище", createdUser);
        return UserMapper.mapToUserDtoResponse(createdUser);
    }

    @Override
    @Transactional
    public UserDtoResponse editUser(long userId, UserDto changesToUser) {
        log.trace("Инициировано редактирование пользователя с id {}", userId);
        User editingUser = entityExistsValidationService.getUserByIdOrThrow(userId);
        editUserFields(editingUser, changesToUser);
        User editedUser = userRepository.save(editingUser);
        log.debug("Отредактированы данные пользователя c id {}, стало - {}", userId, editedUser);
        return UserMapper.mapToUserDtoResponse(editedUser);
    }

    @Override
    @Transactional
    public UserDtoResponse deleteUser(long userId) {
        log.trace("Инициировано удаление пользователя с id {}", userId);

        //Получение пользователя для проверки его наличия в хранилище
        User user = entityExistsValidationService.getUserByIdOrThrow(userId);
        userRepository.deleteById(userId);
        log.debug("Пользователь с id {} удален", userId);
        return UserMapper.mapToUserDtoResponse(user);
    }

    private void editUserFields(User editingUser, UserDto changesToUser) {
        if (changesToUser.getName() != null) {
            editingUser.setName(changesToUser.getName());
            log.debug("У пользователя {} отредактировано поле name на {}", editingUser, changesToUser.getName());
        }

        if (changesToUser.getEmail() != null) {
            checkDuplicateEmail(changesToUser.getEmail());
            editingUser.setEmail(changesToUser.getEmail());
            log.debug("У пользователя {} отредактировано поле email на {}", editingUser, changesToUser.getEmail());
        }
    }

    private void checkDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateDataException("Такой адрес электронной почты уже занят");
        }
    }
}
