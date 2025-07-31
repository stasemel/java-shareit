package ru.practicum.shareit.user.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserUpdateDto {
    private Long id;
    private String name;
    @Email(message = "Email должен быть в правильном формате")
    @Nullable
    private String email;
}
