package com.example.activitymicroservice.utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InputValidationTest {

    @Test
    void validatePositionsTrue() {
        assertTrue(InputValidation.validatePositions(new ArrayList<>(List.of("coach", "cox"))));
    }

    @Test
    void validatePositionsFalse1() {
        assertFalse(InputValidation.validatePositions(new ArrayList<>(List.of("coach", "dummy"))));
    }

    @Test
    void validatePositionsFalse2() {
        assertFalse(InputValidation.validatePositions(new ArrayList<>(List.of("dummy", "cox"))));
    }


    @Test
    void validatePosition() {
    }
}