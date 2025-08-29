package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Component
public class ItemRequestMapper {
    public ItemRequest toModel(ItemRequestCreateDto itemRequestCreateDto) {
        return ItemRequest.builder().description(itemRequestCreateDto.getDescription()).build();
    }

    public ItemRequestDto toDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .created(itemRequest.getCreated())
                .description(itemRequest.getDescription())
                .requestorId(itemRequest.getRequestor().getId())
                .build();
    }

    public ru.practicum.shareit.request.dto.ItemRequestWithItemsDto toDtoWithItems(ItemRequest itemRequest) {
        return ru.practicum.shareit.request.dto.ItemRequestWithItemsDto.builder()
                .id(itemRequest.getId())
                .created(itemRequest.getCreated())
                .description(itemRequest.getDescription())
                .build();
    }
}
