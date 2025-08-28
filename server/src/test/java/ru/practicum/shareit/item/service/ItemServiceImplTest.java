package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
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
class ItemServiceImplTest {
    private final ItemService service;
    private final UserService userService;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        UserCreateDto userCreateDto = new UserCreateDto("User Useroff", "useroff@yandex.ru");
        this.userDto = userService.create(userCreateDto);
    }

    private ItemDto createItemFroTest() {
        ItemCreateDto itemCreateDto = new ItemCreateDto("Item", "Item's description", true, null);
        return service.create(itemCreateDto, userDto.getId());

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
    void getItemsByOwnerId() {
        ItemDto itemDto = createItemFroTest();

        List<ItemBookingDto> list = (List<ItemBookingDto>) service.getItemsByOwnerId(userDto.getId());

        assertThat(list.size(), equalTo(1));
        assertThat(list.getFirst().getId(), equalTo(itemDto.getId()));
    }

    @Test
    void searchItemsWithText() {
        ItemDto itemDto = createItemFroTest();

        List<ItemDto> list = (List<ItemDto>) service.searchItemsWithText("Item", userDto.getId());

        assertThat(list.size(), equalTo(1));
        assertThat(list.getFirst().getId(), equalTo(itemDto.getId()));
    }

    @Test
    void createComment() {
    }
}