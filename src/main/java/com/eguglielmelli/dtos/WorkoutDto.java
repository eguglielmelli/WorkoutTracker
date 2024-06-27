package com.eguglielmelli.dtos;
import com.eguglielmelli.entities.User;
import com.eguglielmelli.entities.WorkoutType;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class WorkoutDto {

    @NotNull
    private String name;

    private String notes;

    @NotNull
    private int durationInMinutes;

    @NotNull
    private LocalDate date;

    private int caloriesBurned;

    @NotNull
    private WorkoutType workoutType;

    @NotNull
    private User user;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public WorkoutType getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(WorkoutType workoutType) {
        this.workoutType = workoutType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
