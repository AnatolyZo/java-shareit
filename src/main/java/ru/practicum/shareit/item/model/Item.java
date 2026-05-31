package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Entity
@Table(name = "items", schema = "public")
@Data
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    private String name;
    private String description;
    @Column(name = "available")
    private boolean isAvailableForRent;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}
