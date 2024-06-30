package com.eguglielmelli.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Since we are storing all details using imperial system, this class will be used for
 * people who use the metric system
 */
public class HeightConversion {

    private final BigDecimal CM_TO_INCHES = new BigDecimal("0.393701");
    private final BigDecimal INCHES_TO_CM = new BigDecimal("2.54");

    public BigDecimal centimetersToInches(BigDecimal cm) {
        if(cm == null) {
            throw new IllegalArgumentException("Centimeters cannot be null");
        }
        return cm.multiply(CM_TO_INCHES).setScale(1,RoundingMode.HALF_UP);
    }

    public BigDecimal inchesToCentimeters(BigDecimal inches) {
        if(inches == null) {
            throw new IllegalArgumentException("Inches cannot be null");
        }

        return inches.multiply(INCHES_TO_CM).setScale(1,RoundingMode.HALF_UP);
    }

    /**
     * This will print what is to be displayed for users who are
     * using the imperial system
     * @param inches the given user height
     * @return a well formatted string to show the user's height
     */
    public static String inchesToFeetAndInches(BigDecimal inches) {
        if(inches == null) {
            throw new IllegalArgumentException("Inches cannot be null");
        }

        int totalInchesInt = inches.setScale(0, RoundingMode.HALF_UP).intValue();
        int feet = totalInchesInt / 12;
        int remainingInches = totalInchesInt % 12;

        return feet + " ft. " + remainingInches + " in.";
    }
}
