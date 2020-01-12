package com.jayfella.sdk.core;

import javafx.scene.control.TextFormatter;

public class IntegerTextFormatter extends TextFormatter<String> {

    public IntegerTextFormatter() {
        super(new IntegerStringConverter());
    }
}
