package ru.practicum.shareit.item.repository;

import lombok.Data;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
@Data
public class ImMemoryItemRepository implements ItemRepository {
    private HashMap<Long, Item> items = new HashMap<>();
    private static Long ID_COUNT = 0L;

    @Override
    public Item save(Item item) {
        Long id = getNextId();
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        if (items.containsKey(id)) return Optional.of(items.get(id));
        return Optional.empty();
    }

    @Override
    public Item update(Item savedItem) {
        items.put(savedItem.getId(), savedItem);
        return savedItem;
    }

    @Override
    public List<Item> getItemsByOwner(User owner) {
        return items.values().stream().filter(item -> item.getOwner().equals(owner)).toList();
    }

    @Override
    public List<Item> searchItemsWithText(String text, Long ownerId) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        String searchText = text.toUpperCase();
        return items.values().stream()
                .filter(item ->
                        (item.getAvailable()) &&
                                (item.getName().toUpperCase().contains(searchText) ||
                                        item.getDescription().toUpperCase().contains(searchText)))
                .toList();
    }

    private Long getNextId() {
        return (++ID_COUNT);
    }
}
