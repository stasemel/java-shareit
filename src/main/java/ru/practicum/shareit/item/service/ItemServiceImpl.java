package ru.practicum.shareit.item.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Data
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final ItemMapper mapper;

    @Override
    public ItemDto create(ItemCreateDto itemCreateDto, Long ownerId) {
        User owner = getOwnerById(ownerId);
        Item item = mapper.toModel(itemCreateDto);
        item.setOwner(owner);
        return mapper.toDto(repository.save(item));
    }

    @Override
    public ItemDto update(ItemUpdateDto itemUpdateDto, Long ownerId) {
        User owner = getOwnerById(ownerId);
        Item item = mapper.toModel(itemUpdateDto);
        Optional<Item> optionalItem = repository.getItemById(item.getId());
        if (optionalItem.isEmpty()) {
            throw new NotFoundException(String.format("Не найдена вещь с id = %d", item.getId()));
        }
        Item savedItem = optionalItem.get();
        if (!savedItem.getOwner().equals(owner)) {
            throw new ForbiddenException("Вещь принадлежит другому пользователю");
        }
        if ((itemUpdateDto.getName() != null) && (!itemUpdateDto.getName().isBlank())) {
            savedItem.setName(itemUpdateDto.getName());
        }
        if ((itemUpdateDto.getDescription() != null) && (!itemUpdateDto.getDescription().isBlank())) {
            savedItem.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.getAvailable() != null) {
            savedItem.setAvailable(itemUpdateDto.getAvailable());
        }
        return mapper.toDto(repository.update(savedItem));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Optional<Item> optionalItem = repository.getItemById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException(String.format("Не найдена вещь с id = %d", itemId));
        }
        return mapper.toDto(optionalItem.get());
    }

    @Override
    public Collection<ItemDto> getItemsByOwnerId(Long ownerId) {
        User owner = getOwnerById(ownerId);
        List<Item> list = repository.getItemsByOwner(owner);
        return list.stream().map(mapper::toDto).toList();
    }

    @Override
    public Collection<ItemDto> searchItemsWithText(String text, Long ownerId) {
        List<Item> list = repository.searchItemsWithText(text, ownerId);
        return list.stream().map(mapper::toDto).toList();
    }

    private User getOwnerById(Long ownerId) {
        User owner = userRepository.getUserById(ownerId);
        if (owner == null) {
            throw new NotFoundException(String.format("Не найден пользователь с id = %d", ownerId));
        }
        return owner;
    }
}
