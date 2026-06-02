package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.validation.DateNotInPast;
import ru.practicum.shareit.validation.StartIsNotBeforeEnd;

import java.time.LocalDateTime;

@Data
@StartIsNotBeforeEnd
public class BookingDto {
    private long id;
    private long itemId;
    private long bookerId;
    @NotNull
    @DateNotInPast
    private LocalDateTime start;
    @NotNull
    @DateNotInPast
    private LocalDateTime end;
    private BookingStatus status;
}
