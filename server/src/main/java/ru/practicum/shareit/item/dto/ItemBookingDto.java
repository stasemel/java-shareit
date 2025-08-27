package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class ItemBookingDto {
    Long id;
    Long ownerId;
    String name;
    String description;
    Long requestId;
    Boolean available;
    LocalDateTime lastBooking;
    LocalDateTime nextBooking;
    List<CommentDto> comments;
}
