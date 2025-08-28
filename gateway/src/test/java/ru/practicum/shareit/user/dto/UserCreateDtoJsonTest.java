package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserCreateDtoJsonTest {
    private final JacksonTester<UserCreateDto> json;

    @Test
    void testNormalDto() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto("Name", "email@yandex.ru");

        JsonContent<UserCreateDto> result = json.write(userCreateDto);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(userCreateDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(userCreateDto.getEmail());
    }


}