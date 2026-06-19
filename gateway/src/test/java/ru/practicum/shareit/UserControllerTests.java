package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTests {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserClient userClient;

    @Test
    void createUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Name");
        userDto.setEmail("email@email.ru");

        when(userClient.createUser(userDto))
                .thenReturn(ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(userDto));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void createUserWithNullName() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName(null);
        userDto.setEmail("email@email.ru");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Имя пользователя не может быть пустым или состоять только из пробелов")));
    }

    @Test
    void createUserWithBlankName() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("    ");
        userDto.setEmail("email@email.ru");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Имя пользователя не может быть пустым или состоять только из пробелов")));
    }

    @Test
    void createUserWithEmptyName() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("");
        userDto.setEmail("email@email.ru");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Имя пользователя не может быть пустым или состоять только из пробелов")));
    }

    @Test
    void createUserWithNullEmail() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Name");
        userDto.setEmail(null);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Графа email не должна быть пустой")));
    }

    @Test
    void createUserWithBlankEmail() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Name");
        userDto.setEmail("    ");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Некорректный формат электронной почты")));
    }

    @Test
    void createUserWithEmptyEmail() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Name");
        userDto.setEmail("");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Графа email не должна быть пустой")));
    }

    @Test
    void createUserWithWrongEmail() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Name");
        userDto.setEmail("@email.ru");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Некорректный формат электронной почты")));

        UserDto userDto2 = new UserDto();
        userDto2.setName("Name");
        userDto2.setEmail("email@");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Некорректный формат электронной почты")));

        UserDto userDto3 = new UserDto();
        userDto3.setName("Name");
        userDto3.setEmail("email");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto3)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Некорректный формат электронной почты")));
    }

    @Test
    void editUser() throws Exception {
        long userId = 1L;

        UserDto userDto = new UserDto();
        userDto.setName("Name");
        userDto.setEmail("email@email.ru");

        when(userClient.editUser(userId, userDto))
                .thenReturn(ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(userDto));

        mvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void editUserWithBlankEmail() throws Exception {
        long userId = 1L;

        UserDto userDto = new UserDto();
        userDto.setName("Name");
        userDto.setEmail("    ");

        mvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Некорректный формат электронной почты")));
    }

    @Test
    void editUserWithWrongEmail() throws Exception {
        long userId = 1L;

        UserDto userDto = new UserDto();
        userDto.setName("Name");
        userDto.setEmail("@email.ru");

        mvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Некорректный формат электронной почты")));

        UserDto userDto2 = new UserDto();
        userDto2.setName("Name");
        userDto2.setEmail("email@");

        mvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Некорректный формат электронной почты")));

        UserDto userDto3 = new UserDto();
        userDto3.setName("Name");
        userDto3.setEmail("email");

        mvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto3)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", containsString("Некорректный формат электронной почты")));
    }

    @Test
    void getUser() throws Exception {
        long userId = 1L;

        mvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());
    }

    @Test
    void getUsers() throws Exception {
        long userId = 1L;

        mvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser() throws Exception {
        long userId = 1L;

        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());
    }
}
