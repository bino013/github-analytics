package com.sample.project.analytics.service;

import com.sample.project.analytics.service.data.RepositoryCommitData;
import com.sample.project.analytics.service.data.RepositorySearchData;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author arvin on 8/15/20
 **/
@Service
public class GithubApiService implements ApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GithubApiService.class);

    private final String endpoint;

    private final RestTemplate restTemplate;

    protected GithubApiService(final RestTemplate restTemplate, @Value("${github-api-service.endpoint}") final String endpoint) {
        this.endpoint = endpoint;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<RepositorySearchData> searchRepository(String query, int size) {
        final String endpointWithParam = endpoint + "/search/repositories?q=" + query + "&per_page=" + size;
        try {
            LOGGER.info("Sending repository search request...");
            LOGGER.debug("Endpoint: {}", endpointWithParam);
            final ResponseEntity<String> response = restTemplate.getForEntity(endpointWithParam, String.class);
            if (!HttpStatus.OK.equals(response.getStatusCode())) {
                throw new ServiceException("Error in search response");
            }
            final List<RepositorySearchData> dataList = processSearchResponse(response);
            LOGGER.debug("Data size: {}", dataList.size());
            return dataList;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error while sending search request", e);
        }
    }

    private List<RepositorySearchData> processSearchResponse(ResponseEntity<String> response) throws ParseException {
        final String responseBody = response.getBody();
        assert responseBody != null;
        final JSONParser jsonParser = new JSONParser(responseBody);
        final LinkedHashMap<Object, Object> result = (LinkedHashMap<Object, Object>) jsonParser.parse();
        final ArrayList<Object> items = (ArrayList<Object>) result.get("items");
        final List<RepositorySearchData> dataList = new ArrayList<>();
        for (Object item : items) {
            final LinkedHashMap itemMap = (LinkedHashMap) item;
            final String name = (String) itemMap.get("name");
            final String owner = (String) ((LinkedHashMap) itemMap.get("owner")).get("login");
            dataList.add(new RepositorySearchData(name, owner));
        }
        return dataList;
    }

    @Override
    public RepositoryCommitData getCommitData(String owner, String name, int size) {
        final String endpointWithParam = endpoint + "/repos/"+owner+"/"+name+"/commits?per_page=" + size;
        try {
            LOGGER.info("Sending commit list request...");
            LOGGER.debug("Endpoint: {}", endpointWithParam);
            final ResponseEntity<String> response = restTemplate.getForEntity(endpointWithParam, String.class);
            if (!HttpStatus.OK.equals(response.getStatusCode())) {
                throw new ServiceException("Error in commit response");
            }
            final RepositoryCommitData commitData = processCommitDataResponse(response);
            LOGGER.debug("Data size: {}", commitData.size());
            return commitData;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error while sending commit request", e);
        }
    }

    private static RepositoryCommitData processCommitDataResponse(final ResponseEntity<String> response) throws ParseException {
        final String responseBody = response.getBody();
        assert responseBody != null;
        final JSONParser jsonParser = new JSONParser(responseBody);
        final ArrayList<Object> responseData = (ArrayList<Object>) jsonParser.parse();
        final RepositoryCommitData commitData = new RepositoryCommitData();
        for(Object entry: responseData) {
            final LinkedHashMap<Object, Object> entryMap = (LinkedHashMap<Object, Object>) entry;
            final String sha = (String) entryMap.get("sha");
            final LinkedHashMap commit = (LinkedHashMap) entryMap.get("commit");
            final LinkedHashMap author = (LinkedHashMap) commit.get("author");
            final String committer = (String) author.get("name");
            final Instant timestamp = Instant.parse((String) author.get("date"));
            final String message = (String) commit.get("message");
            commitData.addCommit(committer, message, timestamp, sha);
        }
        return commitData;
    }

    @Override
    public List<String> getAllContributors(String owner, String name, int size) {
        final String endpointWithParam = endpoint + "/repos/"+owner+"/"+name+"/contributors?per_page=" + size;
        try {
            LOGGER.info("Sending contributors list request...");
            LOGGER.debug("Endpoint: {}", endpointWithParam);
            final ResponseEntity<String> response = restTemplate.getForEntity(endpointWithParam, String.class);
            if (!HttpStatus.OK.equals(response.getStatusCode())) {
                throw new ServiceException("Error in contributors response");
            }
            final List<String> dataList = processContributorResponse(response);
            LOGGER.debug("Data size: {}", dataList.size());
            return dataList;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error while contributors search request", e);
        }
    }

    private static List<String> processContributorResponse(final ResponseEntity<String> response) throws ParseException {
        final String responseBody = response.getBody();
        assert responseBody != null;
        final JSONParser jsonParser = new JSONParser(responseBody);
        final ArrayList<Object> responseData = (ArrayList<Object>) jsonParser.parse();
        final List<String> dataList = new ArrayList<>();
        for(Object entry: responseData) {
            final LinkedHashMap<Object, Object> entryMap = (LinkedHashMap<Object, Object>) entry;
            final String login = (String) entryMap.get("login");
            dataList.add(login);
        }
        return dataList;
    }
}
