package ru.practicum.shareit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.user.UserService;

public class EmailDuplicateValidator implements ConstraintValidator<NotDuplicateEmail, String> {
    @Autowired
    private UserService userService;

    @Override
    public void initialize(NotDuplicateEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return userService.getAllUsers()
                .stream()
                .noneMatch(user -> user.getEmail().equals(value));
    }
}
