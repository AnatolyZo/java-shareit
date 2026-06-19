package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GetItemRequestIntegrationalTests {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void createAndGetItemRequest() {
        UserDto requestorDto = new UserDto();
        requestorDto.setName("Requestor1");
        requestorDto.setEmail("requestor1@email.ru");

        UserDtoResponse requestor = userService.createUser(requestorDto);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Description1");

        ItemRequestDtoResponse createdItemRequest = itemRequestService.createItemRequest(requestor.getId(), itemRequestDto);

        UserDto ownerDto = new UserDto();
        ownerDto.setName("Owner1");
        ownerDto.setEmail("owner1@email.ru");

        UserDtoResponse owner = userService.createUser(ownerDto);

        ItemDto availableItemDto = new ItemDto();
        availableItemDto.setName("Item1");
        availableItemDto.setDescription("Description1");
        availableItemDto.setIsAvailableForRent(true);
        availableItemDto.setRequestId(createdItemRequest.getId());

        ItemDtoResponse item = itemService.createItem(owner.getId(), availableItemDto);

        ItemRequestDtoResponse retrievedItemRequest = itemRequestService.getRequestById(requestor.getId(), createdItemRequest.getId());

        ItemRequestDtoResponse expectedItemRequest = new ItemRequestDtoResponse();
        expectedItemRequest.setId(createdItemRequest.getId());
        expectedItemRequest.setItems(List.of(item));
        expectedItemRequest.setDescription(itemRequestDto.getDescription());
        expectedItemRequest.setRequestorId(requestor.getId());
        expectedItemRequest.setCreated(retrievedItemRequest.getCreated());

        assertThat(retrievedItemRequest, equalTo(expectedItemRequest));
    }

    @Test
    void getNotExistentItemRequest() {
        UserDto requestorDto = new UserDto();
        requestorDto.setName("Requestor2");
        requestorDto.setEmail("requestor2@email.ru");

        UserDtoResponse requestor = userService.createUser(requestorDto);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Description2");

        ItemRequestDtoResponse createdItemRequest = itemRequestService.createItemRequest(requestor.getId(), itemRequestDto);

        UserDto ownerDto = new UserDto();
        ownerDto.setName("Owner2");
        ownerDto.setEmail("owner2@email.ru");

        UserDtoResponse owner = userService.createUser(ownerDto);

        ItemDto availableItemDto = new ItemDto();
        availableItemDto.setName("Item2");
        availableItemDto.setDescription("Description2");
        availableItemDto.setIsAvailableForRent(true);
        availableItemDto.setRequestId(createdItemRequest.getId());

        ItemDtoResponse item = itemService.createItem(owner.getId(), availableItemDto);

        //Пытаемся получить запрос со следующим id относительно последнего созданного
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(requestor.getId(), createdItemRequest.getId() + 1));

        assertEquals(String.format("Запрос на вещь с id %d отсутствует", createdItemRequest.getId() + 1), exception.getMessage());
    }

    @Test
    void getFailedToCreateItemRequest() {
        UserDto requestorDto = new UserDto();
        requestorDto.setName("Requestor2");
        requestorDto.setEmail("requestor2@email.ru");

        UserDtoResponse requestor = userService.createUser(requestorDto);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Description2");

        ItemRequestDtoResponse createdItemRequest = itemRequestService.createItemRequest(requestor.getId(), itemRequestDto);

        //Пытаемся создать пользователя со следующим id относительно последнего созданного
        final NotFoundException exception1 = assertThrows(NotFoundException.class,
                () -> itemRequestService.createItemRequest(requestor.getId() + 1, itemRequestDto));

        assertEquals(String.format("Пользователь с id %d отсутствует", requestor.getId() + 1), exception1.getMessage());

        //Пытаемся получить запрос со следующим id относительно последнего созданного,
        //если бы предыдущий метод на создание сработал
        final NotFoundException exception2 = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(requestor.getId(), createdItemRequest.getId() + 1));

        assertEquals(String.format("Запрос на вещь с id %d отсутствует", createdItemRequest.getId() + 1), exception2.getMessage());
    }
}
