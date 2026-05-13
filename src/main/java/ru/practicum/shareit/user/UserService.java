package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserService {
    Collection<User> getAllUsers();

    User getUserById(long userId);

    User createUser(User user);

    User editUser(long userId, User user);

    User deleteUser(long userId);
}
