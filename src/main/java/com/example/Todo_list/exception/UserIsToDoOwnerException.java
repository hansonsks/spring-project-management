package com.example.Todo_list.exception;

/**
 * Exception thrown when a user is the owner of a ToDo and cannot be added as a collaborator
 */
public class UserIsToDoOwnerException extends RuntimeException {

    /**
     * Constructs a new exception with a default message
     */
    public UserIsToDoOwnerException() {
        super("User is the owner of the ToDo and cannot be added as as a collaborator");
    }

    /**
     * Constructs a new exception with a custom message
     *
     * @param toDoId the id of the ToDo
     * @param userId the id of the user
     */
    public UserIsToDoOwnerException(Long toDoId, Long userId) {
        super("User with userId=" + userId +
                " is the owner of the ToDo with toDoId=" + toDoId + " and cannot be added as a collaborator");
    }
}
