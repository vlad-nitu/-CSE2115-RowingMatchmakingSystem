package nl.tudelft.cse.sem.template.user.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputValidationTest {

    @Test
    void userIdValidation() {
        String userIdSpecialCharacter = "abc^%";
        String userIdShort = "ab";
        String userIdLong = "0123456789abcdefghijk";
        String userIdNull = null;
        String userIdEmpty = "    ";
        String userIdValid = "arobben";
        assertFalse(InputValidation.userIdValidation(userIdSpecialCharacter));
        assertFalse(InputValidation.userIdValidation(userIdShort));
        assertFalse(InputValidation.userIdValidation(userIdLong));
        assertFalse(InputValidation.userIdValidation(userIdNull));
        assertFalse(InputValidation.userIdValidation(userIdEmpty));
        assertTrue(InputValidation.userIdValidation(userIdValid));
    }
}