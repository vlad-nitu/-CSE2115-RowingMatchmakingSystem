package com.example.activitymicroservice.utils;

import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static boolean validatePositions(Set<String> positions) {
        for (String position : positions) {
            if (!validPositions.contains(position)) {
                return false;
            }
        }

        return true;
    }


}
