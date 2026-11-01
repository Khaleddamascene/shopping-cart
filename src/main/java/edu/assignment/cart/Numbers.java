package edu.assignment.cart;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Currency;
import java.util.Locale;

public final class Numbers {

    private Numbers() {
    }

    private static Locale numberLocale(Locale locale) {
        if ("ar".equals(locale.getLanguage())) {
            return new Locale.Builder()
                    .setLocale(locale)
                    .setUnicodeLocaleKeyword("nu", "arab")
                    .build();
        }
        return locale;
    }

    public static BigDecimal parse(String input, Locale locale) {
        String value = input.strip();

        DecimalFormat format = (DecimalFormat)
                NumberFormat.getNumberInstance(numberLocale(locale));

        format.setParseBigDecimal(true);
        format.setGroupingUsed(false);

        ParsePosition position = new ParsePosition(0);
        Number number = format.parse(value, position);

        if (!(number instanceof BigDecimal)
                || position.getIndex() != value.length()) {
            throw new IllegalArgumentException("Invalid number");
        }

        return (BigDecimal) number;
    }

    public static String decimal(BigDecimal value, Locale locale) {
        NumberFormat format =
                NumberFormat.getNumberInstance(numberLocale(locale));

        format.setGroupingUsed(false);
        format.setMaximumFractionDigits(340);

        return format.format(value);
    }

    public static String money(BigDecimal value, Locale locale) {
        NumberFormat format =
                NumberFormat.getCurrencyInstance(numberLocale(locale));

        format.setCurrency(Currency.getInstance("EUR"));
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);

        return format.format(value);
    }
}