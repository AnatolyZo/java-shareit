package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Item item;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
}
