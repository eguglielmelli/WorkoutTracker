package com.eguglielmelli.repositories;

import com.eguglielmelli.entities.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    List<Workout> findByUser_id(Long userId);
}
