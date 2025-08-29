package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRequestState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    private final BookingService service;
    private final ItemService itemService;
    private final UserService userService;
    private UserDto ownerUserDto;
    private UserDto bookerUserDto;
    private ItemDto itemDtoAvailable;
    private ItemDto itemDtoUnAvailable;


    @BeforeEach
    void setUp() {
        UserCreateDto ownerUserCreateDto = new UserCreateDto("User Useroff", "useroff@yandex.ru");
        this.ownerUserDto = userService.create(ownerUserCreateDto);
        UserCreateDto bookerUserCreateDto = new UserCreateDto("Booker Useroff", "booker.useroff@yandex.ru");
        this.bookerUserDto = userService.create(bookerUserCreateDto);
        ItemCreateDto itemCreateDto = new ItemCreateDto("Item", "Item's description", true, null);
        this.itemDtoAvailable = itemService.create(itemCreateDto, ownerUserDto.getId());
        ItemCreateDto itemCreateDtoNA = new ItemCreateDto("Not available item", "Item's description fro not available item", false, null);
        this.itemDtoUnAvailable = itemService.create(itemCreateDtoNA, ownerUserDto.getId());
    }

    @Test
    void create() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2),
                itemDtoAvailable.getId());

        BookingDto bookingDto = service.create(bookingCreateDto, bookerUserDto.getId());

        assertThat(bookingDto.getId(), notNullValue());
        assertThat(bookingDto.getItem().getId(), equalTo(itemDtoAvailable.getId()));
        assertThat(bookingDto.getStart(), equalTo(bookingCreateDto.getStart()));
        assertThat(bookingDto.getEnd(), equalTo(bookingCreateDto.getEnd()));
        assertThat(bookingDto.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void createWithWrongStartAndEnd() {
        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto bookingCreateDtoPastStart = new BookingCreateDto(
                now.minusDays(1),
                now.plusMinutes(2),
                itemDtoAvailable.getId());
        BookingCreateDto bookingCreateDtoPastEnd = new BookingCreateDto(
                now.plusDays(1),
                now.minusDays(1),
                itemDtoAvailable.getId());
        BookingCreateDto bookingCreateDtoStartAfterEnd = new BookingCreateDto(
                now.plusMinutes(2),
                now.plusMinutes(1),
                itemDtoAvailable.getId());

        BookingCreateDto bookingCreateDtoStartEqualsEnd = new BookingCreateDto(
                now.plusMinutes(1),
                now.plusMinutes(1),
                itemDtoAvailable.getId());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.create(bookingCreateDtoPastStart, bookerUserDto.getId()));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.create(bookingCreateDtoPastEnd, bookerUserDto.getId()));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.create(bookingCreateDtoStartAfterEnd, bookerUserDto.getId()));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.create(bookingCreateDtoStartEqualsEnd, bookerUserDto.getId()));
    }

    @Test
    void createWithUnAvailableItemMustThrownException() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2),
                itemDtoUnAvailable.getId());

        Assertions.assertThrows(UnavailableException.class, () -> service.create(bookingCreateDto, bookerUserDto.getId()));
    }

    @Test
    void createWithUnknownItemMustThrownException() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2),
                5L);

        Assertions.assertThrows(NotFoundException.class, () -> service.create(bookingCreateDto, bookerUserDto.getId()));
    }

    private BookingDto createBooking() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2),
                itemDtoAvailable.getId());
        return service.create(bookingCreateDto, bookerUserDto.getId());
    }

    @Test
    void approved() {
        BookingDto bookingDto = createBooking();

        BookingDto approvedBooking = service.approved(bookingDto.getId(), true, ownerUserDto.getId());

        assertThat(approvedBooking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void approvedWithWrongUserMustThrownException() {
        BookingDto bookingDto = createBooking();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.approved(bookingDto.getId(), true, bookerUserDto.getId()));
    }

    @Test
    void getBookingsByUser() {
        BookingDto bookingDto = createBooking();

        List<BookingDto> listForOwner = (List<BookingDto>) service.getBookingsByUser(ownerUserDto.getId(), BookingRequestState.ALL);
        List<BookingDto> listForOwnerPast = (List<BookingDto>) service.getBookingsByUser(ownerUserDto.getId(), BookingRequestState.PAST);
        List<BookingDto> listForOwnerCurrent = (List<BookingDto>) service.getBookingsByUser(ownerUserDto.getId(), BookingRequestState.CURRENT);
        List<BookingDto> listForOwnerFuture = (List<BookingDto>) service.getBookingsByUser(ownerUserDto.getId(), BookingRequestState.FUTURE);
        List<BookingDto> listForOwnerWaiting = (List<BookingDto>) service.getBookingsByUser(ownerUserDto.getId(), BookingRequestState.WAITING);
        List<BookingDto> listForOwnerRejected = (List<BookingDto>) service.getBookingsByUser(ownerUserDto.getId(), BookingRequestState.REJECTED);
        List<BookingDto> listForBooker = (List<BookingDto>) service.getBookingsByUser(bookerUserDto.getId(), BookingRequestState.ALL);

        assertThat(listForOwner.size(), equalTo(0));
        assertThat(listForOwnerPast.size(), equalTo(0));
        assertThat(listForOwnerCurrent.size(), equalTo(0));
        assertThat(listForOwnerWaiting.size(), equalTo(0));
        assertThat(listForOwnerFuture.size(), equalTo(0));
        assertThat(listForOwnerRejected.size(), equalTo(0));
        assertThat(listForBooker.size(), equalTo(1));
        assertThat(listForBooker.getFirst().getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void getBookingByOwnerId() {
        BookingDto bookingDto = createBooking();

        List<BookingDto> listForOwner = service.getBookingByOwnerId(ownerUserDto.getId(), BookingRequestState.ALL);
        List<BookingDto> listForOwnerPast = service.getBookingByOwnerId(ownerUserDto.getId(), BookingRequestState.PAST);
        List<BookingDto> listForOwnerCurrent = service.getBookingByOwnerId(ownerUserDto.getId(), BookingRequestState.CURRENT);
        List<BookingDto> listForOwnerFuture = service.getBookingByOwnerId(ownerUserDto.getId(), BookingRequestState.FUTURE);
        List<BookingDto> listForOwnerWaiting = service.getBookingByOwnerId(ownerUserDto.getId(), BookingRequestState.WAITING);
        List<BookingDto> listForOwnerRejected = service.getBookingByOwnerId(ownerUserDto.getId(), BookingRequestState.REJECTED);
        List<BookingDto> listForBooker = service.getBookingByOwnerId(bookerUserDto.getId(), BookingRequestState.ALL);

        assertThat(listForOwner.size(), equalTo(1));
        assertThat(listForOwner.getFirst().getId(), equalTo(bookingDto.getId()));
        assertThat(listForOwnerPast.size(), equalTo(0));
        assertThat(listForOwnerCurrent.size(), equalTo(0));
        assertThat(listForOwnerWaiting.size(), equalTo(1));
        assertThat(listForOwnerFuture.size(), equalTo(1));
        assertThat(listForOwnerRejected.size(), equalTo(0));
        assertThat(listForBooker.size(), equalTo(0));
    }
}