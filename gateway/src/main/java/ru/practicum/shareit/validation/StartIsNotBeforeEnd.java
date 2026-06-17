package ru.practicum.shareit.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StartIsNotBeforeEndValidator.class)
public @interface StartIsNotBeforeEnd {
    String message() default "Дата начала бронирования должна быть раньше даты окончания бронирования";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
