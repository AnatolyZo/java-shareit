package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(long userId);

    @Query(value = "SELECT i from Item as i WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "AND i.isAvailableForRent = true AND ?1 <> '' ")
    List<Item> findByNameOrDescription(String searchingSubstring);

    List<Item> findByRequestId(long requestId);

    List<Item> findByRequestIdIsNotNull();
}
