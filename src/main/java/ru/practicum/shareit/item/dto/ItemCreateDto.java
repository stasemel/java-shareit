package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemCreateDto {
    @NotBlank(message = "Наименование должно быть указано")
    private String name;
    @NotBlank(message = "Описание должно быть указано")
    private String description;
    @NotNull(message = "Нужно указать, свободна или нет эта вещь")
    private Boolean available;
}
