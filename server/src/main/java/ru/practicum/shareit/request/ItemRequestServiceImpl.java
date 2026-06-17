package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.validation.EntityExistsValidationService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final EntityExistsValidationService entityExistsValidationService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDtoResponse createItemRequest(long userId, ItemRequestDto itemRequestDto) {
        log.trace("Пользователь с id {} инициировал создание запроса вещи \"{}\"", userId, itemRequestDto);

        User requestor = entityExistsValidationService.getUserByIdOrThrow(userId);
        ItemRequest convertedItemRequest = ItemRequestMapper.mapToItemRequest(itemRequestDto, requestor);
        ItemRequest createdItemRequest = itemRequestRepository.save(convertedItemRequest);

        log.debug("Создан запрос вещи {} и добавлен в хранилище", createdItemRequest);
        return ItemRequestMapper.mapToItemRequestDtoResponse(createdItemRequest, null);
    }

    @Override
    public List<ItemRequestDtoResponse> getUsersRequests(long userId) {
        log.trace("Пользователь с id {} инициировал получение своих запросов", userId);

        Map<Long, List<Item>> itemsGroupedByRequestId = getAndGroupItemsByRequest();

        return itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId)
                .stream()
                .map(itemRequest -> {
                    List<ItemDtoResponse> itemsToRequest = itemsGroupedByRequestId.getOrDefault(itemRequest.getId(), List.of())
                            .stream()
                            .map(ItemMapper::mapToItemDtoResponse)
                            .toList();

                    return ItemRequestMapper.mapToItemRequestDtoResponse(itemRequest, itemsToRequest);
                })
                .toList();
    }

    @Override
    public List<ItemRequestDtoResponse> getAllRequests(long userId) {
        log.trace("Пользователь с id {} инициировал получение всех запросов", userId);

        Map<Long, List<Item>> itemsGroupedByRequestId = getAndGroupItemsByRequest();

        return itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId)
                .stream()
                .map(itemRequest -> {
                    List<ItemDtoResponse> itemsToRequest = itemsGroupedByRequestId.getOrDefault(itemRequest.getId(), List.of())
                            .stream()
                            .map(ItemMapper::mapToItemDtoResponse)
                            .toList();

                    return ItemRequestMapper.mapToItemRequestDtoResponse(itemRequest, itemsToRequest);
                })
                .toList();
    }

    @Override
    public ItemRequestDtoResponse getRequestById(long userId, long requestId) {
        log.trace("Пользователь с id {} инициировал получение запроса с id {}", userId, requestId);

        List<ItemDtoResponse> itemsByRequest = itemRepository.findByRequestId(requestId)
                .stream()
                .map(ItemMapper::mapToItemDtoResponse)
                .toList();

        ItemRequest itemRequest = entityExistsValidationService.getItemRequestByIdOrThrow(requestId);
        return ItemRequestMapper.mapToItemRequestDtoResponse(itemRequest, itemsByRequest);
    }

    //Метод получает вещи с ненулевым requestId и группирует по нему
    private Map<Long, List<Item>> getAndGroupItemsByRequest() {
        List<Item> allItemsWithRequest = itemRepository.findByRequestIdIsNotNull();
        Map<Long, List<Item>> groupedItems = allItemsWithRequest.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        log.debug("Получена таблица вещей с группировкой по requestId: {}", groupedItems);

        return groupedItems;
    }
}
