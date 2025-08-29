package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.Utility;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto createItemRequest(@Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto,
                                            @Valid @RequestHeader(Utility.HEADER_USER) Long requestorId) {
        return service.create(itemRequestCreateDto, requestorId);
    }

    @GetMapping
    public List<ItemRequestWithItemsDto> getRequestsByUser(@Valid @RequestHeader(Utility.HEADER_USER) Long userId) {
        return service.getRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequestsByOtherUsers(@Valid @RequestHeader(Utility.HEADER_USER) Long userId) {
        return service.getRequestsByOtherUsers(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto getRequestById(@Valid @PathVariable Long requestId) {
        return service.getRequestsById(requestId);
    }
}
