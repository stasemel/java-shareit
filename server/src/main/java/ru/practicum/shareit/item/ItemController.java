package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.Utility;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemCreateDto itemCreateDto,
                          @Valid @RequestHeader(Utility.HEADER_USER) Long ownerId) {
        log.info("Create item {}, ownerId = {}", itemCreateDto, ownerId);
        validateOwner(ownerId);
        return service.create(itemCreateDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Valid @RequestBody ItemUpdateDto itemUpdateDto,
                          @Valid @RequestHeader(Utility.HEADER_USER) Long ownerId,
                          @Valid @PathVariable Long itemId) {
        log.info("Update item {}, id = {}, ownerId = {}", itemUpdateDto, itemId, ownerId);
        validateOwner(ownerId);
        itemUpdateDto.setId(itemId);
        return service.update(itemUpdateDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemBookingDto getItem(@Valid @PathVariable Long itemId,
                                  @Valid @RequestHeader(Utility.HEADER_USER) Long userId) {
        log.info("Get item id = {}", itemId);
        return service.getItemBookingDtoById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemBookingDto> getItemsByOwner(@Valid @RequestHeader(Utility.HEADER_USER) Long ownerId) {
        log.info("Get items by ownerId = {}", ownerId);
        validateOwner(ownerId);
        return service.getItemsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@Valid @RequestHeader(Utility.HEADER_USER) Long ownerId,
                                           @Valid @RequestParam(Utility.REQUEST_PARAM_SEARCH_TEXT) String text) {
        log.info("Search items by text = {} ownerId = {}", text, ownerId);
        validateOwner(ownerId);
        return service.searchItemsWithText(text, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentCreateDto commentCreateDto,
                                    @Valid @PathVariable Long itemId,
                                    @Valid @RequestHeader(Utility.HEADER_USER) Long userId) {
        log.info("Create comment for item id = {}, user id = {}, comment = {}", itemId, userId, commentCreateDto);
        return service.createComment(commentCreateDto, itemId, userId);
    }

    private void validateOwner(Long ownerId) {
        if (ownerId == null) {
            throw new IllegalArgumentException("Id владельца должен быть указан");
        }
    }
}
