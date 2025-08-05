package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {
    private User owner;
    private Long id;
    private String name;
    private String description;
    private ItemRequest request;
    private Boolean available;

}
