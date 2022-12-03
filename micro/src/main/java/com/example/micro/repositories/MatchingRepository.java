package com.example.micro.repositories;

import com.example.micro.domain.Matching;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingRepository extends JpaRepository<Matching, String> {

}
