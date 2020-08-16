package com.sample.project.analytics.controller;

import com.sample.project.analytics.service.ApiService;
import com.sample.project.analytics.service.data.RepositoryCommitData;
import com.sample.project.analytics.service.data.RepositorySearchData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author arvin on 8/15/20
 **/
@RestController
public class AnalyticsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsController.class);

    private final ApiService apiService;

    protected AnalyticsController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping(path = "/analytics/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> search(@RequestParam(name = "query") final String query,
                                                             @RequestParam(name = "size", defaultValue = "100") final int size) {
        LOGGER.info("Search repository request. Query: {}", query);
        final List<RepositorySearchData> data = apiService.searchRepository(query, size);
        LOGGER.info("Search repository response: {}", data);
        return new ResponseEntity<>(new Response("00", data), HttpStatus.OK);
    }

    @GetMapping(path = "/analytics/{owner}/{repo}/commits", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> commits(@PathVariable(required = true) String owner,
                                                        @PathVariable(required = true) String repo,
                                                        @RequestParam(name = "size", defaultValue = "100") final int size) throws ExecutionException, InterruptedException {
        LOGGER.info("Get commit data request. Owner: {}. Repo: {}", owner, repo);
        final ExecutorService service = Executors.newFixedThreadPool(2);
        final Future<RepositoryCommitData> dataFuture1 = service.submit(() -> apiService.getCommitData(owner, repo, size));
        final Future<List<String>> dataFuture2 = service.submit(() -> apiService.getAllContributors(owner, repo, size));

        final Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("commit_data", dataFuture1.get());
        responseMap.put("committers", dataFuture2.get());
        return new ResponseEntity<>(new Response("00", responseMap), HttpStatus.OK);
    }
}
