package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UserDtoResponse {
    private long id;
    private String name;
    private String email;
}
