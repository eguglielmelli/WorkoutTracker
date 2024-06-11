package com.eguglielmelli.service;
import com.eguglielmelli.dtos.UserDto;
import com.eguglielmelli.entities.User;
import com.eguglielmelli.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

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
    public void createUser(UserDto userDto) {

        //validate the given information
        validateUserBeforeCreation(userDto);

        BigDecimal height = Optional.ofNullable(userDto.getHeight()).orElse(BigDecimal.ZERO);
        BigDecimal weight = Optional.ofNullable(userDto.getWeight()).orElse(BigDecimal.ZERO);

        User user = new User(
                userDto.getFullName(),userDto.getUsername(),userDto.getPassword(),
                userDto.getEmail(),userDto.getAge(), weight, height,
                userDto.isMetricSystem(),false
        );
        userRepository.save(user);
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
        Optional<User> foundUserWithUsername = userRepository.findByusername(userDto.getUsername());

        if(foundUserWithUsername.isPresent()) {
            throw new IllegalArgumentException("A user with this username already exists.");
        }

        if(userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email must not empty or null.");
        }

        //need to do the same thing as above with email, spring should check format
        Optional<User> foundUserWithEmail = userRepository.findByemail(userDto.getEmail());

        if(foundUserWithEmail.isPresent()) {
            throw new IllegalArgumentException("A user with this email already exists.");
        }

        if(userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password must not be empty or null.");
        }

        //VERY IMPORTANT: encode user password because after this method
        //it is going to the repository
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(encodedPassword);

        Integer age = Optional.ofNullable(userDto.getAge()).orElse(0);
        if(age < 0) {
            throw new IllegalArgumentException("Age must be greater than 0.");
        }
    }
}
