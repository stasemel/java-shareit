package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestCreateDto itemRequestCreateDto, Long requestorId);

    List<ItemRequestWithItemsDto> getRequestsByUser(Long userId);

    List<ItemRequestDto> getRequestsByOtherUsers(Long userId);

    ItemRequestWithItemsDto getRequestsById(Long requestId);
}
