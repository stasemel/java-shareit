package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceTest {
    private final ItemRequestService service;
    private final UserService userService;
    private final ItemService itemService;
    private UserDto requestorUserDto;
    private UserDto otherUserDto;

    @BeforeEach
    void setUp() {
        UserCreateDto userCreateDto = new UserCreateDto("User Useroff", "useroff@yandex.ru");
        this.requestorUserDto = userService.create(userCreateDto);
        UserCreateDto otherUserCreateDto = new UserCreateDto("Other Useroff", "other.useroff@yandex.ru");
        this.otherUserDto = userService.create(otherUserCreateDto);
    }

    @Test
    void createItemRequest() {
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("I want some items");

        ItemRequestDto itemRequestDto = service.create(itemRequestCreateDto, requestorUserDto.getId());

        assertThat(itemRequestDto.getId(), notNullValue());
        assertThat(itemRequestDto.getDescription(), equalTo(itemRequestCreateDto.getDescription()));
        assertThat(itemRequestDto.getRequestorId(), equalTo(requestorUserDto.getId()));
    }

    private ItemRequestDto createRequest() {
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("I want some items");
        return service.create(itemRequestCreateDto, requestorUserDto.getId());

    }

    @Test
    void getRequestsByUser() {
        ItemRequestDto itemRequestDto = createRequest();

        List<ItemRequestWithItemsDto> list = service.getRequestsByUser(requestorUserDto.getId());
        List<ItemRequestWithItemsDto> otherList = service.getRequestsByUser(otherUserDto.getId());

        assertThat(list.size(), equalTo(1));
        assertThat(list.getFirst().getId(), equalTo(itemRequestDto.getId()));
        assertThat(otherList.size(), equalTo(0));
    }

    @Test
    void getRequestsByOtherUsers() {
        ItemRequestDto itemRequestDto = createRequest();
        List<ItemRequestDto> otherList = service.getRequestsByOtherUsers(otherUserDto.getId());
        List<ItemRequestDto> requestorList = service.getRequestsByOtherUsers(requestorUserDto.getId());

        assertThat(otherList.size(), equalTo(1));
        assertThat(otherList.getFirst().getId(), equalTo(itemRequestDto.getId()));
        assertThat(requestorList.size(), equalTo(0));
    }

    @Test
    void getRequestById() {
        ItemRequestDto itemRequestDto = createRequest();
        ItemCreateDto itemCreateDto = new ItemCreateDto("Item", "Description", true, itemRequestDto.getId());
        ItemDto itemDto = itemService.create(itemCreateDto, otherUserDto.getId());

        ItemRequestWithItemsDto itemRequestWithItemsDto = service.getRequestsById(itemRequestDto.getId());

        assertThat(itemRequestWithItemsDto.getId(), equalTo(itemRequestDto.getId()));
        assertThat(itemRequestWithItemsDto.getItems().size(), equalTo(1));
        assertThat(itemRequestWithItemsDto.getItems().getFirst().getId(), equalTo(itemDto.getId()));
    }
}