package nl.tudelft.cse.sem.template.user.utils;

import lombok.AllArgsConstructor;

import java.util.Set;

@AllArgsConstructor
public class InputValidation {

    private static Set<String> validPositions = Set.of("cox",
            "coach",
            "port side rower",
            //"starboard side rower",
            "sculling rower");

    private static final Set<Character> possibleGenders = Set.of('M', 'm', 'F', 'f');


    /**
     * Checks whether the all the elements of the set of positions provided as input are valid.
     *
     * @param positions - Set of Strings representing the positions
     * @return true, if all positions are valid, or false otherwise.
     */
    public static Pair<Boolean, String> validate(Set<String> positions, Character gender) {
        for (String position : positions) {
            if (!validPositions.contains(position)) {
                return new Pair<>(false, "One of the positions that you provided is not valid!");
            }
        }
        return possibleGenders.contains(gender) ? null : new Pair<>(false, "The provided gender is invalid!");
    }


}
