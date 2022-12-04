package com.example.micro.services;

import com.example.micro.domain.Matching;
import com.example.micro.repositories.MatchingRepository;
import java.util.List;
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

}
