package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private long itemId;
    private long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
}
