package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;

public interface ItemService {

    ItemDto create(ItemCreateDto itemCreateDto, Long ownerId);

    ItemDto update(ItemUpdateDto itemUpdateDto, Long ownerId);

    ItemBookingDto getItemBookingDtoById(Long itemId, Long userId);

    Collection<ItemBookingDto> getItemsByOwnerId(Long ownerId);

    Collection<ItemDto> searchItemsWithText(String text, Long ownerId);

    CommentDto createComment(CommentCreateDto comment, Long itemId, Long userId);

}
