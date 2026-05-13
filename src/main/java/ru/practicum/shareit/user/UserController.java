package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validation.ValidationGroups;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> getAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getById(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public User create(@Validated(ValidationGroups.Create.class) @RequestBody User user) {
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    public User edit(@PathVariable long userId,@Validated(ValidationGroups.Edit.class) @RequestBody User user) {
        return userService.editUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    public User delete(@PathVariable long userId) {
        return userService.deleteUser(userId);
    }
}
