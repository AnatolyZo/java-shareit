package ru.practicum.shareit.parent;

import java.util.HashMap;
import java.util.Map;

public class ParentStorage<T> {
    protected final Map<Long, T> elementsMap = new HashMap<>();
    protected long nextId = 1;

    protected long createNextId() {
        return nextId++;
    }
}
