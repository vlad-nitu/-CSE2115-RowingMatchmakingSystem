package nl.tudelft.cse.sem.template.user.utils;

import lombok.AllArgsConstructor;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@AllArgsConstructor
public class InputValidation {

    private static final Pattern patternNoSpecialCharacters = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
    private static final Pattern patternMF = Pattern.compile("[^mf]", Pattern.CASE_INSENSITIVE);

    /**
     * Validates the userId by checking if it follows the required format.
     *
     * @param userId - the ID of the user to be validated
     * @return boolean defining whether the input passed the validation
     */
    public static boolean userIdValidation(String userId) {

        Matcher matcher = patternNoSpecialCharacters.matcher(userId);

        return !matcher.find();

    }

    /**
     * Validates the user's gender by checking if it follows the required format.
     *
     * @param gender - the character defining the user's indicated gender
     * @return boolean defining whether the input passed the validation
     */
    public static boolean userGenderValidation(Character gender) {

        Matcher matcher = patternMF.matcher(gender.toString());

        return !matcher.find();

    }


}
