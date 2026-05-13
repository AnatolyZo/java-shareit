package ru.practicum.shareit.parent;

import java.util.HashMap;
import java.util.Map;

public class ParentStorage<T> {
    protected final Map<Long, T> elementsMap = new HashMap<>();

    protected long createNextId() {
        long currentMaxId = elementsMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }
}
