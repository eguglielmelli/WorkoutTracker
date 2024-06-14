package com.eguglielmelli.service;
import com.eguglielmelli.dtos.UserDto;
import com.eguglielmelli.entities.User;
import com.eguglielmelli.repositories.UserRepository;
import org.apache.tomcat.util.bcel.Const;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    private Validator validator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

    }

    @Test
    public void createUserTest_Normal_Success() {
        //Set up sample DTO
        UserDto userDto = createUserDto();

        //create user matching our Dto
        User user = new User("Test User", "test_user","encodedPassword","example@gmail.com",
                30,BigDecimal.valueOf(175).setScale(1,RoundingMode.HALF_UP),BigDecimal.valueOf(70).setScale(1,RoundingMode.HALF_UP),false,false);

        //mock encode and save user variable to the repository
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        //this will mock save to the repository
        User createdUser = userService.createUser(userDto);

        //comparisons between created user and our sample details above
        assertNotNull(createdUser);
        assertEquals("Test User",createdUser.getFullName());
        assertEquals("test_user",createdUser.getUsername());
        assertEquals("encodedPassword",createdUser.getPassword());
        assertEquals(new BigDecimal("175.0"),createdUser.getWeight());
        assertEquals(new BigDecimal("70.0"),createdUser.getHeight());
        assertEquals(30,createdUser.getAge());
        assertFalse(createdUser.isMetricSystem());

        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(any(User.class));

    }

    @Test
    public void createUserTest_NullName_shouldThrowException() {

        //set up with null name, method needs to throw illegal argument exception
        UserDto userDto = createUserDto();
        userDto.setFullName(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(userDto);
        });

        assertEquals("Name must not be empty or null.",exception.getMessage());

        //make sure repository never saved or encoded
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void createUserTest_usernameIsInUse_shouldThrowException() {
        //set up dto with a user who is trying to create an account
        //with a username that's already taken
        UserDto userDto = createUserDto();

        //return some user with the same username
        when(userRepository.findByusername("test_user")).thenReturn(Optional.of(new User()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> {
           userService.createUser(userDto);
        });

        assertEquals("A user with this username already exists.", exception.getMessage());
    }

    @Test
    public void createUserTest_emailIsInUse_shouldThrowException() {
        //set up like normal
        UserDto userDto = createUserDto();

        //return a user with the same email so we can test exception throwing
        when(userRepository.findByemail("test@gmail.com")).thenReturn(Optional.of(new User()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> {
            userService.createUser(userDto);
        });

        assertEquals("A user with this email already exists.", exception.getMessage());
    }

    @Test
    public void createUserTest_nullPassword_shouldThrowException() {
        //set up with null password, method needs to throw illegal argument exception
        UserDto userDto = createUserDto();
        userDto.setPassword(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(userDto);
        });

        assertEquals("Password must not be empty or null.",exception.getMessage());

        //make sure repository never saved or encoded anything
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void createUserTest_negativeAge_shouldThrowException() {
        //set up dto with negative age
        UserDto userDto = createUserDto();
        userDto.setAge(-1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(userDto);
        });

        //check our exception message that was thrown from validateUserBeforeCreation()
        assertEquals("Age must be greater than 0.",exception.getMessage());

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void createUserTest_invalidEmailFormat_shouldCauseViolation() {
        //create dto and then set email to invalid format
        UserDto userDto = createUserDto();
        userDto.setEmail("test-gmail.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<UserDto> violation = violations.iterator().next();
        assertEquals("must be a well-formed email address", violation.getMessage());
    }

    @Test
    public void createUserTest_metricSystemNotSet_shouldDefaultToFalse() {

        //set up dto with metric system not explicitly set
        UserDto userDto = createUserDto();

        User user = new User("Test User","test_user","encodedPassword","test@gmail.com",30
                ,BigDecimal.valueOf(175.0).setScale(1,RoundingMode.HALF_UP),BigDecimal.valueOf(75).setScale(1,RoundingMode.HALF_UP),
                false,false);

        //mock encode password and mock save into repository
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(userDto);

        //asserting other fields just to be sure
        //but main one is assertFalse() of metricSystem because it should be set
        //to false by default
        assertEquals("Test User",createdUser.getFullName());
        assertEquals("test_user", createdUser.getUsername());
        assertEquals("test@gmail.com",createdUser.getEmail());
        assertEquals("encodedPassword", createdUser.getPassword());
        assertEquals(new BigDecimal("175.0").setScale(1,RoundingMode.HALF_UP), createdUser.getWeight());
        assertEquals(new BigDecimal("75").setScale(1,RoundingMode.HALF_UP), createdUser.getHeight());
        assertEquals(30,createdUser.getAge());
        assertFalse(createdUser.isMetricSystem());
    }
    @Test
    public void createUserTest_weightBelowZero_shouldCauseViolation() {
        UserDto userDto = createUserDto();
        //negative weight should be handled by our spring annotation so we will use the validator
        //in order to make sure it is caught
        userDto.setWeight(BigDecimal.valueOf(-1.0));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertFalse(violations.isEmpty());

        ConstraintViolation<UserDto> violation = violations.iterator().next();

        assertEquals("must be greater than or equal to 0.0", violation.getMessage());
    }

    @Test
    public void createUserTest_heightBelowZero_shouldCauseViolation() {
        UserDto userDto = createUserDto();
        //negative height should be handled by our spring annotation so we will use the validator
        //in order to make sure it is caught
        userDto.setHeight(BigDecimal.valueOf(-1.0));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertFalse(violations.isEmpty());

        ConstraintViolation<UserDto> violation = violations.iterator().next();

        assertEquals("must be greater than or equal to 0.0", violation.getMessage());
    }

    @Test
    public void deleteUserTest_Normal_Success() {
        //we will be using regular user for this test since dto
        //does not have id
        User exampleUser = createExampleUser();


        //return a user with this id
        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.of(exampleUser));
        when(userRepository.save(any(User.class))).thenReturn(exampleUser);

        boolean result = userService.deleteUser(exampleUser.getId());

        assertTrue(result);
        assertTrue(exampleUser.isDeleted());
        verify(userRepository, times(1)).findById(exampleUser.getId());
        verify(userRepository, times(1)).save(exampleUser);

    }

    @Test
    public void deleteUserTest_userDoesNotExist_shouldReturnFalse() {
        Long userId = 1L;
        //mock return no user with given id
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        boolean result = userService.deleteUser(userId);

        assertFalse(result);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository,times(0)).save(any(User.class));
    }

    @Test
    public void changeMeasurementSystemTest_Normal_Success() {
        //Important: isMetricSystem is set to false by default in these tests
        //so we will try setting it to true in this method
        User exampleUser = createExampleUser();

        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.of(exampleUser));

        boolean result = userService.changeMeasurementSystem(exampleUser.getId(), true);

        assertTrue(result);
        assertTrue(exampleUser.isMetricSystem());

        verify(userRepository, times(1)).findById(exampleUser.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void changeMeasurementSystemTest_userDoesNotExist_shouldReturnFalse() {
        User exampleUser = createExampleUser();

        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.empty());

        // example user is metric system always set to false, so we must try to set it to true
        boolean result = userService.changeMeasurementSystem(exampleUser.getId(), true);

        assertFalse(result);
        verify(userRepository, times(1)).findById(exampleUser.getId());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void changeWeightTest_Normal_Success() {
        // normal test case where the weight should simply be set
        User exampleUser = createExampleUser();

        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.of(exampleUser));

        boolean result = userService.changeWeight(exampleUser.getId(), BigDecimal.valueOf(177.4));

        assertTrue(result);

        verify(userRepository, times(1)).findById(exampleUser.getId());
        verify(userRepository, times(1)).save(exampleUser);

    }

    @Test
    public void changeWeightTest_negativeWeight_shouldThrowException() {
        //user tries to change to negative weight, this is not allowed
        User exampleUser = createExampleUser();

        BigDecimal negativeWeight = BigDecimal.valueOf(-177.5);

        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.of(exampleUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.changeWeight(exampleUser.getId(), negativeWeight);
        });

        assertEquals("Weight must be greater than or equal to 0", exception.getMessage());
        assertEquals(BigDecimal.valueOf(170.0), exampleUser.getWeight());

        verify(userRepository,never()).findById(exampleUser.getId());
        verify(userRepository, never()).save(any(User.class));

    }
    @Test
    public void changeWeightTest_extraDecimalPlaces_shouldRoundToOne() {
        //user enters weight with a couple extra decimal places
        //function must round to one before saving to repository
        User exampleUser = createExampleUser();

        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.of(exampleUser));

        boolean result = userService.changeWeight(exampleUser.getId(), BigDecimal.valueOf(170.456456));

        assertTrue(result);
        assertEquals(BigDecimal.valueOf(170.5), exampleUser.getWeight());
        verify(userRepository, times(1)).findById(exampleUser.getId());
        verify(userRepository, times(1)).save(exampleUser);
    }

    @Test
    public void changeWeightTest_nullWeight_shouldThrowException() {
        //calling the function with null needs to throw exception
        User exampleUser = createExampleUser();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.changeWeight(exampleUser.getId(), null);
        });

        assertEquals("Weight must be greater than or equal to 0", exception.getMessage());
        assertEquals(BigDecimal.valueOf(170.0), exampleUser.getWeight());

        verify(userRepository, never()).findById(exampleUser.getId());
        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    public void changeHeightTest_Normal_Success() {
        //this is a simple case, height should be updated after
        //calling the function with a height > 0
        User exampleUser = createExampleUser();

        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.of(exampleUser));

        boolean result = userService.changeHeight(exampleUser.getId(), BigDecimal.valueOf(65.0));

        assertTrue(result);
        assertEquals(BigDecimal.valueOf(65.0), exampleUser.getHeight());

        verify(userRepository, times(1)).findById(exampleUser.getId());
        verify(userRepository, times(1)).save(exampleUser);

    }

    @Test
    public void changeHeightTest_negativeWeight_shouldThrowException() {
        //user enters negative height, we need to make sure the method
        //throws an illegalArgumentException and the repository doesn't save
        User exampleUser = createExampleUser();

        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.of(exampleUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.changeHeight(createExampleUser().getId(), BigDecimal.valueOf(-50.0));
        });

        assertEquals("Height must be greater than or equal to 0", exception.getMessage());
        verify(userRepository, never()).findById(exampleUser.getId());
        verify(userRepository, never()).save(exampleUser);
    }

    @Test
    public void changeHeightTest_nullWeight_shouldThrowException() {
        //user enters null in the height, so method must throw an
        //illegalArgumentException
        User exampleUser = createExampleUser();

        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.of(exampleUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.changeHeight(createExampleUser().getId(), null);
        });

        assertEquals("Height must be greater than or equal to 0", exception.getMessage());
        verify(userRepository, never()).findById(exampleUser.getId());
        verify(userRepository, never()).save(exampleUser);

    }

    @Test
    public void changeHeightTest_extraDecimalPlaces_shouldRoundToOne() {
        //user enters multiple decimal places, the method should
        //round it to one decimal place and successfully save
        User exampleUser = createExampleUser();

        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.of(exampleUser));

        boolean result = userService.changeHeight(exampleUser.getId(), BigDecimal.valueOf(65.43434444));

        assertTrue(result);
        assertEquals(BigDecimal.valueOf(65.4), exampleUser.getHeight());
        verify(userRepository, times(1)).findById(exampleUser.getId());
        verify(userRepository, times(1)).save(exampleUser);

    }

    /**
     * Since we're creating so many sample objects, combining into one method
     * and can adjust attributes as necessary
     * @return set up UserDto object
     */
    private UserDto createUserDto() {
        UserDto userDto = new UserDto();
        userDto.setUsername("test_user");
        userDto.setFullName("Test User");
        userDto.setEmail("test@gmail.com");
        userDto.setPassword("password");
        userDto.setAge(30);
        userDto.setWeight(BigDecimal.valueOf(175.0));
        userDto.setHeight(BigDecimal.valueOf(75));
        return userDto;
    }

    /**
     * Helper method to create a user for methods that don't deal with data transfer
     * metric system always default to negative here, and id always == 1
     * @return set up User object
     */
    private User createExampleUser() {
        User user = new User();
        Long userId = 1L;
        user.setId(userId);
        user.setFullName("Test User");
        user.setEmail("test@gmail.com");
        user.setUsername("test_user");
        user.setPassword("password");
        user.setDeleted(false);
        user.setWeight(BigDecimal.valueOf(170.0));
        user.setHeight(BigDecimal.valueOf(70.0));
        user.setMetricSystem(false);

        return user;
    }
}
