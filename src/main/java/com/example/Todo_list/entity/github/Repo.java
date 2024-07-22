package com.example.Todo_list.entity.github;

import lombok.Data;

/**
 * Represents a GitHub repository.

 */
@Data
public class Repo {

    /**
     * The name of the repository.
     */
    private String name;

    /**
     * The owner of the repository.
     */
    private Owner owner;

    /**
     * Represents the owner of a GitHub repository.
     */
    @Data
    public static class Owner {

        /**
         * The login of the owner.
         */
        private String login;
    }
}
