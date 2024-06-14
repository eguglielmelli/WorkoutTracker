package com.eguglielmelli.service;
import com.eguglielmelli.dtos.UserDto;
import com.eguglielmelli.entities.User;
import com.eguglielmelli.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;
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
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
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

        //validate the given information
        validateUserBeforeCreation(userDto);

        BigDecimal height = Optional.ofNullable(userDto.getHeight()).orElse(BigDecimal.ZERO);
        BigDecimal weight = Optional.ofNullable(userDto.getWeight()).orElse(BigDecimal.ZERO);

        User user = new User(
                userDto.getFullName(),userDto.getUsername(),userDto.getPassword(),
                userDto.getEmail(),userDto.getAge(), weight, height,
                userDto.isMetricSystem(),false
        );
        //VERY IMPORTANT: encode user password before sending to the repository
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(encodedPassword);

        return userRepository.save(user);
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
     * Gives user opportunity to switch measurement system if they like (i.e. from metric to imperial)
     * @param id of the user
     * @param measurementSystem boolean: true for metric, false for imperial
     * @return true if correctly set, else false
     */
    @Transactional
    public boolean changeMeasurementSystem(Long id, boolean measurementSystem) {
        return updateUser(id, user -> user.setMetricSystem(measurementSystem));
    }

    /**
     * Gives user the opportunity to change their weight
     * such as if they decided to forego during profile setup or if they
     * just want to change it
     * @param id of user
     * @param weight of user
     * @return true if successfully saved, otherwise false
     */
    @Transactional
    public boolean changeWeight(Long id, BigDecimal weight) {
        if(weight == null || weight.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Weight must be greater than or equal to 0");
        }
        BigDecimal roundedWeight = weight.setScale(1, RoundingMode.HALF_UP);
        return updateUser(id, user -> user.setWeight(roundedWeight));
    }

    /**
     * Gives user the opportunity to change their height
     * @param id of user
     * @param height new height they enter
     * throws exception if user enters null or negative weight
     * @return true if height updated, false if not
     */
    @Transactional
    public boolean changeHeight(Long id, BigDecimal height) {
        if(height == null || height.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Height must be greater than or equal to 0");
        }

        BigDecimal roundedHeight = height.setScale(1, RoundingMode.HALF_UP);
        return updateUser(id, user -> user.setHeight(roundedHeight));
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

        Integer age = Optional.ofNullable(userDto.getAge()).orElse(0);
        if(age < 0) {
            throw new IllegalArgumentException("Age must be greater than 0.");
        }
    }

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
