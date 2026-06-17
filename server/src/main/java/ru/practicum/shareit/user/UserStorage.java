package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> getAllUsers();

    Optional<User> getUserById(long userId);

    User createUser(User user);

    User editUser(long userId, User user);

    User deleteUser(long userId);
}
