package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.Utility;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(Utility.HEADER_USER) long userId,
                                              @RequestParam(name = Utility.REQUEST_PARAM_STATE, defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(Utility.HEADER_USER) long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        validateStartAndEnd(requestDto.getStart(), requestDto.getEnd());
        return bookingClient.bookItem(userId, requestDto);
    }

    private void validateStartAndEnd(LocalDateTime start, LocalDateTime end) {
        LocalDateTime currentTime = LocalDateTime.now().minusMinutes(1); //запас времени на обработку запроса
        if (start.isBefore(currentTime)) {
            throw new IllegalArgumentException(String.format("Дата начала бронирования %s не может быть раньше текущей %s",
                    start, currentTime));
        }
        if (end.isBefore(currentTime)) {
            throw new IllegalArgumentException(String.format("Дата окончания бронирования %s не может быть раньше текущей %s",
                    end, currentTime));
        }
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

    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(Utility.HEADER_USER) long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approved(@Valid @PathVariable Long bookingId,
                                           @Valid @RequestParam(Utility.REQUEST_PARAM_APPROVED) Boolean approved,
                                           @Valid @RequestHeader(Utility.HEADER_USER) Long userId) {
        log.info("Approved booking id = {}, approved = {}", bookingId, approved);
        return bookingClient.approved(bookingId, approved, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwnerId(
            @Valid @RequestHeader(Utility.HEADER_USER) Long userId,
            @RequestParam(name = Utility.REQUEST_PARAM_STATE, defaultValue = "all") String stateParam) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get bookings by owner {} with state {}", userId, state);

        return bookingClient.getBookingByOwnerId(userId, state);
    }
}
