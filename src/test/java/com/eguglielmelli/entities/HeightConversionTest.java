package com.eguglielmelli.entities;

import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class HeightConversionTest {

    private HeightConversion heightConverter;

    @BeforeEach
    void setUp() {
        this.heightConverter = new HeightConversion();
    }

    @Test
    public void centimetersToInchesTest_Normal_Success() {
        //normal case where we don't have to worry about making changes
        //to decimal places etc, should be successful
        BigDecimal heightInCentimeters = new BigDecimal("90.0");

        BigDecimal heightInInches = heightConverter.centimetersToInches(heightInCentimeters);

        assertEquals(BigDecimal.valueOf(35.4), heightInInches);

    }

    @Test
    public void centimetersToInchesTest_nullCm_shouldThrowException() {
        //null case, this should be handled in the method by throwing
        //an IllegalArgumentException

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            heightConverter.centimetersToInches(null);
        });

        assertEquals("Centimeters cannot be null", exception.getMessage());
    }

    @Test
    public void inchesToCentimetersTest_Normal_Success() {
        //normal case, expecting a successful conversion
        BigDecimal heightInInches = new BigDecimal(150.0);

        BigDecimal heightInCentimeters = heightConverter.inchesToCentimeters(heightInInches);

        assertEquals(BigDecimal.valueOf(381.0), heightInCentimeters);
    }

    @Test
    public void inchesToCentimetersTest_nullInches_shouldThrowException() {
        //null case, should throw an IllegalArgumentException

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
           heightConverter.inchesToCentimeters(null);
        });

        assertEquals("Inches cannot be null", exception.getMessage());

    }

    @Test
    public void inchesToFeetAndInchesTest_Normal_Success() {
        //normal case, we expect it will easily convert to a string
        //in our defined format
        BigDecimal inches = new BigDecimal("73.0");
        String inchesAndFeet = HeightConversion.inchesToFeetAndInches(inches);

        assertEquals("6 ft. 1 in.", inchesAndFeet);
    }

    @Test
    public void inchesToFeetAndInchesTest_nullInches_shouldThrowException() {
        //should throw exception for null input
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            HeightConversion.inchesToFeetAndInches(null);
        });

        assertEquals("Inches cannot be null", exception.getMessage());
    }
}
