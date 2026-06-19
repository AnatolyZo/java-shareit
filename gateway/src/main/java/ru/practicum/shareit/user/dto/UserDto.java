package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.validation.ValidationGroups;

@Data
public class UserDto {
    @NotBlank(groups = ValidationGroups.Create.class, message = "Имя пользователя не может быть пустым или состоять только из пробелов")
    private String name;
    @NotNull(groups = ValidationGroups.Create.class, message = "Графа email не должна быть пустой")
    @NotEmpty(groups = ValidationGroups.Create.class, message = "Графа email не должна быть пустой")
    @Email(groups = {ValidationGroups.Create.class, ValidationGroups.Edit.class}, message = "Некорректный формат электронной почты")
    private String email;
}
