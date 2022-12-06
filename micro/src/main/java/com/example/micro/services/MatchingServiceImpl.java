package com.example.micro.services;

import com.example.micro.domain.Matching;
import com.example.micro.publishers.ActivityPublisher;
import com.example.micro.repositories.MatchingRepository;
import com.example.micro.utils.CompositeKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class MatchingServiceImpl {

    private final transient MatchingRepository matchingRepository;

    public MatchingServiceImpl(MatchingRepository matchingRepository) {
        this.matchingRepository = matchingRepository;
    }

    public List<Matching> findAll() {
        return matchingRepository.findAll();
    }

    public Matching save(Matching matching) {
        return matchingRepository.save(matching);
    }

    /**
     * Finds the activities that the user has been enrolled to.
     *
     * @param userId the id of the user
     * @return the list of activities
     */
    public List<Long> findActivitiesByUserId(String userId) {
        return matchingRepository.findMatchingsByUserId(userId)
                .orElse(new ArrayList<Matching>())
                .stream()
                .map(Matching::getActivityId)
                .collect(Collectors.toList());
    }

    public void deleteById(String userId, Long activityId, String position) {
        matchingRepository.deleteById(new CompositeKey(userId, activityId, position));
    }

    public Optional<Matching> findMatchingWithPendingTrue(String userId, Long activityId) {
        return matchingRepository.findMatchingByUserIdAndActivityIdAndPending(userId, activityId, true);
    }

    public Boolean checkId(String userId, long activityId, String position) {
        return matchingRepository.findById(new CompositeKey(userId, activityId, position)).isPresent();
    }

    /**
     * Returns the pending status of a matching.
     *
     * @param userId the id of the user
     * @param activityId the id of the activity
     * @param position the position of the match
     * @return the pending status of a match or false if the match does not exist
     */
    public Boolean findPending(String userId, long activityId, String position) {
        Optional<Matching> matching = matchingRepository.findById(new CompositeKey(userId, activityId, position));
        return matching.map(Matching::isPending).orElse(false);
    }
}
