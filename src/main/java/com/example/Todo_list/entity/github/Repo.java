package com.example.Todo_list.entity.github;

import lombok.Data;

@Data
public class Repo {
    private String name;
    private Owner owner;

    @Data
    public static class Owner {
        private String login;
    }
}
