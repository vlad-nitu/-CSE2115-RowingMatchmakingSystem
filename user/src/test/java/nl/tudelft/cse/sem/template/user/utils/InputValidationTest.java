package nl.tudelft.cse.sem.template.user.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputValidationTest {

    @Test
    void userIdValidationTest() {
        String userIdSpecialCharacter = "abc^%";
        assertFalse(InputValidation.userIdValidation(userIdSpecialCharacter));
        String userIdValid = "arobben";
        assertTrue(InputValidation.userIdValidation(userIdValid));
    }

    @Test
    void userGenderValidation() {
        char genderValidM = 'M';
        assertTrue(InputValidation.userGenderValidation(genderValidM));
        char genderValidf = 'f';
        assertTrue(InputValidation.userGenderValidation(genderValidf));
        char genderInvalid = 'N';
        assertFalse(InputValidation.userGenderValidation(genderInvalid));
    }
}