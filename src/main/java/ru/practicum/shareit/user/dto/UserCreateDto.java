package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserCreateDto {
    @NotBlank(message = "Имя должно быть указано")
    private String name;
    @NotBlank(message = "Email должен быть указан")
    @Email(message = "Email должен быть указан в правильном формате")
    private String email;


}
