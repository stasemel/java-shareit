package ru.practicum.shareit.booking;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingCreateDto bookingCreateDto,
                             @Valid @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        log.info("Create booking = {}, booker = {}", bookingCreateDto, bookerId);
        return service.create(bookingCreateDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approved(@Valid @PathVariable Long bookingId,
                               @Valid @RequestParam("approved") Boolean approved,
                               @Valid @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Approved booking id = {}, approved = {}", bookingId, approved);
        return service.approved(bookingId, approved, userId);
    }

    @GetMapping
    public Collection<BookingDto> getBookingByUser(
            @Valid @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @Nullable @PathVariable(name = "state", required = false) BookingRequestState state) {
        if (state == null) {
            state = BookingRequestState.ALL;
        }
        return service.getBookingByUser(userId, state);
    }


    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@Valid @PathVariable Long bookingId,
                                     @Valid @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getBookingById(bookingId, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingByOwnerId(
            @Valid @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @Nullable @PathVariable(name = "state", required = false) BookingRequestState state) {
        if (state == null) state = BookingRequestState.ALL;
        return service.getBookingByOwnerId(userId, state);
    }
}

