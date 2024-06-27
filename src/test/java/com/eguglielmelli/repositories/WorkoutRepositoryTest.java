package com.eguglielmelli.repositories;

import com.eguglielmelli.config.SecurityConfig;
import com.eguglielmelli.entities.User;
import com.eguglielmelli.entities.Workout;
import com.eguglielmelli.entities.WorkoutType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(SecurityConfig.class)
public class WorkoutRepositoryTest {

    @Autowired
    WorkoutRepository workoutRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Test User", "test_user", "password", "test@gmail.com",
                26, BigDecimal.valueOf(150.0), BigDecimal.valueOf(75.0), false, false);
        entityManager.persistAndFlush(user);
    }

    @Test
    public void findByUserIdTest_Normal_Success() {
        Workout workout1 = new Workout();
        workout1.setWorkoutType(WorkoutType.RUNNING);
        workout1.setDate(LocalDate.parse("2024-06-24"));
        workout1.setName("Sample workout 1");
        workout1.setCaloriesBurned(100);
        workout1.setDurationInMinutes(10);
        workout1.setUser(user);

        Workout workout2 = new Workout();
        workout2.setWorkoutType(WorkoutType.SWIMMING);
        workout2.setDate(LocalDate.parse("2024-06-25"));
        workout2.setName("Sample workout 2");
        workout2.setCaloriesBurned(200);
        workout2.setDurationInMinutes(20);
        workout2.setUser(user);

        entityManager.persistAndFlush(workout1);
        entityManager.persistAndFlush(workout2);

        List<Workout> workouts = workoutRepository.findByUser_id(user.getId());

        assertThat(workouts).isNotEmpty();
        assertThat(workouts.size()).isEqualTo(2);
        assertThat(workouts).extracting("name").contains("Sample workout 1", "Sample workout 2");
        assertThat(workouts).extracting("workoutType").contains(WorkoutType.RUNNING, WorkoutType.SWIMMING);
    }

    @Test
    public void findByUserIdTest_userNotFound_shouldReturnEmptyList() {
        // Try to find workouts for a non-existing user ID
        Long nonExistentUserId = 999L;
        List<Workout> workouts = workoutRepository.findByUser_id(nonExistentUserId);

        //we expect an empty list
        assertThat(workouts).isEmpty();
    }

    @Test
    public void findByUserIdTest_userHasNoWorkouts_shouldReturnEmptyList() {
        //we again expect empty list if the user has not logged any workouts yet
        List<Workout> workouts = workoutRepository.findByUser_id(user.getId());

        assertThat(workouts).isEmpty();
    }
}

