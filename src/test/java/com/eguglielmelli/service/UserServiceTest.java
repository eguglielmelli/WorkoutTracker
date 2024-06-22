package com.eguglielmelli.service;
import com.eguglielmelli.dtos.UserDto;
import com.eguglielmelli.dtos.UserUpdateDto;
import com.eguglielmelli.entities.User;
import com.eguglielmelli.repositories.UserRepository;
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
        userService = new UserService(userRepository, passwordEncoder, validator);

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
    public void createUserTest_SomeCapitals_shouldCastToLowerCase() {
        //Sample case where email and username have capital letters
        //testing if they are stored as lowercase for uniformity
        UserDto userDto = createUserDto();
        userDto.setUsername("TEST_uSeR");
        userDto.setEmail("EXAMPLE@GMAIL.COM");

        User user = new User("Test User", "test_user","encodedPassword","example@gmail.com",
                30,BigDecimal.valueOf(175).setScale(1,RoundingMode.HALF_UP),BigDecimal.valueOf(70).setScale(1,RoundingMode.HALF_UP),false,false);

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(userDto);

        assertNotNull(createdUser);
        assertEquals("example@gmail.com", createdUser.getEmail());
        assertEquals("Test User",createdUser.getFullName());
        assertEquals("test_user",createdUser.getUsername());
        assertEquals("encodedPassword",createdUser.getPassword());
        assertEquals(new BigDecimal("175.0"),createdUser.getWeight());
        assertEquals(new BigDecimal("70.0"),createdUser.getHeight());
        assertEquals(30,createdUser.getAge());
        assertFalse(createdUser.isMetricSystem());

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
    public void updateUserInfoTest_onlyEmailIncluded_Normal_Success() {
        //normal case here, only including email just to make sure
        //if user only wants to include one piece of info to update, they can
        User exampleUser = createExampleUser();

        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.of(exampleUser));

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setEmail("update_user@gmail.com");

        boolean result = userService.updateUserInfo(exampleUser.getId(), userUpdateDto);

        assertTrue(result);
        assertEquals("update_user@gmail.com",exampleUser.getEmail());
        verify(userRepository, times(1)).findById(exampleUser.getId());
        verify(userRepository, times(1)).save(exampleUser);

    }

    @Test
    public void updateUserInfoTest_onlyUsernameIncluded_Normal_Success() {
        //same as above, just testing username this time

        User exampleUser = createExampleUser();

        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.of(exampleUser));

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setUsername("testing_update_user");

        boolean result = userService.updateUserInfo(exampleUser.getId(), userUpdateDto);

        assertTrue(result);
        assertEquals("testing_update_user",exampleUser.getUsername());
        verify(userRepository, times(1)).findById(exampleUser.getId());
        verify(userRepository, times(1)).save(exampleUser);
    }

    @Test
    public void updateUserInfoTest_onlyAgeIncluded_Normal_Success() {
        //same, just including age this time
        User exampleUser = createExampleUser();

        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.of(exampleUser));


        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setAge(35);

        boolean result = userService.updateUserInfo(exampleUser.getId(), userUpdateDto);

        assertTrue(result);
        assertEquals(35,exampleUser.getAge());
        verify(userRepository, times(1)).findById(exampleUser.getId());
        verify(userRepository, times(1)).save(exampleUser);
    }

    @Test
    public void updateUserInfoTest_onlyHeightIncluded_Normal_Success() {
        //only height tested
        User exampleUser = createExampleUser();

        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.of(exampleUser));


        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setHeight(BigDecimal.valueOf(75));

        boolean result = userService.updateUserInfo(exampleUser.getId(), userUpdateDto);

        assertTrue(result);
        assertEquals(BigDecimal.valueOf(75.0),exampleUser.getHeight());
        verify(userRepository, times(1)).findById(exampleUser.getId());
        verify(userRepository, times(1)).save(exampleUser);
    }

    @Test
    public void updateUserInfoTest_onlyWeightIncluded_Normal_Success() {
        //only weight tested
        User exampleUser = createExampleUser();

        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.of(exampleUser));


        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setWeight(BigDecimal.valueOf(67.5));

        boolean result = userService.updateUserInfo(exampleUser.getId(), userUpdateDto);

        assertTrue(result);
        assertEquals(BigDecimal.valueOf(67.5),exampleUser.getWeight());
        verify(userRepository, times(1)).findById(exampleUser.getId());
        verify(userRepository, times(1)).save(exampleUser);
    }

    @Test
    public void updateUserInfoTest_multipleFieldsIncluded_Normal_Success() {
        //multiple fields simulating if a user was updating almost
        //all of their info
        User exampleUser = createExampleUser();

        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.of(exampleUser));


        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setWeight(BigDecimal.valueOf(67.5));
        userUpdateDto.setEmail("updated_email@gmail.com");
        userUpdateDto.setUsername("updated_username");
        userUpdateDto.setMetricSystem(true);
        userUpdateDto.setAge(46);
        userUpdateDto.setHeight(BigDecimal.valueOf(70.0));

        boolean result = userService.updateUserInfo(exampleUser.getId(), userUpdateDto);

        assertTrue(result);
        assertEquals(46, exampleUser.getAge());
        assertEquals("updated_username", exampleUser.getUsername());
        assertTrue(exampleUser.isMetricSystem());
        assertEquals(BigDecimal.valueOf(70.0), exampleUser.getHeight());
        assertEquals("updated_email@gmail.com", exampleUser.getEmail());
        assertEquals(BigDecimal.valueOf(67.5),exampleUser.getWeight());
        verify(userRepository, times(1)).findById(exampleUser.getId());
        verify(userRepository, times(1)).save(exampleUser);
    }

    @Test
    public void updateUserInfoTest_invalidEmailFormat_shouldViolateConstraint() {
        //trying to update email but putting in a bad email format
        //letting java handle the validation
        User exampleUser = createExampleUser();

        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.of(exampleUser));


        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setEmail("email.gmail");

        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(userUpdateDto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<UserUpdateDto> violation = violations.iterator().next();

        assertEquals("must be a well-formed email address", violation.getMessage());

        //checking that email never changed
        assertEquals("test@gmail.com", exampleUser.getEmail());

        verify(userRepository, never()).findById(exampleUser.getId());
        verify(userRepository, never()).save(exampleUser);

    }
    @Test
    public void updateUserInfoTest_invalidAge_shouldViolateConstraint() {
        //this case will also be handled in the code because it checks
        //if age is greater than 0, but here specifically we will check
        //that it is handled by validator
        User exampleUser = createExampleUser();

        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.of(exampleUser));


        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setAge(-1);

        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(userUpdateDto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<UserUpdateDto> violation = violations.iterator().next();

        assertEquals("must be greater than or equal to 0", violation.getMessage());

        //checking that age never changed
        assertEquals(30, exampleUser.getAge());

        verify(userRepository, never()).findById(exampleUser.getId());
        verify(userRepository, never()).save(exampleUser);
    }

    @Test
    public void updateUserInfoTest_mixedCase_shouldCastToLowerCase() {
        // mixed case we want to make sure that everything is cast to lowercase correctly
        User exampleUser = createExampleUser();

        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.of(exampleUser));


        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setWeight(BigDecimal.valueOf(67.5));
        userUpdateDto.setEmail("UPDATED_email@GMAIL.COm");
        userUpdateDto.setUsername("UPDATed_UseRNAme");
        userUpdateDto.setMetricSystem(true);
        userUpdateDto.setAge(46);
        userUpdateDto.setHeight(BigDecimal.valueOf(70.0));

        boolean result = userService.updateUserInfo(exampleUser.getId(), userUpdateDto);

        assertTrue(result);
        assertEquals(46, exampleUser.getAge());
        assertEquals("updated_username", exampleUser.getUsername());
        assertTrue(exampleUser.isMetricSystem());
        assertEquals(BigDecimal.valueOf(70.0), exampleUser.getHeight());
        assertEquals("updated_email@gmail.com", exampleUser.getEmail());
        assertEquals(BigDecimal.valueOf(67.5),exampleUser.getWeight());
        verify(userRepository, times(1)).findById(exampleUser.getId());
        verify(userRepository, times(1)).save(exampleUser);
    }

    @Test
    public void updateUserInfoTest_invalidWeight_shouldViolateConstraint() {
        //this will also be handled by validator but is still covered in the method as well
        User exampleUser = createExampleUser();

        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.of(exampleUser));


        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setWeight(BigDecimal.valueOf(-78.0));

        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(userUpdateDto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<UserUpdateDto> violation = violations.iterator().next();

        assertEquals("must be greater than or equal to 0.0", violation.getMessage());

        //checking that weight never changed
        assertEquals(BigDecimal.valueOf(170.0), exampleUser.getWeight());

        verify(userRepository, never()).findById(exampleUser.getId());
        verify(userRepository, never()).save(exampleUser);
    }

    @Test
    public void updateUserInfoTest_invalidHeight_shouldViolateConstraint() {
        //this will also be handled by validator but is still covered in the method as well
        User exampleUser = createExampleUser();

        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.of(exampleUser));


        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setHeight(BigDecimal.valueOf(-78.0));

        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(userUpdateDto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<UserUpdateDto> violation = violations.iterator().next();

        assertEquals("must be greater than or equal to 0.0", violation.getMessage());

        //checking that height never changed
        assertEquals(BigDecimal.valueOf(70.0), exampleUser.getHeight());

        verify(userRepository, never()).findById(exampleUser.getId());
        verify(userRepository, never()).save(exampleUser);
    }

    @Test
    public void getUserInfoTest_Normal_Success() {
        //test scenario where a user actually exists in the db, should
        //return the user object
        User exampleUser = createExampleUser();

        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.of(exampleUser));

        Optional<User> foundUser = userService.getUserInfo(exampleUser.getId());

        assertEquals(1L, foundUser.get().getId());
        assertEquals("Test User", foundUser.get().getFullName());
        assertEquals("test@gmail.com", foundUser.get().getEmail());
        assertEquals("password", foundUser.get().getPassword());
        assertEquals("test_user", foundUser.get().getUsername());
        assertEquals(BigDecimal.valueOf(170.0), foundUser.get().getWeight());
        assertEquals(BigDecimal.valueOf(70.0), foundUser.get().getHeight());
        assertFalse(foundUser.get().isMetricSystem());

        verify(userRepository, times(1)).findById(exampleUser.getId());

    }

    @Test
    public void getUserInfoTest_userNotFound_shouldThrowException() {
        //test scenario where user id is not found in the repository
        //we want to throw an exception in this case
        User exampleUser = createExampleUser();

        when(userRepository.findById(exampleUser.getId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserInfo(exampleUser.getId());
        });

        assertEquals("User with that id is not found", exception.getMessage());

        verify(userRepository, times(1)).findById(exampleUser.getId());
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
        user.setAge(30);

        return user;
    }
}
