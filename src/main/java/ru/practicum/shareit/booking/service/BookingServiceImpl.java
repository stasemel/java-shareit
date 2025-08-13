package ru.practicum.shareit.booking.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingRequestState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Data
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper mapper;

    @Override
    @Transactional
    public BookingDto create(BookingCreateDto bookingCreateDto, Long bookerId) {
        Optional<Item> optItem = itemRepository.getItemById(bookingCreateDto.getItemId());
        if (optItem.isEmpty()) {
            throw new NotFoundException(String.format("Не найдена вещь с id = %d", bookingCreateDto.getItemId()));
        }
        if (!optItem.get().getAvailable()) {
            throw new UnavailableException(String.format("Вешь с id = %d недоступна", bookingCreateDto.getItemId()));
        }
        User user = getUser(bookerId);
        Booking booking = mapper.toModel(bookingCreateDto);
        booking.setBooker(user);
        booking.setItem(optItem.get());
        validateBooking(bookingCreateDto.getItemId(), booking.getStartDate(), booking.getEndDate());
        booking.setStatus(BookingStatus.WAITING);
        return mapper.toDto(repository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approved(Long bookingId, Boolean approved, Long userId) {
        Booking booking = getBooking(bookingId);
        User user = getUser(userId);
        if (!booking.getItem().getOwner().equals(user)) {
            throw new IllegalArgumentException(String.format("Вы не можете управлять бронированием %d", bookingId));
        }
        booking.setStatus((approved) ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return mapper.toDto(repository.save(booking));
    }

    @Override
    public Collection<BookingDto> getBookingByUser(Long userId, BookingRequestState state) {
        User booker = getUser(userId);
        switch (state) {
            case PAST:
                return returnListDto(repository.findByBookerIdPast(booker));
            case CURRENT:
                return returnListDto(repository.findByBookerIdCurrent(booker));
            case FUTURE:
                return returnListDto(repository.findByBookerIdFuture(booker));
            case WAITING:
                return returnListDto(repository.findByBookerAndStatusOrderByStartDateDesc(
                        booker,
                        BookingStatus.WAITING)
                );
            case REJECTED:
                return returnListDto(repository.findByBookerAndStatusOrderByStartDateDesc(
                        booker,
                        BookingStatus.REJECTED)
                );
            default:
                return returnListDto(repository.findByBookerIdOrderByStartDateDesc(userId));
        }
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        User user = getUser(userId);
        Booking booking = getBooking(bookingId);
        if ((booking.getBooker().equals(user)) || (booking.getItem().getOwner().equals(user))) {
            return mapper.toDto(booking);
        }
        throw new ForbiddenException(String.format("Вы не можете посмотреть информвацию о бронировании %d", bookingId));
    }

    @Override
    public List<BookingDto> getBookingByOwnerId(Long userId, BookingRequestState state) {
        User owner = getUser(userId);
        switch (state) {
            case PAST:
                return returnListDto(repository.findByOwnerPast(owner));
            case CURRENT:
                return returnListDto(repository.findByOwnerCurrent(owner));
            case FUTURE:
                return returnListDto(repository.findByOwnerFuture(owner));
            case WAITING:
                return returnListDto(repository.findByItemOwnerAndStatusOrderByStartDateDesc(owner, BookingStatus.WAITING));
            case REJECTED:
                return returnListDto(repository.findByItemOwnerAndStatusOrderByStartDateDesc(owner, BookingStatus.REJECTED));
            default:
                return returnListDto(repository.findByItemOwnerOrderByStartDateDesc(owner));
        }
    }

    private List<BookingDto> returnListDto(List<Booking> list) {
        return list.stream().map(mapper::toDto).toList();
    }

    private Booking getBooking(Long bookingId) {
        Optional<Booking> optionalBooking = repository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new NotFoundException(String.format("Не обнаружено бронирование id = %d", bookingId));
        }
        return optionalBooking.get();
    }

    private void validateBooking(Long itemId, LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException(
                    String.format("Дата начала аренды %s не может быть позже окончания %s", start, end)
            );
        }
        if (start.equals(end)) {
            throw new IllegalArgumentException(
                    String.format("Дата начала не должна совпадать с датой окончания %s", start)
            );
        }
        //Только APPROVED, а не APPROVED+WAITING, чтобы владелец мог сам выбрать кому отдать
        Set<BookingStatus> statuses = new HashSet<>(
                Arrays.asList(
                        BookingStatus.APPROVED
                )
        );
        List<Booking> list = repository.findBookingByItemIdBetweenDateAndStatusIn(
                itemId,
                start,
                end,
                statuses
        );
        if (!list.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("Уже есть бронь вещи с id = %d в период между %s и %s",
                            itemId,
                            start,
                            end)
            );
        }
    }

    private User getUser(Long userId) {
        Optional<User> optUser = userRepository.getUserById(userId);
        if (optUser.isEmpty()) {
            throw new IllegalArgumentException(String.format("Не найден пользователь с id = %d", userId));
        }
        return optUser.get();
    }
}
