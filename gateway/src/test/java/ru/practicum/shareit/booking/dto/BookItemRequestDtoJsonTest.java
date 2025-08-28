package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookItemRequestDtoJsonTest {
    private final JacksonTester<BookItemRequestDto> json;

    @Test
    public void testNormalDto() throws Exception {
        BookItemRequestDto bookItemRequestDto = new BookItemRequestDto(
                1L,
                LocalDateTime.of(2025, 8, 28, 21, 12, 35),
                LocalDateTime.of(2025, 8, 28, 21, 12, 37));
        JsonContent<BookItemRequestDto> result = json.write(bookItemRequestDto);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-08-28T21:12:35");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-08-28T21:12:37");
    }

}