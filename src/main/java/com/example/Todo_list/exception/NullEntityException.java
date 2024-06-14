package com.example.Todo_list.exception;

public class NullEntityException extends RuntimeException {

    public NullEntityException() {
        super("A null entity was found");
    }

    public NullEntityException(String message) {
        super(message);
    }

    public NullEntityException(String entityType, String message) {
        super(String.format("%s: %s", entityType, message));
    }
}
