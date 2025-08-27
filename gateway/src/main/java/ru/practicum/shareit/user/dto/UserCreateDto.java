package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class UserCreateDto {
    @NotBlank(message = "Имя должно быть указано")
    String name;
    @NotBlank(message = "Email должен быть указан")
    @Email(message = "Email должен быть указан в правильном формате")
    String email;
}
