package com.example.Todo_list.entity.github;

import lombok.Data;

/**
 * Represents a pull request from the GitHub API.

 */
@Data
public class PullRequest {

    /**
     * The title of the pull request.
     */
    private String title;

    /**
     * The body of the pull request.
     */
    private String html_url;
}
