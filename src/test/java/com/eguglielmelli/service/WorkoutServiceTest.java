package com.eguglielmelli.service;


import com.eguglielmelli.dtos.WorkoutDto;
import com.eguglielmelli.dtos.WorkoutUpdateDto;
import com.eguglielmelli.entities.User;
import com.eguglielmelli.entities.Workout;
import com.eguglielmelli.repositories.UserRepository;
import com.eguglielmelli.repositories.WorkoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WorkoutServiceTest {

    @Mock
    WorkoutRepository workoutRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    WorkoutService workoutService;

    private Validator validator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void createWorkoutTest_Normal_Success() {
        //normal case when saving a workout for the first time
        //createUserForWorkout() is called in createWorkoutDto
        //so that is where the user comes from
        WorkoutDto workoutDto = createWorkoutDto();

        Workout workout = new Workout("Workout 1","Example Notes for a given workout",21,LocalDate.parse("2024-06-21"),100,"cardio",createUserForWorkout());
        when(workoutRepository.save(any(Workout.class))).thenReturn(workout);
        when(userRepository.findById(createWorkoutDto().getUser().getId())).thenReturn(Optional.ofNullable(workoutDto.getUser()));

        Workout createdWorkout = workoutService.createWorkout(workoutDto);

        assertNotNull(createdWorkout);
        assertEquals("Workout 1", createdWorkout.getName());
        assertEquals(LocalDate.parse("2024-06-21"), createdWorkout.getDate());
        assertEquals("Example Notes for a given workout", createdWorkout.getNotes());
        assertEquals(21, createdWorkout.getDurationInMinutes());
        assertEquals(100, createdWorkout.getCaloriesBurned());
        assertNotNull(createdWorkout.getUser());
        assertEquals("Test User", createdWorkout.getUser().getFullName());
        verify(workoutRepository, times(1)).save(any(Workout.class));
        verify(userRepository, times(1)).findById(createdWorkout.getUser().getId());
    }

    @Test
    public void createWorkoutTest_titleIsNull_shouldThrowException() {
        //title is a required field for the workouts so this scenario
        //would cause an exception
        WorkoutDto workoutDto = createWorkoutDto();
        workoutDto.setName(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            workoutService.createWorkout(workoutDto);
        });

        assertEquals("Workout name must not be null or empty", exception.getMessage());

    }

    @Test
    public void createWorkoutTest_durationIsNegative_shouldThrowException() {
        //duration must be greater than or equal to 0
        //otherwise it does not make sense to have a workout with negative time
        WorkoutDto workoutDto = createWorkoutDto();
        workoutDto.setDurationInMinutes(-1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            workoutService.createWorkout(workoutDto);
        });

        assertEquals("Duration of workout must be greater than or equal to 0", exception.getMessage());
    }

    @Test
    public void createWorkoutTest_dateIsNull_shouldThrowException() {
        //date is null, this should throw an exception because a workout needs an associated date
        WorkoutDto workoutDto = createWorkoutDto();
        workoutDto.setDate(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            workoutService.createWorkout(workoutDto);
        });

        assertEquals("Date of workout must not be null", exception.getMessage());
    }

    @Test
    public void createWorkoutTest_workoutTypeIsNull_shouldThrowException() {
        //workout type needs to be specified by the user or else this method should throw an exception
        WorkoutDto workoutDto = createWorkoutDto();
        workoutDto.setWorkoutType(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
           workoutService.createWorkout(workoutDto);
        });

        assertEquals("Workout type cannot be null or empty", exception.getMessage());
    }

    @Test
    public void createWorkoutTest_userIsNull_shouldThrowException() {
        //user is null, this is not allowed as any given workout needs to be
        //tied to a user object by user id
        WorkoutDto workoutDto = createWorkoutDto();
        workoutDto.setUser(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            workoutService.createWorkout(workoutDto);
        });

        assertEquals("There must be a user associated with this workout", exception.getMessage());
    }

    @Test
    public void createWorkoutTest_caloriesBurnedIsNegative_shouldThrowException() {
        //calories burned should not be a negative number, must be 0 or greater or else
        //we throw an exception
        WorkoutDto workoutDto = createWorkoutDto();
        workoutDto.setCaloriesBurned(-100);

        //need to return a user due to logic where we check user id before checking calories burned
        when(userRepository.findById(workoutDto.getUser().getId())).thenReturn(Optional.of(workoutDto.getUser()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            workoutService.createWorkout(workoutDto);
        });

        assertEquals("Calories burned must be greater than or equal to 0", exception.getMessage());
    }
    @Test
    public void createWorkoutTest_userNotFound_shouldThrowException() {
        //every workout should have a user associated with it so if the userid
        //is not found we should throw an exception
        WorkoutDto workoutDto = createWorkoutDto();
        workoutDto.setCaloriesBurned(-100);

        //need to return empty so we simulate a workout not having a valid user id associated
        when(userRepository.findById(workoutDto.getUser().getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            workoutService.createWorkout(workoutDto);
        });

        assertEquals("Cannot validate workout data because user with that id is not found", exception.getMessage());
    }

    @Test
    public void deleteWorkoutTest_Normal_Success() {
        //simple case, should easily be deleted when we call our delete method
        Workout sampleWorkout = createSampleWorkout();

        when(workoutRepository.findById(sampleWorkout.getId())).thenReturn(Optional.of(sampleWorkout));

        workoutService.deleteWorkout(sampleWorkout.getId());

        verify(workoutRepository, times(1)).findById(sampleWorkout.getId());
        verify(workoutRepository, times(1)).delete(sampleWorkout);
    }

    @Test
    public void deleteWorkoutTest_workoutIdNotFound_shouldThrowException() {
        //unlikely case, but if delete is called on a workout without a valid id
        //we need to throw an IllegalArgumentException
        Workout sampleWorkout = createSampleWorkout();

        when(workoutRepository.findById(sampleWorkout.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
           workoutService.deleteWorkout(sampleWorkout.getId());
        });

        assertEquals("Workout with that ID not found", exception.getMessage());
        verify(workoutRepository, times(1)).findById(sampleWorkout.getId());
        verify(workoutRepository, never()).delete(sampleWorkout);
    }

    @Test
    public void updateWorkoutInfoTest_Normal_Success() {
        //very basic case just making sure that we are updating
        //the workout details correctly
        Workout sampleWorkout = createSampleWorkout();

        WorkoutUpdateDto workoutUpdateDto = new WorkoutUpdateDto();
        workoutUpdateDto.setWorkoutType("Running");
        workoutUpdateDto.setName("Updated workout");
        workoutUpdateDto.setCaloriesBurned(10000);

        when(workoutRepository.findById(sampleWorkout.getId())).thenReturn(Optional.of(sampleWorkout));

        boolean result = workoutService.updateWorkoutInfo(sampleWorkout.getId(), workoutUpdateDto);

        assertTrue(result);
        assertEquals("Running", sampleWorkout.getWorkoutType());
        assertEquals("Updated workout", sampleWorkout.getName());
        assertEquals(10000, sampleWorkout.getCaloriesBurned());
        verify(workoutRepository, times(1)).findById(sampleWorkout.getId());
        verify(workoutRepository, times(1)).save(sampleWorkout);
    }

    @Test
    public void updateWorkoutInfoTest_nameIsNull_shouldNotUpdate() {
        //name cannot be null for a workout it is mandatory for it to have a
        //name when entered into the database
        //so if name is null, we will just not update it
        Workout sampleWorkout = createSampleWorkout();

        when(workoutRepository.findById(sampleWorkout.getId())).thenReturn(Optional.of(sampleWorkout));

        WorkoutUpdateDto workoutUpdateDto = new WorkoutUpdateDto();
        workoutUpdateDto.setName(null);
        workoutUpdateDto.setWorkoutType("Running");

        boolean result = workoutService.updateWorkoutInfo(sampleWorkout.getId(), workoutUpdateDto);
        assertTrue(result);

        //making sure that workout name hasn't changed, but workout type has
        assertEquals("Workout 1", sampleWorkout.getName());
        assertEquals("Running", sampleWorkout.getWorkoutType());
        verify(workoutRepository, times(1)).save(sampleWorkout);
    }
    @Test
    public void updateWorkoutInfoTest_dateIsNull_shouldNotUpdate() {
        //every workout needs to have a date so it must not be null when a user is updating
        //if it is, the method simply won't update it
        Workout sampleWorkout = createSampleWorkout();

        when(workoutRepository.findById(sampleWorkout.getId())).thenReturn(Optional.of(sampleWorkout));

        WorkoutUpdateDto workoutUpdateDto = new WorkoutUpdateDto();
        workoutUpdateDto.setDate(null);

        boolean result = workoutService.updateWorkoutInfo(sampleWorkout.getId(), workoutUpdateDto);

        assertTrue(result);
        assertEquals(LocalDate.parse("2024-06-21"), sampleWorkout.getDate());

    }

    @Test
    public void updateWorkoutInfoTest_durationAndCaloriesAreNegative_shouldNotUpdate() {
        //if the duration and/or calories are negative we will not update it since it
        //makes no sense to have negative workout duration or calories
        Workout sampleWorkout = createSampleWorkout();

        when(workoutRepository.findById(sampleWorkout.getId())).thenReturn(Optional.of(sampleWorkout));

        WorkoutUpdateDto workoutUpdateDto = new WorkoutUpdateDto();
        workoutUpdateDto.setDurationInMinutes(-1);
        workoutUpdateDto.setCaloriesBurned(-100);

        boolean result = workoutService.updateWorkoutInfo(sampleWorkout.getId(), workoutUpdateDto);

        assertTrue(result);
        assertEquals(21, sampleWorkout.getDurationInMinutes());
        assertEquals(100, sampleWorkout.getCaloriesBurned());
    }

    @Test
    public void updateWorkoutInfoTest_notesAndTypeAreNull_shouldNotUpdate() {
        //same case as above, its very important we don't update any fields
        //with null values
        Workout sampleWorkout = createSampleWorkout();

        when(workoutRepository.findById(sampleWorkout.getId())).thenReturn(Optional.of(sampleWorkout));

        WorkoutUpdateDto workoutUpdateDto = new WorkoutUpdateDto();
        workoutUpdateDto.setNotes(null);
        workoutUpdateDto.setWorkoutType(null);

        boolean result = workoutService.updateWorkoutInfo(sampleWorkout.getId(), workoutUpdateDto);

        assertTrue(result);
        assertEquals("Example Notes for a given workout", sampleWorkout.getNotes());
        assertEquals("cardio", sampleWorkout.getWorkoutType());
    }

    @Test
    public void getWorkoutInfoTest_Normal_Success() {
        //basic case just to make sure everything is being returned as intended
        Workout sampleWorkout = createSampleWorkout();

        when(workoutRepository.findById(sampleWorkout.getId())).thenReturn(Optional.of(sampleWorkout));

        Workout foundWorkout = workoutService.getWorkoutInfo(sampleWorkout.getId());

        assertNotNull(foundWorkout);
        assertEquals("cardio", foundWorkout.getWorkoutType());
        assertEquals("Workout 1", foundWorkout.getName());
        assertEquals(100, foundWorkout.getCaloriesBurned());
        assertEquals("Example Notes for a given workout", foundWorkout.getNotes());
        assertEquals(21, foundWorkout.getDurationInMinutes());
        assertEquals(LocalDate.parse("2024-06-21"), foundWorkout.getDate());

        verify(workoutRepository, times(1)).findById(foundWorkout.getId());

    }

    @Test
    public void getWorkoutInfoTest_workoutIdNotFound_shouldThrowException() {
        //case where workout Id is not found, we will throw an IllegalArgumentException
        Workout sampleWorkout = createSampleWorkout();

        when(workoutRepository.findById(sampleWorkout.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            workoutService.getWorkoutInfo(sampleWorkout.getId());
        });

        assertEquals("Workout with that id is not found", exception.getMessage());
        verify(workoutRepository, never()).save(sampleWorkout);
    }




    /**
     * Helper method to set up a dto for testing createWorkout() specifically
     * @return a created workoutDTO
     */
    private WorkoutDto createWorkoutDto() {
        //create our user to attach to this workout
        User user = createUserForWorkout();

        WorkoutDto workoutDto = new WorkoutDto();
        workoutDto.setWorkoutType("cardio");
        workoutDto.setDate(LocalDate.parse("2024-06-21"));
        workoutDto.setName("Workout 1");
        workoutDto.setNotes("Example Notes for a given workout");
        workoutDto.setDurationInMinutes(21);
        workoutDto.setCaloriesBurned(100);
        workoutDto.setUser(user);

        return workoutDto;
    }

    /**
     * Since each workout needs a corresponding user, helper method easily creates one and is called
     * in both of the other helpers
     * @return a newly created user
     */
    private User createUserForWorkout() {
        User user = new User();
        user.setUsername("username");
        user.setMetricSystem(false);
        user.setFullName("Test User");
        user.setId(1L);
        user.setEmail("test@gmail.com");

        return user;
    }

    /**
     * Created a workout that will be used in tests when calling
     * repository methods
     * @return a newly created workout
     */
    private Workout createSampleWorkout() {
        User user = createUserForWorkout();
        Workout workout = new Workout();
        workout.setId(1L);
        workout.setWorkoutType("cardio");
        workout.setDate(LocalDate.parse("2024-06-21"));
        workout.setName("Workout 1");
        workout.setNotes("Example Notes for a given workout");
        workout.setDurationInMinutes(21);
        workout.setCaloriesBurned(100);
        workout.setUser(user);

        return workout;
    }

}
