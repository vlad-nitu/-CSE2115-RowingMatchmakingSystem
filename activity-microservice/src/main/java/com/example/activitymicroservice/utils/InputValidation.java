package com.example.activitymicroservice.utils;

import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.Set;

@AllArgsConstructor
public class InputValidation {

    private static Set<String> validPositions = Set.of("cox",
            "coach",
            "port side rower",
            "starboard side rower",
            "sculling rower");


    /**
     * Checks whether the all the elements of the set of positions provided as input are valid.
     *
     * @param positions - Set of Strings representing the positions
     * @return true, if all positions are valid, or false otherwise.
     */
    public static boolean validatePositions(Collection<String> positions) {
        for (String position : positions) {
            if (!validPositions.contains(position)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks whether the all the elements of the set of positions provided as input are valid.
     *
     * @param position the positions
     * @return true, if all positions are valid, or false otherwise.
     */
    public static boolean validatePosition(String position) {
        return validPositions.contains(position);
    }


}
