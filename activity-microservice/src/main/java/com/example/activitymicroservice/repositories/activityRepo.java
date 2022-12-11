package com.example.activitymicroservice.repositories;

import com.example.activitymicroservice.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface activityRepo extends JpaRepository<Activity, Long> {
}
