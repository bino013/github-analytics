package com.sample.project.analytics.service.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author arvin on 8/15/20
 **/
@Value
@AllArgsConstructor
public class RepositoryCommitData {

    @JsonProperty("committers_impact")
    Map<String, Integer> committersImpact = new TreeMap<>(new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        }
    });

    @JsonProperty("commit_timelines")
    Set<CommitTimeline> commitTimelines = new TreeSet<>(new Comparator<CommitTimeline>() {
        @Override
        public int compare(CommitTimeline o1, CommitTimeline o2) {
            return o2.getTimestamp().compareTo(o1.getTimestamp());
        }
    });

    public void addCommit(final String committer, final String message, final Instant timestamp, final String sha) {
        committersImpact.compute(committer, (key, value) -> Objects.isNull(value) ? 1 : value + 1);
        commitTimelines.add(new CommitTimeline(timestamp, message, sha));
    }

    public int size() {
        return committersImpact.values().stream().reduce(Integer::sum).get();
    }


}
