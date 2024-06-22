package com.eguglielmelli.repositories;

import com.eguglielmelli.config.SecurityConfig;
import com.eguglielmelli.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(SecurityConfig.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setFullName("Test User");
        user.setAge(30);
        user.setWeight(BigDecimal.valueOf(175.0));
        user.setHeight(BigDecimal.valueOf(75.0));
        user.setEmail("test@gmail.com");
        user.setDeleted(false);
        user.setMetricSystem(false);
        user.setPassword("password");
        user.setUsername("username");

        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    public void whenFindByEmail_UserFound_thenReturnUser() {
        //basic case should return the user
        Optional<User> foundUser = userRepository.findByusername(user.getUsername());

        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void whenFindByEmail_UserNotFound_thenReturnEmpty() {
        //shouldn't return anything since no user with that email
        Optional<User> nonExistentUser = userRepository.findByemail("nonexistentemail");

        assertThat(nonExistentUser.isEmpty()).isTrue();
    }

    @Test
    public void whenFindByUserName_UserFound_thenReturnUser() {
        //another normal case where user should be found
        Optional<User> foundUser = userRepository.findByusername(user.getUsername());

        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void whenFindByUsername_UserNotFound_thenReturnEmpty() {
        //shouldn't return anything because that username doesn't exist
        Optional<User> nonExistentUser = userRepository.findByusername("UsernameDoesn'tExist");

        assertThat(nonExistentUser.isEmpty()).isTrue();
    }


}
