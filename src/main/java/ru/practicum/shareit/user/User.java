package ru.practicum.shareit.user;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users", schema = "public")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long id;
    private String name;
    private String email;
}
