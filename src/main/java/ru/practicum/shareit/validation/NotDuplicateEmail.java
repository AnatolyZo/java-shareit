package ru.practicum.shareit.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailDuplicateValidator.class)
public @interface NotDuplicateEmail {
    String message() default "Такой адрес электронной почты уже занят";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
