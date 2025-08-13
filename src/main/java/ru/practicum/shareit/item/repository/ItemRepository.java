package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> getItemById(long id);

    List<Item> getItemsByOwnerId(Long ownerId);

    @Query(value = "SELECT i FROM Item i " +
            "WHERE (i.available = true) " +
            "AND (upper(i.name) like upper(concat('%',?1,'%')) " +
            "OR upper(i.description) like upper(concat('%',?1,'%'))) "
            )
    List<Item> searchItemsWithText(String text, Long ownerId);
}
