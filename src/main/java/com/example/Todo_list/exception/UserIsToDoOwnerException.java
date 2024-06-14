package com.example.Todo_list.exception;

public class UserIsToDoOwnerException extends RuntimeException {
    public UserIsToDoOwnerException() {
        super("User is the owner of the ToDo and cannot be added as as a collaborator");
    }

    public UserIsToDoOwnerException(Long toDoId, Long userId) {
        super("User with userId=" + userId +
                " is the owner of the ToDo with toDoId=" + toDoId + " and cannot be added as a collaborator");
    }
}
