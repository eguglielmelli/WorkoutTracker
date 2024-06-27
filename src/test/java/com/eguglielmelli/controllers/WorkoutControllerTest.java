package com.eguglielmelli.controllers;

import com.eguglielmelli.config.SecurityConfig;
import com.eguglielmelli.dtos.WorkoutDto;
import com.eguglielmelli.entities.User;
import com.eguglielmelli.entities.Workout;
import com.eguglielmelli.entities.WorkoutType;
import com.eguglielmelli.service.WorkoutService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkoutController.class)
@Import(SecurityConfig.class)
public class WorkoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkoutService workoutService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createWorkout_Normal_Success() throws Exception {
        //set up example user to tie with our workouts
        User user = new User();
        user.setId(1L);
        user.setFullName("Test User");
        user.setEmail("test@gmail.com");
        user.setUsername("test_user");
        user.setDeleted(false);
        user.setMetricSystem(false);
        user.setHeight(BigDecimal.valueOf(75.0));
        user.setWeight(BigDecimal.valueOf(150.0));
        user.setPassword("password");

        //actual workoutdto that will be used to be created
        WorkoutDto workoutDto = new WorkoutDto();
        workoutDto.setWorkoutType(WorkoutType.RUNNING);
        workoutDto.setUser(user);
        workoutDto.setDate(LocalDate.parse("2024-06-24"));
        workoutDto.setName("Sample workout");
        workoutDto.setDurationInMinutes(50);
        workoutDto.setCaloriesBurned(100);
        workoutDto.setNotes("Workout notes");

        //create workout to match dto
        Workout workout = new Workout("Sample workout","Workout notes",50, LocalDate.parse("2024-06-24"),100,WorkoutType.RUNNING,user);

        when(workoutService.createWorkout(any(WorkoutDto.class))).thenReturn(workout);

        String workoutDtoJson = objectMapper.writeValueAsString(workoutDto);

        //perform the post request
        mockMvc.perform(post("/api/users/{userId}/workouts/", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(workoutDtoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Sample workout"))
                .andExpect(jsonPath("$.workoutType").value("RUNNING"))
                .andExpect(jsonPath("$.durationInMinutes").value(50))
                .andExpect(jsonPath("$.caloriesBurned").value(100))
                .andExpect(jsonPath("$.notes").value("Workout notes"))
                .andExpect(jsonPath("$.date").value("2024-06-24"))
                .andExpect(jsonPath("$.user.fullName").value("Test User"))
                .andExpect(jsonPath("$.user.username").value("test_user"))
                .andExpect(jsonPath("$.user.email").value("test@gmail.com"));
    }

    @Test
    public void deleteWorkout_Normal_Success() throws Exception {
        Long workoutId = 1L;
        Long userId = 1L;

        when(workoutService.deleteWorkout(workoutId)).thenReturn(true);

        mockMvc.perform(delete("/api/users/{userId}/workouts/{id}/delete", userId, workoutId))
                .andExpect(status().isOk());
    }

    @Test
    public void updateWorkoutInfoTest_Normal_Success() {

    }

}
