package ru.practicum.shareit.user.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.User;

@Slf4j
public class UserMapper {
    public static void editUserFields(User editingUser, User changesToUser) {
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
