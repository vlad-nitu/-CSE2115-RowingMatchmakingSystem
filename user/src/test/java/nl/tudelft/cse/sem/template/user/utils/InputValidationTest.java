package nl.tudelft.cse.sem.template.user.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputValidationTest {

    @Test
    void userIdValidationTest() {
        String userIdSpecialCharacter = "abc^%";
        assertFalse(InputValidation.userIdValidation(userIdSpecialCharacter));
        //        String userIdShort = "ab";
        //        assertFalse(InputValidation.userIdValidation(userIdShort));
        //        String userIdLong = "0123456789abcdefghijk";
        //        assertFalse(InputValidation.userIdValidation(userIdLong));
        //        String userIdNull = null;
        //        assertFalse(InputValidation.userIdValidation(userIdNull));
        //        String userIdEmpty = "    ";
        //        assertFalse(InputValidation.userIdValidation(userIdEmpty));
        String userIdValid = "arobben";
        assertTrue(InputValidation.userIdValidation(userIdValid));
    }
}