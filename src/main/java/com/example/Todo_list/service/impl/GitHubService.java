package com.example.Todo_list.service.impl;

import com.example.Todo_list.entity.github.Issue;
import com.example.Todo_list.entity.github.PullRequest;
import com.example.Todo_list.entity.github.Repo;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;
import java.util.List;

@Service
public class GitHubService {

    private final RestTemplate restTemplate;
    private final String gitHubApiUrl = "https://api.github.com";

    public GitHubService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<Repo> getUserRepos(OAuth2AuthorizedClient authorizedClient) {
        String url = gitHubApiUrl + "/user/repos";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Repo>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Repo>>() {}
        );

        return response.getBody();
    }

    public List<Issue> getRepoIssues(OAuth2AuthorizedClient authorizedClient, String owner, String repo) {
        String url = gitHubApiUrl + "/repos/" + owner + "/" + repo + "/issues";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Issue>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Issue>>() {}
        );

        return response.getBody();
    }

    public List<PullRequest> getRepoPullRequests(OAuth2AuthorizedClient authorizedClient, String owner, String repo) {
        String url = gitHubApiUrl + "/repos/" + owner + "/" + repo + "/pulls";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<PullRequest>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<PullRequest>>() {}
        );

        return response.getBody();
    }
}
