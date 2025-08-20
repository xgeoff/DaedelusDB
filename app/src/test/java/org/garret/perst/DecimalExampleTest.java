package org.garret.perst;

import org.junit.Test;
import static org.junit.Assert.*;

public class DecimalExampleTest {
    static final int INT_DIGITS = 5;
    static final int FRAC_DIGITS = 2;

    @Test
    public void decimalOperations() {
        Decimal d1 = new Decimal(12345, INT_DIGITS, FRAC_DIGITS);
        Decimal d2 = new Decimal(12.34, INT_DIGITS, FRAC_DIGITS);
        Decimal d3 = new Decimal("1.23", INT_DIGITS, FRAC_DIGITS);
        Decimal d4 = new Decimal("00001.00");
        Decimal d5 = new Decimal(-12345, INT_DIGITS, FRAC_DIGITS);
        Decimal d6 = new Decimal(-12.34, INT_DIGITS, FRAC_DIGITS);
        Decimal d7 = new Decimal("    -1.23", INT_DIGITS, FRAC_DIGITS);
        Decimal d8 = new Decimal("-00001.00");

        assertEquals(new Decimal(13579, INT_DIGITS, FRAC_DIGITS), d1.add(d2));
        assertEquals(new Decimal(13579, INT_DIGITS, FRAC_DIGITS), d1.sub(d6));
        assertEquals(1, d3.floor());
        assertEquals(-2, d7.floor());
        assertEquals(1, d4.floor());
        assertEquals(-1, d8.floor());
        assertEquals(2, d3.ceil());
        assertEquals(-1, d7.ceil());
        assertEquals(1, d4.ceil());
        assertEquals(-1, d8.ceil());
        assertEquals(1, d3.round());
        assertEquals(-1, d7.round());
        assertEquals(1, d4.round());
        assertEquals(-1, d8.round());
        assertEquals(d5, d1.neg());
        assertEquals(d8, d4.neg());
        assertTrue(d1.compareTo(d2) > 0);
        assertTrue(d3.compareTo(d2) < 0);
        assertTrue(d5.compareTo(d6) < 0);
        assertTrue(d7.compareTo(d6) > 0);
        assertEquals(0, d4.compareTo(d8.abs()));
        assertEquals("   123.45", d1.toString(' '));
        assertEquals("  -123.45", d5.toString(' '));
        assertEquals("123.45", d1.toString());
    }
}
