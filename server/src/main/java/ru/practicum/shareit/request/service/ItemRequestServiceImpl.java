package ru.practicum.shareit.request.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Data
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final ItemRequestMapper mapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequestDto create(ItemRequestCreateDto itemRequestCreateDto, Long requestorId) {
        User requestor = itemService.getUser(requestorId);
        ItemRequest itemRequest = mapper.toModel(itemRequestCreateDto);
        itemRequest.setRequestor(requestor);
        return mapper.toDto(repository.save(itemRequest));
    }

    @Override
    public List<ItemRequestWithItemsDto> getRequestsByUser(Long userId) {
        User user = itemService.getUser(userId);
        List<ItemRequest> listItemRequest = repository.findByRequestorIdOrderByCreatedDesc(user.getId());
        return listItemRequest.stream().map(this::getRequestDto).toList();
    }

    @Override
    public List<ItemRequestDto> getRequestsByOtherUsers(Long userId) {
        User user = itemService.getUser(userId);
        List<ItemRequest> list = repository.findByRequestorIdNotOrderByCreatedDesc(user.getId());
        return list.stream().map(mapper::toDto).toList();
    }

    @Override
    public ItemRequestWithItemsDto getRequestsById(Long requestId) {
        Optional<ItemRequest> optItemRequest = repository.findById(requestId);
        if (optItemRequest.isEmpty()) {
            throw new NotFoundException(String.format("Не найден запрос %d", requestId));
        }
        return getRequestDto(optItemRequest.get());
    }

    private ItemRequestWithItemsDto getRequestDto(ItemRequest itemRequest) {
        ItemRequestWithItemsDto itemRequestWithItemsDto = mapper.toDtoWithItems(itemRequest);
        List<Item> list = itemRepository.findByRequestId(itemRequest.getId());
        itemRequestWithItemsDto.setItems(list.stream().map(itemMapper::toRequestDto).toList());
        return itemRequestWithItemsDto;
    }
}
