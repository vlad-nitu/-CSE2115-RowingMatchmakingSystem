package com.example.micro.repositories;

import com.example.micro.domain.Matching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchingRepository extends JpaRepository<Matching, String> {
    Optional<List<Matching>> findMatchingByUserId(String userId);
}
