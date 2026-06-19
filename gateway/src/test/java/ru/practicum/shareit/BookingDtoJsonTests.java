package ru.practicum.shareit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTests {
    @Autowired
    private JacksonTester<BookingDto> json;

    private final Validator validator;

    public BookingDtoJsonTests() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void bookingDtoDeserialization() throws IOException {
        String validJson = "{\n" +
                "                    \"itemId\": 123,\n" +
                "                    \"start\": \"2026-12-25T10:00:00\",\n" +
                "                    \"end\": \"2026-12-26T18:00:00\"\n" +
                "                }";

        BookingDto bookingDto = json.parseObject(validJson);

        assertThat(bookingDto.getItemId()).isEqualTo(123);
        assertThat(bookingDto.getStart()).isEqualTo(LocalDateTime.of(2026,12,25,10,0,0));
        assertThat(bookingDto.getEnd()).isEqualTo(LocalDateTime.of(2026,12,26,18,0,0));
    }

    @Test
    void bookingDtoDeserializationWithWrongItemId() throws IOException {
        String invalidJson1 = "{\n" +
                "                        \"start\": \"2026-12-25T10:00:00\",\n" +
                "                        \"end\": \"2026-12-26T18:00:00\"\n" +
                "                    }";

        BookingDto bookingDto1 = json.parseObject(invalidJson1);

        Set<ConstraintViolation<BookingDto>> violations1 =
                validator.validate(bookingDto1);

        assertThat(violations1).hasSize(1);
        assertThat(violations1)
                .extracting(ConstraintViolation::getMessage)
                .contains("itemId не должно быть пустым");

        String invalidJson2 = "{\n" +
                "                        \"itemId\": 0,\n" +
                "                        \"start\": \"2026-12-25T10:00:00\",\n" +
                "                        \"end\": \"2026-12-26T18:00:00\"\n" +
                "                    }";

        BookingDto bookingDto2 = json.parseObject(invalidJson2);

        Set<ConstraintViolation<BookingDto>> violations2 =
                validator.validate(bookingDto2);

        assertThat(violations2).hasSize(1);
        assertThat(violations2)
                .extracting(ConstraintViolation::getMessage)
                .contains("itemId должно быть больше нуля");

        String invalidJson3 = "{\n" +
                "                        \"itemId\": -1,\n" +
                "                        \"start\": \"2026-12-25T10:00:00\",\n" +
                "                        \"end\": \"2026-12-26T18:00:00\"\n" +
                "                    }";

        BookingDto bookingDto3 = json.parseObject(invalidJson3);

        Set<ConstraintViolation<BookingDto>> violations3 =
                validator.validate(bookingDto3);

        assertThat(violations3).hasSize(1);
        assertThat(violations3)
                .extracting(ConstraintViolation::getMessage)
                .contains("itemId должно быть больше нуля");
    }

    @Test
    void bookingDtoDeserializationWithWrongStart() throws IOException {
        String invalidJson1 = "{\n" +
                "                        \"itemId\": 123,\n" +
                "                        \"end\": \"2026-12-26T18:00:00\"\n" +
                "                    }";

        BookingDto bookingDto1 = json.parseObject(invalidJson1);

        Set<ConstraintViolation<BookingDto>> violations1 =
                validator.validate(bookingDto1);

        assertThat(violations1).hasSize(1);
        assertThat(violations1)
                .extracting(ConstraintViolation::getMessage)
                .contains("Дата начала бронирования не должна быть пустой");

        String invalidJson2 = "{\n" +
                "                        \"itemId\": 123,\n" +
                "                        \"start\": \"2024-12-25T10:00:00\",\n" +
                "                        \"end\": \"2026-12-26T18:00:00\"\n" +
                "                    }";

        BookingDto bookingDto2 = json.parseObject(invalidJson2);

        Set<ConstraintViolation<BookingDto>> violations2 =
                validator.validate(bookingDto2);

        assertThat(violations2).hasSize(1);
        assertThat(violations2)
                .extracting(ConstraintViolation::getMessage)
                .contains("Дата не может быть в прошлом");
    }

    @Test
    void bookingDtoDeserializationWithWrongEnd() throws IOException {
        String invalidJson1 = "{\n" +
                "                        \"itemId\": 123,\n" +
                "                        \"start\": \"2026-12-25T10:00:00\"\n" +
                "                    }";

        BookingDto bookingDto1 = json.parseObject(invalidJson1);

        Set<ConstraintViolation<BookingDto>> violations1 =
                validator.validate(bookingDto1);

        assertThat(violations1).hasSize(1);
        assertThat(violations1)
                .extracting(ConstraintViolation::getMessage)
                .contains("Дата окончания бронирования не должна быть пустой");
    }

    @Test
    void bookingDtoDeserializationWithStartAfterOrEqualEnd() throws IOException {
        String invalidJson1 = "{\n" +
                "                        \"itemId\": 123,\n" +
                "                        \"start\": \"2026-12-25T10:00:00\",\n" +
                "                        \"end\": \"2026-12-25T10:00:00\"\n" +
                "                    }";

        BookingDto bookingDto1 = json.parseObject(invalidJson1);

        Set<ConstraintViolation<BookingDto>> violations1 =
                validator.validate(bookingDto1);

        assertThat(violations1).hasSize(1);
        assertThat(violations1)
                .extracting(ConstraintViolation::getMessage)
                .contains("Дата начала бронирования должна быть раньше даты окончания бронирования");

        String invalidJson2 = "{\n" +
                "                        \"itemId\": 123,\n" +
                "                        \"start\": \"2026-12-25T10:00:00\",\n" +
                "                        \"end\": \"2026-12-25T09:59:59\"\n" +
                "                    }";

        BookingDto bookingDto2 = json.parseObject(invalidJson2);

        Set<ConstraintViolation<BookingDto>> violations2 =
                validator.validate(bookingDto2);

        assertThat(violations2).hasSize(1);
        assertThat(violations2)
                .extracting(ConstraintViolation::getMessage)
                .contains("Дата начала бронирования должна быть раньше даты окончания бронирования");
    }
}
