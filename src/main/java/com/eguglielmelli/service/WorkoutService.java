package com.eguglielmelli.service;
import com.eguglielmelli.dtos.WorkoutDto;
import com.eguglielmelli.dtos.WorkoutUpdateDto;
import com.eguglielmelli.entities.Workout;
import com.eguglielmelli.repositories.UserRepository;
import com.eguglielmelli.repositories.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class WorkoutService {

    private WorkoutRepository workoutRepository;

    private UserRepository userRepository;

    @Autowired
    public WorkoutService(WorkoutRepository workoutRepository, UserRepository userRepository) {
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
    }

    /**
     * Method allows for the workout to be created, more strict
     * validation rules compared to updating because there are certain items
     * that cannot be null
     * Throws an exception if input is null
     * @param workoutDto workout data to be saved
     * @return the created workout
     */
    @Transactional
    public Workout createWorkout(WorkoutDto workoutDto) {
        if(workoutDto == null) {
            throw new IllegalArgumentException("Workout data object must not be null");
        }
        validateWorkoutInfo(workoutDto);

        Workout workout = new Workout(workoutDto.getName(),workoutDto.getNotes(),workoutDto.getDurationInMinutes(),
                workoutDto.getDate(),workoutDto.getCaloriesBurned(),workoutDto.getWorkoutType(),workoutDto.getUser());

        return workoutRepository.save(workout);

    }

    /**
     * Delete a workout that corresponds to a given workout ID number
     * @param workoutId id of workout
     */
    @Transactional
    public void deleteWorkout(Long workoutId) {
        Workout workout = workoutRepository.findById(workoutId).orElseThrow(() -> new IllegalArgumentException("" +
                "Workout with that ID not found"));

        workoutRepository.delete(workout);

    }

    /**
     * Method to update workout information if the user chooses
     * @param id of workout
     * @param workoutUpdateDto updated workout data
     * @return true if updated, false otherwise
     */
    @Transactional
    public boolean updateWorkoutInfo(Long id, WorkoutUpdateDto workoutUpdateDto) {
        return updateWorkout(id, workout -> {
            if(workoutUpdateDto.getName() != null && !workoutUpdateDto.getName().isEmpty()) {
                workout.setName(workoutUpdateDto.getName());
            }
            if(workoutUpdateDto.getDate() != null) {
                workout.setDate(workoutUpdateDto.getDate());
            }
            if(workoutUpdateDto.getDurationInMinutes() >= 0) {
                workout.setCaloriesBurned(workoutUpdateDto.getCaloriesBurned());
            }
            if(workoutUpdateDto.getNotes() != null) {
                workout.setNotes(workoutUpdateDto.getNotes());
            }
            if(workoutUpdateDto.getWorkoutType() != null) {
                workout.setWorkoutType(workoutUpdateDto.getWorkoutType());
            }
        });
    }

    @Transactional
    public Workout getWorkoutInfo(Long id) {
        return workoutRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("" +
                "Workout with that id is not found"));
    }

    /**
     * This method is going to handle all updates, this helps slim down the code and users can include
     * any of the fields they want in the update DTO
     * @param id of workout
     * @param updateAction dto that user will be transferring to update
     * @return true if updated, false otherwise
     */
    private boolean updateWorkout(Long id, Consumer<Workout> updateAction) {
        Optional<Workout> foundWorkout = workoutRepository.findById(id);
        if(foundWorkout.isPresent()) {
            Workout workout = foundWorkout.get();
            updateAction.accept(workout);
            workoutRepository.save(workout);
            return true;
        }
        return false;
    }



    /**
     * Validate a workout before creating it for the first time
     * These rules will be more strict than the update validations
     * Since some of the fields are mandatory
     * @param workoutDto data that user included to create workout
     */
    private void validateWorkoutInfo(WorkoutDto workoutDto) {
        if(workoutDto == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        if(workoutDto.getName() == null || workoutDto.getName().isEmpty()) {
            throw new IllegalArgumentException("Workout name must not be null or empty");
        }

        if(workoutDto.getDate() == null) {
            throw new IllegalArgumentException("Date of workout must not be null");
        }

        if(workoutDto.getDurationInMinutes() < 0) {
            throw new IllegalArgumentException("Duration of workout must be greater than or equal to 0");
        }

        if(workoutDto.getWorkoutType() == null || workoutDto.getWorkoutType().isEmpty()) {
            throw new IllegalArgumentException("Workout type cannot be null or empty");
        }

        if(workoutDto.getUser() == null) {
            throw new IllegalArgumentException("There must be a user associated with this workout");
        }

        if(userRepository.findById(workoutDto.getUser().getId()).isEmpty()) {
            throw new IllegalArgumentException("Cannot validate workout data because user with that id is not found");
        }

        if(workoutDto.getCaloriesBurned() < 0) {
            throw new IllegalArgumentException("Calories burned must be greater than or equal to 0");
        }

    }


}
