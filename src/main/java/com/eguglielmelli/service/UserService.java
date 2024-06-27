package com.eguglielmelli.service;
import com.eguglielmelli.dtos.UserDto;
import com.eguglielmelli.dtos.UserUpdateDto;
import com.eguglielmelli.entities.User;
import com.eguglielmelli.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Service class that will handle our CRUD operations, methods will be called in the corresponding
 * controller
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, Validator validator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

    }

    /**
     * Method to create our user, validation will be done in
     * validateUserBeforeCreation() method
     * @param userDto to hold user data being transferred
     */
    @Transactional
    public User createUser(@Valid UserDto userDto) {
        if(userDto == null) {
            throw new IllegalArgumentException("UserDto cannot be null");
        }

        //validate the given information
        validateUserBeforeCreation(userDto);

        BigDecimal height = Optional.ofNullable(userDto.getHeight()).orElse(BigDecimal.ZERO);
        BigDecimal weight = Optional.ofNullable(userDto.getWeight()).orElse(BigDecimal.ZERO);

        User user = new User(
                userDto.getFullName().toLowerCase(),userDto.getUsername().toLowerCase(),userDto.getPassword(),
                userDto.getEmail().toLowerCase(),userDto.getAge(), weight, height,
                userDto.isMetricSystem(),false
        );
        //VERY IMPORTANT: encode user password before sending to the repository
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    /**
     * This method will handle all updates to user info, EXCEPT PASSWORD (this will be handled in a different
     * method since it is more complex)
     * Users do not have to include all information, they can choose what to include
     * but the validator will still make sure requirements are abided by (i.e valid email format etc)
     * @param id of user
     * @param userUpdateDto user data transfer object containing all of the new info
     * @return true if updated, false otherwise
     */
    @Transactional
    public boolean updateUserInfo(Long id, @Valid UserUpdateDto userUpdateDto) {
        return updateUser(id, user ->  {
            if(userUpdateDto.getEmail() != null && !userUpdateDto.getEmail().isEmpty()) {
                user.setEmail(userUpdateDto.getEmail().toLowerCase());
            }
            if(userUpdateDto.getAge() > 0) {
                user.setAge(userUpdateDto.getAge());
            }
            if(userUpdateDto.getUsername() != null && !userUpdateDto.getUsername().isEmpty()) {
                user.setUsername(userUpdateDto.getUsername().toLowerCase());
            }
            if(userUpdateDto.getHeight() != null && userUpdateDto.getHeight().compareTo(BigDecimal.ZERO) > 0) {
                user.setHeight(userUpdateDto.getHeight().setScale(1,RoundingMode.HALF_UP));
            }
            if(userUpdateDto.getWeight() != null && userUpdateDto.getWeight().compareTo(BigDecimal.ZERO) > 0) {
                user.setWeight(userUpdateDto.getWeight().setScale(1, RoundingMode.HALF_UP));
            }
            if(userUpdateDto.getPassword() != null && !userUpdateDto.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
            }
            if(userUpdateDto.isMetricSystem() != user.isMetricSystem()) {
                user.setMetricSystem(userUpdateDto.isMetricSystem());
            }
        });
    }

    /**
     * Soft delete the user from the database if they choose
     * we will just mark isDeleted() to be true, save to repository and return
     * @param id user's id
     * @return true if soft delete and false if user is not found
     */
    @Transactional
    public boolean deleteUser(Long id) {
        return updateUser(id, user -> user.setDeleted(true));
    }

    /**
     * Method to get user's info such as name, password etc
     * This will be displayed in a menu where users can adjust info as needed
     * @param id of user
     * @return user object corresponding to the given id
     */
    @Transactional
    public Optional<User> getUserInfo(Long id) {
        return Optional.ofNullable(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with that id is not found")));
    }


    /**
     * Validation method to make sure user data is acceptable before
     * saving it to the user repository in createUser()
     * @param userDto to carry user data being transferred
     */
    private void validateUserBeforeCreation(UserDto userDto) {

        if(userDto.getFullName() == null || userDto.getFullName().isEmpty()) {
            throw new IllegalArgumentException("Name must not be empty or null.");
        }
        if(userDto.getUsername() == null || userDto.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username must not be empty or null.");
        }

        //username must be unique according to our annotations, so we check here
        if(userRepository.findByusername(userDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("A user with this username already exists.");
        }

        if(userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email must not empty or null.");
        }

        //need to do the same thing as above with email, spring should check format
        if(userRepository.findByemail(userDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("A user with this email already exists.");
        }

        if(userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password must not be empty or null.");
        }

        Integer age = Optional.of(userDto.getAge()).orElse(0);
        if(age < 0) {
            throw new IllegalArgumentException("Age must be greater than 0.");
        }
    }

    /**
     * Method allow for cleaning up duplicate code in deleting and updating various attributes
     * of user class
     * @param id of user
     * @param updateAction which parameter will be passed into the prospective methods
     * @return true if success, false otherwise
     */
    private boolean updateUser(Long id, Consumer<User> updateAction) {
        Optional<User> foundUser = userRepository.findById(id);
        if(foundUser.isPresent()) {
            User user = foundUser.get();
            updateAction.accept(user);
            userRepository.save(user);
            return true;
        }
        return false;
    }

}
