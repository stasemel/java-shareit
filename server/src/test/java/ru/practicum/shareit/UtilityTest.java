package ru.practicum.shareit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilityTest {
    @Test
    void testStatic() {
        assertEquals("X-Sharer-User-Id", Utility.HEADER_USER);
    }
}