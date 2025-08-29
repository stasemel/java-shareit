package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.Utility;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemCreateDto itemCreateDto,
                                         @Valid @RequestHeader(Utility.HEADER_USER) Long ownerId) {
        log.info("Create item {}, ownerId = {}", itemCreateDto, ownerId);
        validateOwner(ownerId);
        return itemClient.create(itemCreateDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Valid @RequestBody ItemUpdateDto itemUpdateDto,
                                         @Valid @RequestHeader(Utility.HEADER_USER) Long ownerId,
                                         @Valid @PathVariable Long itemId) {
        log.info("Update item {}, id = {}, ownerId = {}", itemUpdateDto, itemId, ownerId);
        validateOwner(ownerId);
        return itemClient.update(itemUpdateDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@Valid @PathVariable Long itemId,
                                          @Valid @RequestHeader(Utility.HEADER_USER) Long userId) {
        log.info("Get item id = {}", itemId);
        return itemClient.getItemBookingDtoById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@Valid @RequestHeader(Utility.HEADER_USER) Long ownerId) {
        log.info("Get items by ownerId = {}", ownerId);
        validateOwner(ownerId);
        return itemClient.getItemsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@Valid @RequestHeader(Utility.HEADER_USER) Long ownerId,
                                              @Valid @RequestParam(Utility.REQUEST_PARAM_SEARCH_TEXT) String text) {
        log.info("Search items by text = {} ownerId = {}", text, ownerId);
        validateOwner(ownerId);
        return itemClient.searchItemsWithText(text, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentCreateDto commentCreateDto,
                                                @Valid @PathVariable Long itemId,
                                                @Valid @RequestHeader(Utility.HEADER_USER) Long userId) {
        log.info("Create comment for item id = {}, user id = {}, comment = {}", itemId, userId, commentCreateDto);
        return itemClient.createComment(commentCreateDto, itemId, userId);
    }

    private void validateOwner(Long ownerId) {
        if (ownerId == null) {
            throw new IllegalArgumentException("Id владельца должен быть указан");
        }
    }

}
