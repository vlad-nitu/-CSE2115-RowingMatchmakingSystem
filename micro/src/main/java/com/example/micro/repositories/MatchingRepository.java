package com.example.micro.repositories;

import com.example.micro.domain.Matching;
import com.example.micro.utils.CompositeKey;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchingRepository extends JpaRepository<Matching, CompositeKey> {
    Optional<List<Matching>> findMatchingsByUserId(String userId);

    Optional<Matching> findMatchingByUserIdAndActivityIdAndPending(String userId, Long activityId, Boolean pending);

    void deleteByActivityId(Long activityId);
}
