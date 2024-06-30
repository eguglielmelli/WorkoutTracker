package com.eguglielmelli.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Similar to height conversion, this class will convert imperial to metric or
 * vice versa
 */
public class WeightConversion {
    public static final BigDecimal POUNDS_TO_KG = new BigDecimal("0.45359237");
    public static final BigDecimal KG_TO_POUNDS = new BigDecimal("2.20462262");

    public BigDecimal poundsToKilos(BigDecimal pounds) {
        if(pounds == null || pounds.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Input pounds must be a non-negative number.");
        }
        return pounds.multiply(POUNDS_TO_KG).setScale(1, RoundingMode.HALF_UP);
    }

    public BigDecimal kilosToPounds(BigDecimal kilos) {
        if(kilos == null || kilos.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Input kilograms must be a non-negative number.");
        }
        return kilos.multiply(KG_TO_POUNDS).setScale(1,RoundingMode.HALF_UP);
    }

}
