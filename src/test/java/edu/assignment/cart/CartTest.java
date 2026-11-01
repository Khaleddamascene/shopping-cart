package edu.assignment.cart;

import edu.assignment.cart.model.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class CartTest {
    private CartItem item(String price, int quantity) { return new CartItem(new BigDecimal(price), quantity); }
    @Test void itemCost() { assertEquals(new BigDecimal("25.00"), item("12.50", 2).total()); }
    @Test void exactDecimalMath() { assertEquals(new BigDecimal("0.30"), item("0.10", 3).total()); }
    @Test void multipleItems() { assertEquals(new BigDecimal("44.97"), CartCalculator.total(List.of(item("12.50",2), item("3.99",3),item("8.00",1)))); }
    @Test void emptyCart() { assertEquals(BigDecimal.ZERO, CartCalculator.total(List.of())); }
    @Test void zeroPriceAndQuantity() { assertEquals(0,item("0",5).total().signum()); assertEquals(0,item("8.00",0).total().signum()); }
    @Test void invalidPrice() { assertThrows(IllegalArgumentException.class,()->item("-1",1)); assertThrows(IllegalArgumentException.class,()->item("1.234",1)); }
    @Test void invalidQuantity() { assertThrows(IllegalArgumentException.class,()->item("1",-1)); }
    @Test void nullInputs() { assertThrows(NullPointerException.class,()->new CartItem(null,1)); assertThrows(NullPointerException.class,()->CartCalculator.total(null)); }
    @Test void trailingZerosAllowed() { assertEquals(0,new BigDecimal("2.40").compareTo(item("1.200",2).total())); }
    @Test void largeValues() { assertEquals(new BigDecimal("999999999999999999.00"),item("999999999999999999.00",1).total()); }
    @Test void localizedInput() {
        assertEquals(new BigDecimal("12.50"),Numbers.parse("12.50",Locale.US));
        assertEquals(new BigDecimal("12.50"),Numbers.parse("12,50",Locale.forLanguageTag("fi-FI")));
        assertEquals(new BigDecimal("12.50"),Numbers.parse("12,50",Locale.forLanguageTag("sv-SE")));
        assertEquals(new BigDecimal("12.50"),Numbers.parse("12.50",Locale.JAPAN));
        assertEquals(new BigDecimal("12.50"),Numbers.parse("١٢٫٥٠",Locale.forLanguageTag("ar-AR")));
    }
    @Test void rejectPartialAndMalformedInput() {
        for (String value : List.of("", " ", "12abc", "1,200", "NaN", "∞", "1.2.3"))
            assertThrows(IllegalArgumentException.class,()->Numbers.parse(value,Locale.US),value);
        assertThrows(ArithmeticException.class,()->Numbers.parse("2.5",Locale.US).intValueExact());
        assertThrows(ArithmeticException.class,()->Numbers.parse("2147483648",Locale.US).intValueExact());
    }
    @Test void bundlesHaveCompleteKeys() {
        Set<String> expected = ResourceBundle.getBundle("MessagesBundle",Locale.US).keySet();
        for (String code : Controller.CODES) {
            ResourceBundle bundle=ResourceBundle.getBundle("MessagesBundle",Locale.forLanguageTag(code.replace('_','-')));
            assertEquals(code,bundle.getLocale().toString());
            assertEquals(expected,bundle.keySet());
            for (String key : expected) assertFalse(bundle.getString(key).isBlank());
        }
    }
}
