package ru.practicum.shareit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTests {
    @Autowired
    private JacksonTester<ItemDto> json;

    private final Validator validator;

    public ItemDtoJsonTests() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void bookingDtoDeserialization() throws IOException {
        String validJson = "{\n" +
                "                        \"name\": \"Item\",\n" +
                "                        \"description\": \"Description\",\n" +
                "                        \"available\": true,\n" +
                "                        \"requestId\": 123\n" +
                "                    }";

        ItemDto itemDto = json.parseObject(validJson);

        assertThat(itemDto.getName()).isEqualTo("Item");
        assertThat(itemDto.getDescription()).isEqualTo("Description");
        assertThat(itemDto.getIsAvailableForRent()).isEqualTo(true);
        assertThat(itemDto.getRequestId()).isEqualTo(123);
    }

    @Test
    void itemDtoDeserializationWithWrongName() throws IOException {
        String invalidJson1 = "{\n" +
                "                        \"description\": \"Description\",\n" +
                "                        \"available\": true,\n" +
                "                        \"requestId\": 123\n" +
                "                    }";

        ItemDto itemDto1 = json.parseObject(invalidJson1);

        Set<ConstraintViolation<ItemDto>> violations1 =
                validator.validate(itemDto1);

        assertThat(violations1).hasSize(1);
        assertThat(violations1)
                .extracting(ConstraintViolation::getMessage)
                .contains("Название предмета не может быть пустым или состоять только из пробелов");

        String invalidJson2 = "{\n" +
                "                        \"name\": \"\",\n" +
                "                        \"description\": \"Description\",\n" +
                "                        \"available\": true,\n" +
                "                        \"requestId\": 123\n" +
                "                    }";

        ItemDto itemDto2 = json.parseObject(invalidJson2);

        Set<ConstraintViolation<ItemDto>> violations2 =
                validator.validate(itemDto2);

        assertThat(violations2).hasSize(1);
        assertThat(violations2)
                .extracting(ConstraintViolation::getMessage)
                .contains("Название предмета не может быть пустым или состоять только из пробелов");

        String invalidJson3 = "{\n" +
                "                        \"name\": \"     \",\n" +
                "                        \"description\": \"Description\",\n" +
                "                        \"available\": true,\n" +
                "                        \"requestId\": 123\n" +
                "                    }";

        ItemDto itemDto3 = json.parseObject(invalidJson3);

        Set<ConstraintViolation<ItemDto>> violations3 =
                validator.validate(itemDto3);

        assertThat(violations3).hasSize(1);
        assertThat(violations3)
                .extracting(ConstraintViolation::getMessage)
                .contains("Название предмета не может быть пустым или состоять только из пробелов");
    }

    @Test
    void itemDtoDeserializationWithWrongDescription() throws IOException {
        String invalidJson1 = "{\n" +
                "                        \"name\": \"Item\",\n" +
                "                        \"available\": true,\n" +
                "                        \"requestId\": 123\n" +
                "                    }";

        ItemDto itemDto1 = json.parseObject(invalidJson1);

        Set<ConstraintViolation<ItemDto>> violations1 =
                validator.validate(itemDto1);

        assertThat(violations1).hasSize(1);
        assertThat(violations1)
                .extracting(ConstraintViolation::getMessage)
                .contains("Описание предмета не может быть пустым или состоять только из пробелов");

        String invalidJson2 = "{\n" +
                "                        \"name\": \"Item\",\n" +
                "                        \"description\": \"\",\n" +
                "                        \"available\": true,\n" +
                "                        \"requestId\": 123\n" +
                "                    }";

        ItemDto itemDto2 = json.parseObject(invalidJson2);

        Set<ConstraintViolation<ItemDto>> violations2 =
                validator.validate(itemDto2);

        assertThat(violations2).hasSize(1);
        assertThat(violations2)
                .extracting(ConstraintViolation::getMessage)
                .contains("Описание предмета не может быть пустым или состоять только из пробелов");

        String invalidJson3 = "{\n" +
                "                        \"name\": \"Item\",\n" +
                "                        \"description\": \"     \",\n" +
                "                        \"available\": true,\n" +
                "                        \"requestId\": 123\n" +
                "                    }";

        ItemDto itemDto3 = json.parseObject(invalidJson3);

        Set<ConstraintViolation<ItemDto>> violations3 =
                validator.validate(itemDto3);

        assertThat(violations3).hasSize(1);
        assertThat(violations3)
                .extracting(ConstraintViolation::getMessage)
                .contains("Описание предмета не может быть пустым или состоять только из пробелов");
    }

    @Test
    void itemDtoDeserializationWithWrongAvailable() throws IOException {
        String invalidJson1 = "{\n" +
                "                        \"name\": \"Item\",\n" +
                "                        \"description\": \"Description\",\n" +
                "                        \"requestId\": 123\n" +
                "                    }";

        ItemDto itemDto1 = json.parseObject(invalidJson1);

        Set<ConstraintViolation<ItemDto>> violations1 =
                validator.validate(itemDto1);

        assertThat(violations1).hasSize(1);
        assertThat(violations1)
                .extracting(ConstraintViolation::getMessage)
                .contains("Параметр доступности предмета для аренды не может быть пустым");
    }
}
