package com.jasonwjones.pbcs.util;

/**
 * Small numeric parsing helpers.
 */
public class NumberUtil {

    private NumberUtil() {}

    /**
     * Determines whether the given string can be parsed as a number.
     *
     * @param str the string to check
     * @return true if the string is numeric, false otherwise
     */
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

}
