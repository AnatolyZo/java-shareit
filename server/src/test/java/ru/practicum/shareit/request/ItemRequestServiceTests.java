package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.validation.EntityExistsValidationService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTests {
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private EntityExistsValidationService entityExistsValidationService;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void createItemRequest() {
        long userId = 2L;
        ItemRequestDto request = formItemRequestDto();
        User requestor = formRequestor();
        LocalDateTime created = LocalDateTime.now();
        ItemRequest savedItemRequest = formItemRequest(requestor, created);
        ItemRequestDtoResponse expectedItemRequest = formExpectedItemRequest(created);

        when(entityExistsValidationService.getUserByIdOrThrow(userId)).thenReturn(requestor);
        when(itemRequestRepository.save(any())).thenReturn(savedItemRequest);

        ItemRequestDtoResponse result = itemRequestService.createItemRequest(userId, request);

        assertThat(result, equalTo(expectedItemRequest));
    }

    @Test
    void createItemRequestWithNotExistentUser() {
        long userId = 999L;
        ItemRequestDto request = formItemRequestDto();

        when(entityExistsValidationService.getUserByIdOrThrow(userId))
                .thenThrow(new NotFoundException(String.format("Пользователь с id %d отсутствует", userId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.createItemRequest(userId, request));

        assertEquals(String.format("Пользователь с id %d отсутствует", userId), exception.getMessage());
    }

    @Test
    void getAllUsersItemRequests() {
        long userId = 2L;
        LocalDateTime created = LocalDateTime.now();
        List<ItemRequestDtoResponse> expectedItemRequests = List.of(formExpectedItemRequestWithItem(created));

        List<ItemRequest> itemRequests = List.of(formItemRequest(formRequestor(), created));
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId))
                .thenReturn(itemRequests);

        Item item = formItem();
        when(itemRepository.findByRequestIdIsNotNull()).thenReturn(List.of(item));

        List<ItemRequestDtoResponse> result = itemRequestService.getUsersRequests(userId);

        assertThat(result, equalTo(expectedItemRequests));
    }

    @Test
    void getAllItemRequests() {
        long userId = 2L;
        LocalDateTime created = LocalDateTime.now();
        List<ItemRequestDtoResponse> expectedItemRequests = List.of(formExpectedItemRequestWithItem(created));

        List<ItemRequest> itemRequests = List.of(formItemRequest(formRequestor(), created));
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId))
                .thenReturn(itemRequests);

        Item item = formItem();
        when(itemRepository.findByRequestIdIsNotNull()).thenReturn(List.of(item));

        List<ItemRequestDtoResponse> result = itemRequestService.getAllRequests(userId);

        assertThat(result, equalTo(expectedItemRequests));
    }

    @Test
    void getRequestById() {
        long userId = 2L;
        long requestId = 1L;
        LocalDateTime created = LocalDateTime.now();
        ItemRequestDtoResponse expectedItemRequest = formExpectedItemRequestWithItem(created);

        List<Item> items = List.of(formItem());
        when(itemRepository.findByRequestId(requestId))
                .thenReturn(items);

        ItemRequest itemRequest = formItemRequest(formRequestor(), created);
        when(entityExistsValidationService.getItemRequestByIdOrThrow(requestId)).thenReturn(itemRequest);

        ItemRequestDtoResponse result = itemRequestService.getRequestById(userId, requestId);

        assertThat(result, equalTo(expectedItemRequest));
    }

    @Test
    void getNotExistentRequest() {
        long userId = 2L;
        long requestId = 999L;

        List<Item> items = List.of(formItem());
        when(itemRepository.findByRequestId(requestId))
                .thenReturn(items);

        when(entityExistsValidationService.getItemRequestByIdOrThrow(requestId))
                .thenThrow(new NotFoundException(String.format("Запрос на вещь с id %d отсутствует", requestId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(userId, requestId));

        assertEquals(String.format("Запрос на вещь с id %d отсутствует", requestId), exception.getMessage());
    }

    private ItemRequestDto formItemRequestDto() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Description");

        return itemRequestDto;
    }

    private User formRequestor() {
        User requestor = new User();
        requestor.setId(2L);
        requestor.setName("Requestor");
        requestor.setEmail("requestor@email.ru");

        return requestor;
    }

    private ItemRequest formItemRequest(User requestor, LocalDateTime created) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Description");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(created);

        return itemRequest;
    }

    private ItemRequestDtoResponse formExpectedItemRequest(LocalDateTime created) {
        ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse();
        itemRequestDtoResponse.setId(1L);
        itemRequestDtoResponse.setDescription("Description");
        itemRequestDtoResponse.setRequestorId(2L);
        itemRequestDtoResponse.setCreated(created);

        return itemRequestDtoResponse;
    }

    private Item formItem() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@email.ru");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailableForRent(true);
        item.setRequest(itemRequest);

        return item;
    }

    private ItemRequestDtoResponse formExpectedItemRequestWithItem(LocalDateTime created) {
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

        ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse();
        itemRequestDtoResponse.setId(1L);
        itemRequestDtoResponse.setItems(List.of(item));
        itemRequestDtoResponse.setDescription("Description");
        itemRequestDtoResponse.setRequestorId(2L);
        itemRequestDtoResponse.setCreated(created);

        return itemRequestDtoResponse;
    }
}
