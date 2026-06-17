package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTests {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void createBooking() throws Exception {
        long userId = 1L;

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.MILLIS));

        when(bookingClient.createBooking(userId, bookingDto))
                .thenReturn(ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(bookingDto));

        mvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId().intValue())))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().truncatedTo(ChronoUnit.MILLIS).toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().truncatedTo(ChronoUnit.MILLIS).toString())));
    }

    @Test
    void createBookingWithItemIdEqualsNull() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.MILLIS));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("itemId не должно быть пустым")));
    }

    @Test
    void createBookingWithItemIdNotPositive() throws Exception {
        BookingDto bookingDtoWithZeroItemId = new BookingDto();
        bookingDtoWithZeroItemId.setItemId(0L);
        bookingDtoWithZeroItemId.setStart(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS));
        bookingDtoWithZeroItemId.setEnd(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.MILLIS));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoWithZeroItemId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("itemId должно быть больше нуля")));

        BookingDto bookingDtoWithNegativeItemId = new BookingDto();
        bookingDtoWithNegativeItemId.setItemId(-1L);
        bookingDtoWithNegativeItemId.setStart(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS));
        bookingDtoWithNegativeItemId.setEnd(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.MILLIS));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoWithNegativeItemId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("itemId должно быть больше нуля")));
    }

    @Test
    void createBookingWithStartDateInPast() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().minusSeconds(1).truncatedTo(ChronoUnit.MILLIS));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.MILLIS));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Дата не может быть в прошлом")));
    }

    @Test
    void createBookingWithStartDateIsNull() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setEnd(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.MILLIS));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Дата начала бронирования не должна быть пустой")));
    }

    @Test
    void createBookingWithEndDateIsNull() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Дата окончания бронирования не должна быть пустой")));
    }

    @Test
    void createBookingWithStartIsBeforeEnd() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1).minusSeconds(1).truncatedTo(ChronoUnit.MILLIS));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Дата начала бронирования должна быть раньше даты окончания бронирования")));
    }

    @Test
    void createBookingWithStartIsEqualsEnd() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Дата начала бронирования должна быть раньше даты окончания бронирования")));
    }

    @Test
    void approveBooking() throws Exception {
        long bookingId = 1L;
        boolean approved = true;

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk());
    }

    @Test
    void getBooking() throws Exception {
        long bookingId = 1L;
        boolean approved = true;

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getOwnersBookings() throws Exception {
        long bookingId = 1L;

        mvc.perform(get("/bookings/owner", bookingId)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void createBookingWithMissingHeader() throws Exception {
        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['Отсутствует обязательный заголовок']").exists())
                .andExpect(jsonPath("$.['Отсутствует обязательный заголовок']", containsString("Required request header 'X-Sharer-User-Id' for method parameter type long is not present")));
    }
}
