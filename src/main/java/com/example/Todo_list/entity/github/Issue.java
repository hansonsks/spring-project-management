package com.example.Todo_list.entity.github;

import lombok.Data;

/**
 * Represents an issue from the GitHub API.
 */
@Data
public class Issue {

    /**
     * The title of the issue.
     */
    private String title;

    /**
     * The URL of the issue.
     */
    private String html_url;
}
