package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingRequestState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;
import java.util.List;

public interface BookingService {
    BookingDto create(BookingCreateDto bookingCreateDto, Long bookerId);

    BookingDto approved(Long bookingId, Boolean approved, Long userId);

    Collection<BookingDto> getBookingsByUser(Long userId, BookingRequestState state);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getBookingByOwnerId(Long userId, BookingRequestState state);
}
