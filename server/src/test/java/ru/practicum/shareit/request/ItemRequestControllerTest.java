package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.Utility;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @MockBean
    ItemRequestService service;

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Description", 2L, Instant.now());
    private final ItemRequestWithItemsDto itemRequestWithItemsDto = new ItemRequestWithItemsDto(3L, Instant.now(), "Description With Items", null);

    @Test
    void createItemRequest() throws Exception {
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("Text");
        when(service.create(any(), any())).thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestCreateDto))
                        .header(Utility.HEADER_USER, 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()));
    }

    @Test
    void getRequestsByUser() throws Exception {
        List<ItemRequestWithItemsDto> list = List.of(itemRequestWithItemsDto);
        when(service.getRequestsByUser(any())).thenReturn(list);
        mvc.perform(get("/requests")
                        .header(Utility.HEADER_USER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value(itemRequestWithItemsDto.getDescription()));
    }

    @Test
    void getRequestsByOtherUsers() throws Exception {
        List<ItemRequestDto> list = List.of(itemRequestDto);
        when(service.getRequestsByOtherUsers(any())).thenReturn(list);
        mvc.perform(get("/requests/all")
                        .header(Utility.HEADER_USER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()));
    }

    @Test
    void getRequestById() throws Exception {
        when(service.getRequestsById(any())).thenReturn(itemRequestWithItemsDto);
        mvc.perform(get("/requests/1")
                        .header(Utility.HEADER_USER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(itemRequestWithItemsDto.getDescription()));
    }
}