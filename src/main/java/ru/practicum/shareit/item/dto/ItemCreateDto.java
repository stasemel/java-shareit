package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class ItemCreateDto {
    @NotBlank(message = "Наименование должно быть указано")
    String name;
    @NotBlank(message = "Описание должно быть указано")
    String description;
    @NotNull(message = "Нужно указать, свободна или нет эта вещь")
    Boolean available;
}
