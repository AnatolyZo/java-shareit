package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTests {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestDto request;
    private ItemRequestDtoResponse response;

    @BeforeEach
    void setUp() {
        request = new ItemRequestDto();
        request.setDescription("Description");

        UserDtoResponse owner = new UserDtoResponse();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@email.ru");

        ItemDtoResponse item = new ItemDtoResponse();
        item.setId(1L);
        item.setOwner(owner);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailableForRent(true);

        response = new ItemRequestDtoResponse();
        response.setId(1L);
        response.setItems(List.of(item));
        response.setDescription("Description");
        response.setRequestorId(2L);
    }

    @Test
    void createItemRequest() throws Exception {
        long userId = 1L;

        when(itemRequestService.createItemRequest(userId, request))
                .thenReturn(response);

        mvc.perform(post("/internal/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.items").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.requestorId").exists());
    }

    @Test
    void getOwnItemRequests() throws Exception {
        long userId = 1L;

        when(itemRequestService.getUsersRequests(userId))
                .thenReturn(List.of(response));

        mvc.perform(get("/internal/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").exists())
                .andExpect(jsonPath("$.[*].items").exists())
                .andExpect(jsonPath("$.[*].description").exists())
                .andExpect(jsonPath("$.[*].requestorId").exists());
    }

    @Test
    void getAllItemRequests() throws Exception {
        long userId = 1L;

        when(itemRequestService.getAllRequests(userId))
                .thenReturn(List.of(response));

        mvc.perform(get("/internal/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").exists())
                .andExpect(jsonPath("$.[*].items").exists())
                .andExpect(jsonPath("$.[*].description").exists())
                .andExpect(jsonPath("$.[*].requestorId").exists());
    }

    @Test
    void getItemRequestById() throws Exception {
        long userId = 1L;
        long requestId = 1L;

        when(itemRequestService.getRequestById(userId, requestId))
                .thenReturn(response);

        mvc.perform(get("/internal/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.items").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.requestorId").exists());
    }
}
