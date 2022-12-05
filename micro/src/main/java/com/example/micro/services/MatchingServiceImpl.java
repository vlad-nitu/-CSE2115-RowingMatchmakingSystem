package com.example.micro.services;

import com.example.micro.domain.Matching;
import com.example.micro.repositories.MatchingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.micro.utils.CompositeKey;
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

    public List<Long> findActivitiesByUserId(String userId) {
        return matchingRepository.findMatchingByUserId(userId)
                .orElse( new ArrayList<Matching>())
                .stream()
                .map(Matching::getActivityId)
                .collect(Collectors.toList());
    }

    public void deleteById(String userId, Long activityId, String position){
        matchingRepository.deleteById(new CompositeKey(userId, activityId, position));
    }


}
