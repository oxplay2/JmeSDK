package com.jayfella.sdk.core;

import javafx.scene.control.TextFormatter;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.function.UnaryOperator;

/**
 * Enforce float-only values in a JavaFX TextField.
 */
public class FloatStringConverter implements UnaryOperator<TextFormatter.Change> {

    DecimalFormat format = new DecimalFormat("#.0");

    @Override
    public TextFormatter.Change apply(TextFormatter.Change c) {

        if (c.getControlNewText().isEmpty()) {
            return c;
        }

        ParsePosition parsePosition = new ParsePosition(0);
        Object object = format.parse(c.getControlNewText(), parsePosition);

        if (object == null || parsePosition.getIndex() < c.getControlNewText().length()) {
            return null;
        } else {
            return c;
        }

    }
}