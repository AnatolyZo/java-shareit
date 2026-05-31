package ru.practicum.shareit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class DateValidator implements ConstraintValidator<DateNotInPast, LocalDateTime> {
    private final LocalDateTime now = LocalDateTime.now();

    @Override
    public void initialize(DateNotInPast constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.isAfter(now);
    }
}
