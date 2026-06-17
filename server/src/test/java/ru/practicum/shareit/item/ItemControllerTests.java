package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.CommentService;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoResponseForOwner;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ItemController.class)
public class ItemControllerTests {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CommentService commentService;

    private ItemDto request;
    private ItemDtoResponse response;
    private ItemDtoResponseForOwner responseForOwner;
    private CommentDto commentRequest;
    private CommentDtoResponse commentResponse;

    @BeforeEach
    void setUp() {
        request = new ItemDto();
        request.setName("Item");
        request.setDescription("Description");
        request.setIsAvailableForRent(true);

        UserDtoResponse owner = new UserDtoResponse();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@email.ru");

        response = new ItemDtoResponse();
        response.setId(1L);
        response.setOwner(owner);
        response.setName("Item");
        response.setDescription("Description");
        response.setAvailableForRent(true);

        commentRequest = new CommentDto();
        commentRequest.setText("Text");

        commentResponse = new CommentDtoResponse();
        commentResponse.setId(1L);
        commentResponse.setText("Text");
        commentResponse.setItemId(1L);
        commentResponse.setAuthorName("Author");
        commentResponse.setCreated(LocalDateTime.now());

        responseForOwner = new ItemDtoResponseForOwner();
        responseForOwner.setId(1L);
        responseForOwner.setOwner(owner);
        responseForOwner.setName("Item");
        responseForOwner.setDescription("Description");
        responseForOwner.setAvailableForRent(true);
        responseForOwner.setLastBooking(LocalDateTime.now().minusDays(1));
        responseForOwner.setNextBooking(LocalDateTime.now().plusDays(1));
        responseForOwner.setComments(List.of(commentResponse));

    }

    @Test
    void createItem() throws Exception {
        long userId = 1L;

        when(itemService.createItem(userId, request))
                .thenReturn(response);

        mvc.perform(post("/internal/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.owner").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.available").exists());
    }

    @Test
    void editItem() throws Exception {
        long userId = 1L;
        long itemId = 1L;

        when(itemService.editItem(userId, itemId, request))
                .thenReturn(response);

        mvc.perform(patch("/internal/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.owner").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.available").exists());
    }

    @Test
    void getItemById() throws Exception {
        long userId = 1L;
        long itemId = 1L;

        when(itemService.getItemDtoById(userId, itemId))
                .thenReturn(responseForOwner);

        mvc.perform(get("/internal/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.owner").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.available").exists())
                .andExpect(jsonPath("$.lastBooking").exists())
                .andExpect(jsonPath("$.nextBooking").exists())
                .andExpect(jsonPath("$.comments").exists());
    }

    @Test
    void getAllItems() throws Exception {
        long userId = 1L;

        when(itemService.getAllItems(userId))
                .thenReturn(List.of(responseForOwner));

        mvc.perform(get("/internal/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").exists())
                .andExpect(jsonPath("$.[*].owner").exists())
                .andExpect(jsonPath("$.[*].name").exists())
                .andExpect(jsonPath("$.[*].description").exists())
                .andExpect(jsonPath("$.[*].available").exists())
                .andExpect(jsonPath("$.[*].lastBooking").exists())
                .andExpect(jsonPath("$.[*].nextBooking").exists())
                .andExpect(jsonPath("$.[*].comments").exists());
    }

    @Test
    void searchItems() throws Exception {
        long userId = 1L;
        String searchingSubstring = "substring";

        when(itemService.searchItems(userId, searchingSubstring))
                .thenReturn(List.of(response));

        mvc.perform(get("/internal/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", searchingSubstring))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").exists())
                .andExpect(jsonPath("$.[*].owner").exists())
                .andExpect(jsonPath("$.[*].name").exists())
                .andExpect(jsonPath("$.[*].description").exists())
                .andExpect(jsonPath("$.[*].available").exists());
    }

    @Test
    void createComment() throws Exception {
        long userId = 1L;
        long itemId = 1L;

        when(commentService.createComment(userId, itemId, commentRequest))
                .thenReturn(commentResponse);

        mvc.perform(post("/internal/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.text").exists())
                .andExpect(jsonPath("$.itemId").exists())
                .andExpect(jsonPath("$.authorName").exists())
                .andExpect(jsonPath("$.created").exists());
    }
}
