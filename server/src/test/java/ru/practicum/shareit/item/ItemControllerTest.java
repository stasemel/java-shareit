package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.Utility;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @MockBean
    ItemService service;

    private final ItemDto itemDto = new ItemDto(1L, 2L, "Item", "Description", null, true);
    private final ItemBookingDto itemBookingDto = new ItemBookingDto(1L, 2L, "Item BookingDto", "Description BookingDto", null, true, LocalDateTime.now(), LocalDateTime.now(), null);

    private final CommentDto commentDto = new CommentDto(1L, "Author", "Text", Instant.now());

    @Test
    void create() throws Exception {
        ItemCreateDto itemCreateDto = new ItemCreateDto("Item", "Description", true, null);
        when(service.create(any(), any())).thenReturn(itemDto);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemCreateDto))
                        .header(Utility.HEADER_USER, 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
    }

    @Test
    void update() throws Exception {
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(1L, "New Item", "", false);
        when(service.update(any(), any())).thenReturn(itemDto);
        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemUpdateDto))
                        .header(Utility.HEADER_USER, 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
    }

    @Test
    void getItem() throws Exception {
        when(service.getItemBookingDtoById(any(), any())).thenReturn(itemBookingDto);
        mvc.perform(get("/items/1")
                        .header(Utility.HEADER_USER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemBookingDto.getName()));
    }

    @Test
    void getItemsByOwner() throws Exception {
        List<ItemBookingDto> list = List.of(itemBookingDto);
        when(service.getItemsByOwnerId(any())).thenReturn(list);
        mvc.perform(get("/items").header(Utility.HEADER_USER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(itemBookingDto.getName()));
    }

    @Test
    void searchItems() throws Exception {
        List<ItemDto> list = List.of(itemDto);
        when(service.searchItemsWithText(any(), any())).thenReturn(list);
        mvc.perform(get("/items/search?text=text").header(Utility.HEADER_USER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()));
    }

    @Test
    void createComment() throws Exception {
        CommentCreateDto commentCreateDto = new CommentCreateDto("Text");
        when(service.createComment(any(), any(), any())).thenReturn(commentDto);
        mvc.perform(post("/items/1/comment")
                        .header(Utility.HEADER_USER, 2L)
                        .content(mapper.writeValueAsString(commentCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(commentDto.getText()));
    }
}