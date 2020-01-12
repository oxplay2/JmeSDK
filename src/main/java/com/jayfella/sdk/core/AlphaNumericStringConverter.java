package com.jayfella.sdk.core;

import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class AlphaNumericStringConverter implements UnaryOperator<TextFormatter.Change> {
    @Override
    public TextFormatter.Change apply(TextFormatter.Change c) {

        if (c.getControlNewText().isEmpty()) {
            return c;
        }

        if (c.getControlNewText().matches("[A-Za-z0-9]+")) {
            return c;
        }
        return null;
    }
}
