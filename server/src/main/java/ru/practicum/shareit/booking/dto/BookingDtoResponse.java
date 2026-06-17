package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.time.LocalDateTime;

@Data
public class BookingDtoResponse {
    private Long id;
    private ItemDtoResponse item;
    private UserDtoResponse booker;
    private LocalDateTime start;
    private LocalDateTime end;
    private LocalDateTime created;
    private BookingStatus status;
}
