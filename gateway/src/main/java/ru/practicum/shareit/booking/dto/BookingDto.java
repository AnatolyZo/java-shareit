package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.practicum.shareit.validation.DateNotInPast;
import ru.practicum.shareit.validation.StartIsNotBeforeEnd;

import java.time.LocalDateTime;

@Data
@StartIsNotBeforeEnd
public class BookingDto {
    @NotNull(message = "itemId не должно быть пустым")
    @Positive(message = "itemId должно быть больше нуля")
    private Long itemId;
    @NotNull(message = "Дата начала бронирования не должна быть пустой")
    @DateNotInPast
    private LocalDateTime start;
    @NotNull(message = "Дата окончания бронирования не должна быть пустой")
    private LocalDateTime end;
}
