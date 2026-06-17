package ru.practicum.shareit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoJsonTests {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    private final Validator validator;

    public ItemRequestDtoJsonTests() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void itemRequestDtoDeserialization() throws IOException {
        String validJson = "{\n" +
                "                        \"description\": \"Description\"\n" +
                "                    }";

        ItemRequestDto itemRequestDto = json.parseObject(validJson);

        assertThat(itemRequestDto.getDescription()).isEqualTo("Description");
    }

    @Test
    void itemRequestDtoDeserializationWrongText() throws IOException {
        String invalidJson1 = "{\n" +
                "                    }";

        ItemRequestDto itemRequestDto1 = json.parseObject(invalidJson1);

        Set<ConstraintViolation<ItemRequestDto>> violations1 =
                validator.validate(itemRequestDto1);

        assertThat(violations1).hasSize(1);
        assertThat(violations1)
                .extracting(ConstraintViolation::getMessage)
                .contains("Описание запроса не должно быть пустым или состоять из пробелов");

        String invalidJson2 = "{\n" +
                "                        \"description\": \"\"\n" +
                "                    }";

        ItemRequestDto itemRequestDto2 = json.parseObject(invalidJson2);

        Set<ConstraintViolation<ItemRequestDto>> violations2 =
                validator.validate(itemRequestDto2);

        assertThat(violations2).hasSize(1);
        assertThat(violations2)
                .extracting(ConstraintViolation::getMessage)
                .contains("Описание запроса не должно быть пустым или состоять из пробелов");

        String invalidJson3 = "{\n" +
                "                        \"description\": \"     \"\n" +
                "                    }";

        ItemRequestDto itemRequestDto3 = json.parseObject(invalidJson3);

        Set<ConstraintViolation<ItemRequestDto>> violations3 =
                validator.validate(itemRequestDto3);

        assertThat(violations3).hasSize(1);
        assertThat(violations3)
                .extracting(ConstraintViolation::getMessage)
                .contains("Описание запроса не должно быть пустым или состоять из пробелов");
    }
}
