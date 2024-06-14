package com.eguglielmelli.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

public class WeightConversionTest {

    WeightConversion weightConverter;

    @BeforeEach
    public void setUp() {
        weightConverter = new WeightConversion();
    }
    @Test
    public void poundsToKilosTest_Normal_Success() {
      //  normal case, making sure our rounding to one decimal place is correct
        BigDecimal pounds = new BigDecimal("150.5");
        BigDecimal conversionToKilos = weightConverter.poundsToKilos(pounds);
        assertEquals(new BigDecimal("68.3"),conversionToKilos);
    }
    @Test
    public void poundsToKilosTest_nullPounds_mustThrowException() {
        //null case, method must throw an exception
        BigDecimal nullPounds  = null;
        IllegalArgumentException argException = assertThrows(IllegalArgumentException.class, () -> {
            weightConverter.poundsToKilos(nullPounds);
        });
        assertEquals("Input pounds must be a non-negative number.",argException.getMessage());

    }
    @Test
    public void poundsToKilosTest_zeroPounds_shouldRoundCorrectly() {
        //case where pounds == 0
        BigDecimal zeroPounds = BigDecimal.ZERO;
        BigDecimal zeroKilosResult = BigDecimal.ZERO.setScale(1);
        assertEquals(zeroKilosResult,weightConverter.poundsToKilos(zeroPounds));
    }

    @Test
    public void poundsToKiloTest_multipleDecimalPlaces_shouldRoundCorrectly() {
        //another normal case, however input number has multiple decimal places (unlikely since
        // database will also maintain one decimal place)
        BigDecimal poundsMultipleDecimal = new BigDecimal("135.354");
        BigDecimal kilosConvertedToOneDecimal = weightConverter.poundsToKilos(poundsMultipleDecimal);
        assertEquals(new BigDecimal("61.4"),kilosConvertedToOneDecimal);
    }
    @Test
    public void poundsToKilosTest_negativeInput_shouldThrowException() {
        //final case where input is < 0, method must throw exception
        BigDecimal negativePounds = new BigDecimal("-1.0");
        IllegalArgumentException argumentException = assertThrows(IllegalArgumentException.class, () -> {
            weightConverter.poundsToKilos(negativePounds);
        });
        assertEquals("Input pounds must be a non-negative number.",argumentException.getMessage());
    }
    @Test
    public void kilosToPoundsTest_Normal_Success() {
        //normal case, input kilos with one decimal place
        BigDecimal kilos = new BigDecimal("120.9");
        WeightConversion weightConverter = new WeightConversion();
        BigDecimal conversionFromKilos = weightConverter.kilosToPounds(kilos);
        assertEquals(new BigDecimal("266.5"), conversionFromKilos);
    }

    @Test
    public void kilosToPoundsTest_nullInput_shouldThrowException() {
        //null case, method should throw an exception
        BigDecimal nullKilos = null;
        IllegalArgumentException argExceptionFromNull = assertThrows(IllegalArgumentException.class, () -> {
            weightConverter.kilosToPounds(nullKilos);
        });
        assertEquals("Input kilograms must be a non-negative number.",argExceptionFromNull.getMessage());

    }

    @Test
    public void kilosToPoundsTest_zeroKilos_shouldRoundCorrectly() {
        //case where input kilos == 0
        BigDecimal zeroKilos = BigDecimal.ZERO;
        BigDecimal zeroPounds = BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP);
        assertEquals(zeroPounds,weightConverter.kilosToPounds(zeroKilos));
    }

    @Test
    public void kilosToPoundsTest_multipleDecimalPlaces_shouldRoundCorrectly() {
        //normal case with extra decimal places, need to ensure it stays as 1 decimal place
        BigDecimal multipleDecimalKilos = new BigDecimal("172.354554");
        BigDecimal oneDecimalPounds = weightConverter.kilosToPounds(multipleDecimalKilos);
        assertEquals(new BigDecimal("380.0"),oneDecimalPounds);
    }

    @Test
    public void kilosToPoundsTest_negativeKilos_shouldThrowException() {
        //case where input kilos is < 0
        BigDecimal negativeKilos = new BigDecimal("-1.5");
        IllegalArgumentException negativeException = assertThrows(IllegalArgumentException.class, () -> {
            weightConverter.kilosToPounds(negativeKilos);
        });
        assertEquals("Input kilograms must be a non-negative number.",negativeException.getMessage());
    }
}
