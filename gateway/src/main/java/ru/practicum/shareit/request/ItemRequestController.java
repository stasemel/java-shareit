package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.Utility;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

/**
 *  Sprint add-item-requests.
 */
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto,
                                                    @Valid @RequestHeader(Utility.HEADER_USER) Long requestorId) {
        return requestClient.create(itemRequestCreateDto, requestorId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByUser(@Valid @RequestHeader(Utility.HEADER_USER) Long userId) {
        return requestClient.getRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsByOtherUsers(@Valid @RequestHeader(Utility.HEADER_USER) Long userId) {
        return requestClient.getRequestsByOtherUsers(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@Valid @PathVariable Long requestId) {
        return requestClient.getRequestsById(requestId);
    }
}
