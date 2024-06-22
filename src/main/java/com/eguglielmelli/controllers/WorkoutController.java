package com.eguglielmelli.controllers;
import com.eguglielmelli.dtos.WorkoutDto;
import com.eguglielmelli.entities.Workout;
import com.eguglielmelli.service.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/workouts")
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

}
