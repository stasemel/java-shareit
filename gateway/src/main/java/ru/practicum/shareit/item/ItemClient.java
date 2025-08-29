package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(ItemCreateDto itemCreateDto, Long ownerId) {
        return post("", ownerId, itemCreateDto);
    }

    public ResponseEntity<Object> update(ItemUpdateDto itemUpdateDto, Long itemId, Long ownerId) {
        return patch("/" + itemId, ownerId, itemUpdateDto);
    }

    public ResponseEntity<Object> getItemBookingDtoById(Long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemsByOwnerId(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> searchItemsWithText(String text, Long ownerId) {
        return get("/search?text=" + text, ownerId);
    }

    public ResponseEntity<Object> createComment(CommentCreateDto commentCreateDto, Long itemId, Long userId) {
        return post("/" + itemId + "/comment", userId, commentCreateDto);
    }
}
