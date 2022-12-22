package com.example.notificationmicroservice.utils;

import lombok.AllArgsConstructor;

import java.util.Set;

@AllArgsConstructor
public class InputValidation {

    private static Set<String> validPositions = Set.of("cox",
            "coach",
            "port side rower",
            "starboard side rower",
            "sculling rower");

    private static Set<String> validTypes = Set.of("notifyUser",
            "notifyOwner");

    /**
     * Checks whether position is valid.
     *
     * @param position the position to be checked
     * @return true, if valid
     */
    public static boolean validatePosition(String position) {
        return validPositions.contains(position);
    }

    /**
     * Checks whether the type is valid.
     *
     * @param type - string to be validated
     * @return true, if valid
     */
    public static boolean validateType(String type) {
        return validTypes.contains(type);
    }


}
