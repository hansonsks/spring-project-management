package com.example.Todo_list.controller;

import com.example.Todo_list.entity.github.Issue;
import com.example.Todo_list.entity.github.PullRequest;
import com.example.Todo_list.entity.github.Repo;
import com.example.Todo_list.service.impl.GitHubService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

// TODO: Add Preauthorization

@Controller
@RequestMapping("/github")
@RequiredArgsConstructor
public class GitHubController {

    private final GitHubService gitHubService;

    @GetMapping("/repos")
    public String listRepos(Model model, HttpServletRequest request) {
        OAuth2AuthorizedClient client = (OAuth2AuthorizedClient) request.getSession().getAttribute("oauth2AuthorizedClient");
        if (client == null) {
            return "redirect:/logout";
        }

        List<Repo> repos = gitHubService.getUserRepos(client);
        model.addAttribute("repos", repos);
        return "repos";
    }

    @GetMapping("/repos/{owner}/{repo}/issues")
    public String listIssues(@PathVariable String owner, @PathVariable String repo, Model model, HttpServletRequest request) {
        OAuth2AuthorizedClient client = (OAuth2AuthorizedClient) request.getSession().getAttribute("oauth2AuthorizedClient");
        if (client == null) {
            return "redirect:/logout";
        }

        List<Issue> issues = gitHubService.getRepoIssues(client, owner, repo);
        model.addAttribute("issues", issues);
        model.addAttribute("repoName", repo);
        return "issues";
    }

    @GetMapping("/repos/{owner}/{repo}/pulls")
    public String listPullRequests(@PathVariable String owner, @PathVariable String repo, Model model, HttpServletRequest request) {
        OAuth2AuthorizedClient client = (OAuth2AuthorizedClient) request.getSession().getAttribute("oauth2AuthorizedClient");
        if (client == null) {
            return "redirect:/logout";
        }

        List<PullRequest> pulls = gitHubService.getRepoPullRequests(client, owner, repo);
        model.addAttribute("pulls", pulls);
        model.addAttribute("repoName", repo);
        return "pull-requests";
    }
}
