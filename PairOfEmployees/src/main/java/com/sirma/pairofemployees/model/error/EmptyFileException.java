package com.sirma.pairofemployees.model.error;

public class EmptyFileException extends RuntimeException {
    public EmptyFileException(String msg) {
        super(msg);
    }
}
