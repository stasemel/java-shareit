package ru.practicum.shareit.user.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateDto {
    Long id;
    String name;
    @Email(message = "Email должен быть в правильном формате")
    @Nullable
    String email;
}
