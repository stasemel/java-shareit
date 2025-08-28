package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.Utility;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

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


@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @MockBean
    BookingService service;

    private final UserDto userDto = new UserDto(1L, "User", "user@userov.ru");
    private final ItemDto itemDto = new ItemDto(2L, 1L, "Item", "Description", null, true);
    private final BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now(), itemDto, userDto, BookingStatus.APPROVED);

    @Test
    void create() throws Exception {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2), 1L);
        when(service.create(any(), any())).thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .header(Utility.HEADER_USER, 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void approved() throws Exception {
        when(service.approved(any(), any(), any())).thenReturn(bookingDto);
        mvc.perform(patch("/bookings/2")
                        .param("approved", "true")
                        .header(Utility.HEADER_USER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingsByUser() throws Exception {
        List<BookingDto> list = List.of(bookingDto);
        when(service.getBookingsByUser(any(), any())).thenReturn(list);
        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header(Utility.HEADER_USER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }

    @Test
    void getBookingById() throws Exception {
        when(service.getBookingById(any(), any())).thenReturn(bookingDto);
        mvc.perform(get("/bookings/1").header(Utility.HEADER_USER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingsByOwnerId() throws Exception {
        List<BookingDto> list = List.of(bookingDto);
        when(service.getBookingByOwnerId(any(), any())).thenReturn(list);
        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header(Utility.HEADER_USER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }
}