package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTests {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService bookingService;

    private BookingDto request;
    private BookingDtoResponse response;

    @BeforeEach
    void setUp() {
        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime start = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime end = LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.MILLIS);

        request = new BookingDto();
        request.setItemId(1L);
        request.setStart(start);
        request.setEnd(end);

        UserDtoResponse owner = new UserDtoResponse();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@email.ru");

        UserDtoResponse booker = new UserDtoResponse();
        booker.setId(2L);
        booker.setName("Booker");
        booker.setEmail("booker@email.ru");

        ItemDtoResponse item = new ItemDtoResponse();
        item.setId(1L);
        item.setOwner(owner);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailableForRent(true);

        response = new BookingDtoResponse();
        response.setId(1L);
        response.setItem(item);
        response.setBooker(booker);
        response.setStart(start);
        response.setEnd(end);
        response.setCreated(created);
        response.setStatus(BookingStatus.WAITING);
    }

    @Test
    void createBooking() throws Exception {
        long userId = 1L;

        when(bookingService.createBooking(userId, request))
                .thenReturn(response);

        mvc.perform(post("/internal/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.item").exists())
                .andExpect(jsonPath("$.booker").exists())
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void approveBooking() throws Exception {
        long userId = 1L;
        long bookingId = 1L;
        boolean state = true;

        response.setStatus(BookingStatus.APPROVED);

        when(bookingService.approveBooking(userId, bookingId, state))
                .thenReturn(response);

        mvc.perform(patch("/internal/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", "true")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.item").exists())
                .andExpect(jsonPath("$.booker").exists())
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void getBooking() throws Exception {
        long userId = 1L;
        long bookingId = 1L;

        when(bookingService.getBookingById(userId, bookingId))
                .thenReturn(response);

        mvc.perform(get("/internal/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.item").exists())
                .andExpect(jsonPath("$.booker").exists())
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void getUsersBookings() throws Exception {
        long userId = 2L;
        String state = "WAITING";

        when(bookingService.getAllUsersBookings(userId, state))
                .thenReturn(List.of(response));

        mvc.perform(get("/internal/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.[*].id").exists())
                .andExpect(jsonPath("$.[*].item").exists())
                .andExpect(jsonPath("$.[*].booker").exists())
                .andExpect(jsonPath("$.[*].start").exists())
                .andExpect(jsonPath("$.[*].end").exists())
                .andExpect(jsonPath("$.[*].created").exists())
                .andExpect(jsonPath("$.[*].status", Matchers.everyItem(is("WAITING"))));
    }

    @Test
    void getOwnersBookings() throws Exception {
        long userId = 1L;
        String state = "WAITING";

        when(bookingService.getAllOwnersBookings(userId, state))
                .thenReturn(List.of(response));

        mvc.perform(get("/internal/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.[*].id").exists())
                .andExpect(jsonPath("$.[*].item").exists())
                .andExpect(jsonPath("$.[*].booker").exists())
                .andExpect(jsonPath("$.[*].start").exists())
                .andExpect(jsonPath("$.[*].end").exists())
                .andExpect(jsonPath("$.[*].created").exists())
                .andExpect(jsonPath("$.[*].status", Matchers.everyItem(is("WAITING"))));
    }
}
