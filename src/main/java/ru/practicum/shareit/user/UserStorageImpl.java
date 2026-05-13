package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.parent.ParentStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
@Slf4j
public class UserStorageImpl extends ParentStorage<User> implements UserStorage {
    @Override
    public Collection<User> getAllUsers() {
        return elementsMap.values();
    }

    @Override
    public Optional<User> getUserById(long userId) {
        return Optional.ofNullable(elementsMap.get(userId));
    }

    @Override
    public User createUser(User user) {
        long id = createNextId();
        user.setId(id);
        log.debug("Сформирован id {} для нового пользователя", id);

        try {
            elementsMap.put(id, user);
        } catch (RuntimeException e) {
            log.error("Ошибка добавления нового пользователя {}, сообщение об ошибке - {}", user, e.getMessage());
            throw new RuntimeException("Ошибка добавления нового пользователя");
        }

        return user;
    }

    @Override
    public User editUser(long userId, User editingUser) {
        return elementsMap.put(userId, editingUser);
    }

    @Override
    public User deleteUser(long userId) {
        return elementsMap.remove(userId);
    }
}
