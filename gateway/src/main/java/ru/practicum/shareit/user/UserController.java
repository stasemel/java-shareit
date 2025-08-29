package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserCreateDto userCreateDto) {
        log.info("Create user {}", userCreateDto);
        return userClient.create(userCreateDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable Long userId, @Valid @RequestBody UserUpdateDto userUpdateDto) {
        log.info("Update user id = {}, {}", userId, userUpdateDto);
        return userClient.update(userId, userUpdateDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Get user by id = {}", userId);
        return userClient.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("Delete user id = {}", userId);
        return userClient.deleteUser(userId);
    }

}
