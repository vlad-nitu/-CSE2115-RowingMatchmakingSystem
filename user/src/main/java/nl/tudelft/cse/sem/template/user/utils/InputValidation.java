package nl.tudelft.cse.sem.template.user.utils;

import lombok.AllArgsConstructor;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

@AllArgsConstructor
public class InputValidation {

    private static final Pattern patternNoSpecialCharacters = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
    private static final int minLength = 3;
    private static final int maxLength = 20;

    /**
     * Validates the userId by checking if it contains special characters, is null, empty and within the desired length.
     *
     * @param userId - the ID of the user to be validated
     * @return boolean defining whether the input passed the validation
     */
    public static boolean userIdValidation(String userId) {

        if (userId == null) {
            return false;
        }

        Matcher matcher = patternNoSpecialCharacters.matcher(userId);

        return !userId.trim().isEmpty() && userId.length() >= minLength && userId.length() <= maxLength
                && !matcher.find();

    }


}
