package com.sample.project.analytics.service;

import com.sample.project.analytics.service.data.RepositoryCommitData;
import com.sample.project.analytics.service.data.RepositorySearchData;

import java.util.List;

/**
 * @author arvin on 8/15/20
 **/
public interface ApiService {

    List<RepositorySearchData> searchRepository(String query, int size);

    RepositoryCommitData getCommitData(String owner, String name, int size);

    List<String> getAllContributors(String owner, String name, int size);

}
