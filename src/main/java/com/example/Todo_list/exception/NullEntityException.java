package com.example.Todo_list.exception;

/**
 * Exception thrown when a null entity is found
 */
public class NullEntityException extends RuntimeException {

    /**
     * Default constructor
     */
    public NullEntityException() {
        super("A null entity was found");
    }

    /**
     * Constructor with message
     * @param message
     */
    public NullEntityException(String message) {
        super(message);
    }

    /**
     * Constructor with entity type and message
     * @param entityType
     * @param message
     */
    public NullEntityException(String entityType, String message) {
        super(String.format("%s: %s", entityType, message));
    }
}
