package com.eguglielmelli.entities;


import javax.validation.constraints.NotNull;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "workouts")
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "notes")
    private String notes;

    @NotNull
    @Column(name = "workout_duration",nullable = false)
    private int durationInMinutes;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "calories_burned")
    private int caloriesBurned;

    @NotNull
    @Column(name = "workout_type",nullable = false)
    private String workoutType;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    public Workout(String name, String notes, int durationInMinutes, LocalDate date, int caloriesBurned, String workoutType) {
        this.name = name;
        this.notes = notes;
        this.durationInMinutes = durationInMinutes;
        this.date = date;
        this.caloriesBurned = caloriesBurned;
        this.workoutType = workoutType;
    }

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

    public String getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(String workoutType) {
        this.workoutType = workoutType;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
