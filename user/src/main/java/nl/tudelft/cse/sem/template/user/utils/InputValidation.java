package nl.tudelft.cse.sem.template.user.utils;

import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@AllArgsConstructor
public class InputValidation {

    private static final Pattern patternNoSpecialCharacters = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
    private static Set<String> validPositions = Set.of("cox",
            "coach",
            "port side rower",
            "starboard side rower",
            "sculling rower");


    /**
     * Validates the userId by checking if it contains special characters.
     *
     * @param userId - the ID of the user to be validated
     * @return boolean defining whether the input passed the validation
     */
    public static boolean userIdValidation(String userId) {

        Matcher matcher = patternNoSpecialCharacters.matcher(userId);

        return !matcher.find();

    }

    /**
     * Checks whether the all the elements of the set of positions provided as input are valid.
     *
     * @param positions - Set of Strings representing the positions
     * @return true, if all positions are valid, or false otherwise.
     */
    public static boolean validatePositions(Set<String> positions) {
        for (String position : positions) {
            if (!validPositions.contains(position)) {
                return false;
            }
        }

        return true;
    }


}
