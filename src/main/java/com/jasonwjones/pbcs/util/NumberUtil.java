package com.jasonwjones.pbcs.util;

public class NumberUtil {

    private NumberUtil() {}

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

}