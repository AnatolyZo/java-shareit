package ru.practicum.shareit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

public class StartIsNotBeforeEndValidator implements ConstraintValidator<StartIsNotBeforeEnd, BookingDto> {
    @Override
    public void initialize(StartIsNotBeforeEnd constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext context) {
        if (bookingDto == null) {
            return true;
        }

        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();

        if (start == null || end == null) {
            return true;
        }

        if (!start.isBefore(end)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("start")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
