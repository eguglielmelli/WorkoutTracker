package com.eguglielmelli.controllers;
import com.eguglielmelli.dtos.WorkoutDto;
import com.eguglielmelli.dtos.WorkoutUpdateDto;
import com.eguglielmelli.entities.Workout;
import com.eguglielmelli.service.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Workout controller that will call WorkoutService CRUD operations
 */
@RestController
@RequestMapping("api/users/{userId}/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    @Autowired
    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping("/")
    public ResponseEntity<Workout> createWorkout(@RequestBody @Valid WorkoutDto workoutDto) {
        Workout workout = workoutService.createWorkout(workoutDto);
        return new ResponseEntity<>(workout, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long id) {
        boolean deleted = workoutService.deleteWorkout(id);
        if(deleted) {
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Workout> updateWorkout(@PathVariable Long id, WorkoutUpdateDto workoutUpdateDto) {
        boolean updatedWorkout = workoutService.updateWorkoutInfo(id, workoutUpdateDto);
        return updatedWorkout ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/info")
    public ResponseEntity<Workout> getWorkout(@PathVariable Long id) {
        Workout foundWorkout = workoutService.getWorkoutInfo(id);
        return new ResponseEntity<>(foundWorkout,HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Workout>> getUsersWorkouts(@PathVariable Long userId) {
        List<Workout> workouts = workoutService.getUsersWorkouts(userId);
        return new ResponseEntity<>(workouts, HttpStatus.OK);
    }


}
