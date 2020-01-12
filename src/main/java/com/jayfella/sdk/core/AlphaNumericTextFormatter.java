package com.jayfella.sdk.core;

import javafx.scene.control.TextFormatter;

/**
 * Enforces AlphaNumeric input only.
 */
public class AlphaNumericTextFormatter extends TextFormatter<String> {
    public AlphaNumericTextFormatter() {
        super(new AlphaNumericStringConverter());
    }
}
