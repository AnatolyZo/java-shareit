package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Booking {
    private long id;
    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
}
