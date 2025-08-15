package ru.practicum.shareit.item.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Data
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper mapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto create(ItemCreateDto itemCreateDto, Long ownerId) {
        User owner = getUserById(ownerId);
        Item item = mapper.toModel(itemCreateDto);
        item.setOwner(owner);
        return mapper.toDto(repository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(ItemUpdateDto itemUpdateDto, Long ownerId) {
        User owner = getUserById(ownerId);
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
        return mapper.toDto(repository.save(savedItem));
    }

    @Override
    public ItemBookingDto getItemBookingDtoById(Long itemId, Long userId) {
        Item item = getItemById(itemId);
        User user = getUserById(userId);
        return toBookingDto(item, user);
    }

    private Item getItemById(Long itemId) {
        Optional<Item> optionalItem = repository.getItemById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException(String.format("Не найдена вещь с id = %d", itemId));
        }
        return optionalItem.get();
    }

    @Override
    public Collection<ItemBookingDto> getItemsByOwnerId(Long ownerId) {
        User owner = getUserById(ownerId);
        List<Item> list = repository.getItemsByOwner(owner);
        return list.stream().map(item -> toBookingDto(item, owner)).toList();
    }

    private ItemBookingDto toBookingDto(Item item, User user) {
        ItemBookingDto bookingDto = mapper.toBookingDto(item);
        if (item.getOwner().equals(user)) {
            Optional<Booking> optLastBooking =
                    bookingRepository.findFirstByItemAndEndDateBeforeAndStatusOrderByEndDateDesc(
                            item,
                            LocalDateTime.now(),
                            BookingStatus.APPROVED);
            Optional<Booking> optNextBooking =
                    bookingRepository.findFirstByItemAndStartDateAfterAndStatusOrderByStartDateDesc(
                            item,
                            LocalDateTime.now(),
                            BookingStatus.APPROVED);
            optLastBooking.ifPresent(booking -> bookingDto.setLastBooking(booking.getEndDate()));
            optNextBooking.ifPresent(booking -> bookingDto.setNextBooking(booking.getStartDate()));
        }
        List<CommentDto> comments = commentRepository
                .findByItemOrderByCreatedDesc(item)
                .stream()
                .map(commentMapper::toDto)
                .toList();
        bookingDto.setComments(comments);
        return bookingDto;
    }

    @Override
    public Collection<ItemDto> searchItemsWithText(String text, Long ownerId) {
        if ((text == null) || (text.isBlank())) return new ArrayList<>();
        List<Item> list = repository.searchItemsWithText(text, ownerId);
        return list.stream().map(mapper::toDto).toList();
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentCreateDto commentCreateDto, Long itemId, Long userId) {
        User user = getUserById(userId);
        Item item = getItemById(itemId);
        Optional<Booking> optionalBooking = bookingRepository.findFirstByItemAndBookerAndEndDateBefore(
                item,
                user,
                LocalDateTime.now());
        if (optionalBooking.isEmpty()) {
            throw new IllegalArgumentException(String.format("Не найдено законченное бронирование вещи id = %d пользователем %d",
                    itemId,
                    userId)
            );
        }
        Comment comment = commentMapper.toModel(commentCreateDto);
        comment.setItem(item);
        comment.setAuthor(user);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    private User getUserById(Long ownerId) {
        Optional<User> optUser = userRepository.getUserById(ownerId);
        if (optUser.isEmpty()) {
            throw new NotFoundException(String.format("Не найден пользователь с id = %d", ownerId));
        }
        return optUser.get();
    }
}
