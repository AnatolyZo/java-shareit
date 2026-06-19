package ru.practicum.shareit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.ValidationGroups;

import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Validated
public class UserDtoJsonTests {
    @Autowired
    private JacksonTester<UserDto> json;

    private final Validator validator;

    public UserDtoJsonTests() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void userDtoDeserialization() throws IOException {
        String validJson = "{\n" +
                "                        \"name\": \"Name\",\n" +
                "                        \"email\": \"email@email.ru\"\n" +
                "                    }";

        UserDto userDto = json.parseObject(validJson);

        assertThat(userDto.getName()).isEqualTo("Name");
        assertThat(userDto.getEmail()).isEqualTo("email@email.ru");
    }

    @Test
    void userDtoDeserializationWithWrongName() throws IOException {
        String invalidJson1 = "{\n" +
                "                        \"email\": \"email@email.ru\"\n" +
                "                    }";

        UserDto userDto1 = json.parseObject(invalidJson1);

        Set<ConstraintViolation<UserDto>> violations1 =
                validator.validate(userDto1, ValidationGroups.Create.class);

        assertThat(violations1).hasSize(1);
        assertThat(violations1)
                .extracting(ConstraintViolation::getMessage)
                .contains("Имя пользователя не может быть пустым или состоять только из пробелов");

        String invalidJson2 = "{\n" +
                "                        \"name\": \"\",\n" +
                "                        \"email\": \"email@email.ru\"\n" +
                "                    }";

        UserDto userDto2 = json.parseObject(invalidJson2);

        Set<ConstraintViolation<UserDto>> violations2 =
                validator.validate(userDto2, ValidationGroups.Create.class);

        assertThat(violations2).hasSize(1);
        assertThat(violations2)
                .extracting(ConstraintViolation::getMessage)
                .contains("Имя пользователя не может быть пустым или состоять только из пробелов");

        String invalidJson3 = "{\n" +
                "                   \"name\": \"     \",\n" +
                "                   \"email\": \"email@email.ru\"\n" +
                "               }";

        UserDto userDto3 = json.parseObject(invalidJson3);

        Set<ConstraintViolation<UserDto>> violations3 =
                validator.validate(userDto3, ValidationGroups.Create.class);

        assertThat(violations3).hasSize(1);
        assertThat(violations3)
                .extracting(ConstraintViolation::getMessage)
                .contains("Имя пользователя не может быть пустым или состоять только из пробелов");
    }

    @Test
    void userDtoDeserializationWithWrongEmail() throws IOException {
        String invalidJson1 = "{\n" +
                "                        \"name\": \"Name\"\n" +
                "                    }";

        UserDto userDto1 = json.parseObject(invalidJson1);

        Set<ConstraintViolation<UserDto>> violations1 =
                validator.validate(userDto1, ValidationGroups.Create.class);

        assertThat(violations1).hasSize(2);
        assertThat(violations1)
                .extracting(ConstraintViolation::getMessage)
                .contains("Графа email не должна быть пустой", "Графа email не должна быть пустой");

        String invalidJson2 = "{\n" +
                "                        \"name\": \"Name\",\n" +
                "                        \"email\": \"\"\n" +
                "                    }";

        UserDto userDto2 = json.parseObject(invalidJson2);

        Set<ConstraintViolation<UserDto>> violations2 =
                validator.validate(userDto2, ValidationGroups.Create.class);

        assertThat(violations2).hasSize(1);
        assertThat(violations2)
                .extracting(ConstraintViolation::getMessage)
                .contains("Графа email не должна быть пустой");

        String invalidJson3 = "{\n" +
                "                        \"name\": \"Name\",\n" +
                "                        \"email\": \"email\"\n" +
                "                    }";

        UserDto userDto3 = json.parseObject(invalidJson3);

        Set<ConstraintViolation<UserDto>> violations3 =
                validator.validate(userDto3, ValidationGroups.Create.class);

        assertThat(violations3).hasSize(1);
        assertThat(violations3)
                .extracting(ConstraintViolation::getMessage)
                .contains("Некорректный формат электронной почты");
    }
}
