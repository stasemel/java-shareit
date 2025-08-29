package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
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
class ItemServiceImplTest {
    private final ItemService service;
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final BookingService bookingService;
    private UserDto userDto;
    private UserDto secondUserDto;

    @BeforeEach
    void setUp() {
        UserCreateDto userCreateDto = new UserCreateDto("User Useroff", "useroff@yandex.ru");
        this.userDto = userService.create(userCreateDto);
        UserCreateDto userCreateDto2 = new UserCreateDto("ReqUser Useroff", "req.useroff@yandex.ru");
        this.secondUserDto = userService.create(userCreateDto2);
    }

    private ItemDto createItemFroTest() {
        ItemCreateDto itemCreateDto = new ItemCreateDto("Item", "Item's description", true, null);
        return service.create(itemCreateDto, userDto.getId());

    }

    private ItemRequestDto createRequest() {
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("Request Item");
        return itemRequestService.create(itemRequestCreateDto, userDto.getId());
    }

    private BookingDto createBooking(ItemDto itemDto) {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                itemDto.getId());
        BookingDto bookingDto = bookingService.create(bookingCreateDto, secondUserDto.getId());
        return bookingService.approved(bookingDto.getId(), true, userDto.getId());
    }

    @Test
    void create() {
        ItemCreateDto itemCreateDto = new ItemCreateDto("Item", "Item's description", true, null);

        ItemDto itemDto = service.create(itemCreateDto, userDto.getId());

        assertThat(itemDto.getId(), notNullValue());
        assertThat(itemDto.getName(), equalTo(itemCreateDto.getName()));
        assertThat(itemDto.getDescription(), equalTo(itemCreateDto.getDescription()));
        assertThat(itemDto.getOwnerId(), equalTo(userDto.getId()));
    }

    @Test
    void createWithUnknownRequestMustThrowException() {
        ItemCreateDto itemCreateDto = new ItemCreateDto("Item", "Item's description", true, 2L);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.create(itemCreateDto, userDto.getId()));
    }

    @Test
    void createWithRequest() {
        ItemRequestDto itemRequestDto = createRequest();
        ItemCreateDto itemCreateDto = new ItemCreateDto(
                "Item",
                "Item's description",
                true,
                itemRequestDto.getId());

        ItemDto itemDto = service.create(itemCreateDto, userDto.getId());

        assertThat(itemDto.getId(), notNullValue());
        assertThat(itemDto.getName(), equalTo(itemCreateDto.getName()));
        assertThat(itemDto.getDescription(), equalTo(itemCreateDto.getDescription()));
        assertThat(itemDto.getOwnerId(), equalTo(userDto.getId()));
        assertThat(itemDto.getRequestId(), equalTo(itemRequestDto.getId()));
    }

    @Test
    void update() {
        ItemDto itemDto = createItemFroTest();

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(itemDto.getId(), "", "", null);
        itemUpdateDto.setName("New Name");
        ItemDto itemChangeName = service.update(itemUpdateDto, userDto.getId());
        itemUpdateDto.setName("");
        itemUpdateDto.setDescription("New Description");
        ItemDto itemChangeDescription = service.update(itemUpdateDto, userDto.getId());
        itemUpdateDto.setDescription("");
        itemUpdateDto.setAvailable(false);
        ItemDto itemChangeAvailable = service.update(itemUpdateDto, userDto.getId());

        assertThat(itemChangeName.getId(), equalTo(itemDto.getId()));
        assertThat(itemChangeName.getName(), equalTo("New Name"));
        assertThat(itemChangeName.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(itemChangeName.getAvailable(), equalTo(itemDto.getAvailable()));

        assertThat(itemChangeDescription.getId(), equalTo(itemDto.getId()));
        assertThat(itemChangeDescription.getDescription(), equalTo("New Description"));
        assertThat(itemChangeDescription.getAvailable(), equalTo(itemDto.getAvailable()));

        assertThat(itemChangeAvailable.getId(), equalTo(itemDto.getId()));
        assertThat(itemChangeAvailable.getAvailable(), equalTo(false));
    }

    @Test
    void updateWithUnknownItemMustThrowException() {
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(1L, "Name", "", null);

        Assertions.assertThrows(NotFoundException.class,
                () -> service.update(itemUpdateDto, userDto.getId()));
    }

    @Test
    void updateWithWrongUserMustThrowException() {
        ItemDto itemDto = createItemFroTest();
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(itemDto.getId(), "New Name", "", null);

        Assertions.assertThrows(NotFoundException.class,
                () -> service.update(itemUpdateDto, 3L));
        Assertions.assertThrows(ForbiddenException.class,
                () -> service.update(itemUpdateDto, secondUserDto.getId()));
    }

    @Test
    void getItemBookingDtoById() {
        ItemDto itemDto = createItemFroTest();
        BookingDto bookingDto = createBooking(itemDto);
        ItemBookingDto itemBookingDto = service.getItemBookingDtoById(itemDto.getId(),
                userDto.getId());
        assertThat(itemBookingDto.getId(), equalTo(itemDto.getId()));
        assertThat(itemBookingDto.getNextBooking(), equalTo(bookingDto.getStart()));
    }

    @Test
    void getItemsByOwnerId() {
        ItemDto itemDto = createItemFroTest();

        List<ItemBookingDto> list = (List<ItemBookingDto>) service.getItemsByOwnerId(userDto.getId());

        assertThat(list.size(), equalTo(1));
        assertThat(list.getFirst().getId(), equalTo(itemDto.getId()));
    }

    @Test
    void searchItemsWithText() {
        ItemDto itemDto = createItemFroTest();

        List<ItemDto> list = (List<ItemDto>) service.searchItemsWithText("Item",
                userDto.getId());

        assertThat(list.size(), equalTo(1));
        assertThat(list.getFirst().getId(), equalTo(itemDto.getId()));
    }

    @Test
    void createCommentWithWrongItemAndUserMustThrowException() {
        CommentCreateDto commentCreateDto = new CommentCreateDto("Comment");
        ItemDto itemDto = createItemFroTest();
        createBooking(itemDto);

        Assertions.assertThrows(NotFoundException.class,
                () -> service.createComment(commentCreateDto, itemDto.getId(), 5L));
        Assertions.assertThrows(NotFoundException.class,
                () -> service.createComment(commentCreateDto, 5L, userDto.getId()));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.createComment(commentCreateDto, itemDto.getId(), userDto.getId()));
    }

    @Test
    void createCommentBeforeBookingEndMustThrowException() {
        CommentCreateDto commentCreateDto = new CommentCreateDto("Comment");
        ItemDto itemDto = createItemFroTest();
        createBooking(itemDto);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.createComment(commentCreateDto, itemDto.getId(),
                        secondUserDto.getId()));
    }

    @Test
    void createCommentAfterBookingEnd() throws InterruptedException {
        CommentCreateDto commentCreateDto = new CommentCreateDto("Comment");
        ItemDto itemDto = createItemFroTest();
        createBooking(itemDto);
        Thread.sleep(3000);
        CommentDto commentDto = service.createComment(commentCreateDto, itemDto.getId(),
                secondUserDto.getId());
        assertThat(commentDto.getId(), notNullValue());
        assertThat(commentDto.getText(), equalTo(commentCreateDto.getText()));
    }
}