package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.validation.NotDuplicateEmail;
import ru.practicum.shareit.validation.ValidationGroups;

@Data
public class UserDto {
    private long id;
    private String name;
    @NotNull(groups = ValidationGroups.Create.class)
    @Email(groups = {ValidationGroups.Create.class, ValidationGroups.Edit.class})
    @NotDuplicateEmail(groups = {ValidationGroups.Create.class, ValidationGroups.Edit.class})
    private String email;
}
