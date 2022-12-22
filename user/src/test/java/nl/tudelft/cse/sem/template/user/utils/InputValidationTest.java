package nl.tudelft.cse.sem.template.user.utils;

import org.junit.jupiter.api.Test;

import java.util.Set;

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

    @Test
    void userPositionsValidation() {
        Set<String> validPositions = Set.of("coach");
        assertTrue(InputValidation.validatePositions(validPositions));
        Set<String> invalidPositions = Set.of("invalid");
        assertFalse(InputValidation.validatePositions(invalidPositions));
    }
}