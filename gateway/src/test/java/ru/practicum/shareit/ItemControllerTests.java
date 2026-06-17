package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTests {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemClient itemClient;

    @Test
    void createItem() throws Exception {
        long userId = 1L;

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Name");
        itemDto.setDescription("Description");
        itemDto.setIsAvailableForRent(true);

        when(itemClient.createItem(userId, itemDto))
                .thenReturn(ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(itemDto));

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getIsAvailableForRent())));
    }

    @Test
    void createItemWithBlankName() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("      ");
        itemDto.setDescription("Description");
        itemDto.setIsAvailableForRent(true);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Название предмета не может быть пустым или состоять только из пробелов")));
    }

    @Test
    void createItemWithNullName() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(null);
        itemDto.setDescription("Description");
        itemDto.setIsAvailableForRent(true);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Название предмета не может быть пустым или состоять только из пробелов")));
    }

    @Test
    void createItemWithEmptyName() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("");
        itemDto.setDescription("Description");
        itemDto.setIsAvailableForRent(true);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Название предмета не может быть пустым или состоять только из пробелов")));
    }

    @Test
    void createItemWithBlankDescription() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Name");
        itemDto.setDescription("     ");
        itemDto.setIsAvailableForRent(true);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Описание предмета не может быть пустым или состоять только из пробелов")));
    }

    @Test
    void createItemWithNullDescription() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Name");
        itemDto.setDescription(null);
        itemDto.setIsAvailableForRent(true);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Описание предмета не может быть пустым или состоять только из пробелов")));
    }

    @Test
    void createItemWithEmptyDescription() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Name");
        itemDto.setDescription("");
        itemDto.setIsAvailableForRent(true);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Описание предмета не может быть пустым или состоять только из пробелов")));
    }

    @Test
    void createItemWithEmptyAvailable() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Name");
        itemDto.setDescription("Description");
        itemDto.setIsAvailableForRent(null);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Параметр доступности предмета для аренды не может быть пустым")));
    }

    @Test
    void createItemWithMissingHeader() throws Exception {
        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['Отсутствует обязательный заголовок']").exists())
                .andExpect(jsonPath("$.['Отсутствует обязательный заголовок']", containsString("Required request header 'X-Sharer-User-Id' for method parameter type long is not present")));
    }

    @Test
    void editItem() throws Exception {
        long userId = 1L;
        long itemId = 1L;

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Name");
        itemDto.setDescription("Description");
        itemDto.setIsAvailableForRent(true);

        when(itemClient.editItem(userId, itemId, itemDto))
                .thenReturn(ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(itemDto));

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getItem() throws Exception {
        long itemId = 1L;

        mvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getItems() throws Exception {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void searchItems() throws Exception {
        String searchingSubstring = "Item";

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", searchingSubstring))
                .andExpect(status().isOk());
    }

    @Test
    void searchItemsWithNullTextParam() throws Exception {
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['Отсутствует обязательный параметр']").exists())
                .andExpect(jsonPath("$.['Отсутствует обязательный параметр']", containsString("Required request parameter 'text' for method parameter type String is not present")));
    }

    @Test
    void searchItemsWithEmptyTextParam() throws Exception {
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['Ошибка валидации']").exists())
                .andExpect(jsonPath("$.['Ошибка валидации']", containsString("must not be blank")));
    }

    @Test
    void searchItemsWithBlankTextParam() throws Exception {
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "    "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['Ошибка валидации']").exists())
                .andExpect(jsonPath("$.['Ошибка валидации']", containsString("must not be blank")));
    }

    @Test
    void createComment() throws Exception {
        long authorId = 1L;
        long itemId = 1L;

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Text");

        when(itemClient.createComment(authorId, itemId, commentDto))
                .thenReturn(ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(commentDto));

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }

    @Test
    void createCommentWithNullText() throws Exception {
        long itemId = 1L;

        CommentDto commentDto = new CommentDto();
        commentDto.setText(null);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Текст комментария не может быть пустым или состоять только из пробелов")));
    }

    @Test
    void createCommentWithBlankText() throws Exception {
        long itemId = 1L;

        CommentDto commentDto = new CommentDto();
        commentDto.setText("     ");

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Текст комментария не может быть пустым или состоять только из пробелов")));
    }

    @Test
    void createCommentWithEmptyText() throws Exception {
        long itemId = 1L;

        CommentDto commentDto = new CommentDto();
        commentDto.setText("");

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Текст комментария не может быть пустым или состоять только из пробелов")));
    }
}
