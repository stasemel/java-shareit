package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
public class UserDto {
    Long id;
    String name;
    String email;
}
