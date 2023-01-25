package nl.tudelft.cse.sem.template.user.utils;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InputValidationTest {

    @Test
    void userValidation() {
        char genderValidM = 'M';
        Set<String> validPositions = Set.of("cox",
                "coach",
                "port side rower",
                "starboard side rower",
                "sculling rower");
        assertNull(InputValidation.validate(validPositions, genderValidM));

        Set<Character> validGenders = Set.of('M', 'm', 'F', 'f');
        for (Character gender : validGenders) {
            assertNull(InputValidation.validate(Set.of("coach"),  gender));
        }

        char genderInvalid = 'N';
        assertEquals(InputValidation.validate(validPositions, genderInvalid).getSecond(), "The provided gender is invalid!");
        Set<String> invalidPositions = Set.of("invalid");
        assertEquals(InputValidation.validate(invalidPositions, genderValidM).getSecond(),
                "One of the positions that you provided is not valid!");
    }

}