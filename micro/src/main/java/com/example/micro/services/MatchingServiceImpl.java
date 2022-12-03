package com.example.micro.services;

import com.example.micro.domain.Matching;
import com.example.micro.repositories.MatchingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchingServiceImpl {

    private final MatchingRepository matchingRepository;

    public MatchingServiceImpl(MatchingRepository matchingRepository) {
        this.matchingRepository = matchingRepository;
    }

    public List<Matching> findAll() {
        return matchingRepository.findAll();
    }

    public Matching save (Matching matching){
        return matchingRepository.save(matching);
    }

}
