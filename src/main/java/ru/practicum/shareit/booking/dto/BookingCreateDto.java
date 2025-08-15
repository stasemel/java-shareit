package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
public class BookingCreateDto {
    @NotNull(message = "Дата начала аренды не может быть пустой")
    LocalDateTime start;
    @NotNull(message = "Дата окончания аренды не может быть пустой")
    LocalDateTime end;
    @NotNull(message = "Необходимо указать вешь для аренды")
    Long itemId;
}
