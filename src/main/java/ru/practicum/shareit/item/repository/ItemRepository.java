package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    Optional<Item> getItemById(Long id);

    Item update(Item savedItem);

    List<Item> getItemsByOwner(User owner);

    List<Item> searchItemsWithText(String text, Long ownerId);
}
