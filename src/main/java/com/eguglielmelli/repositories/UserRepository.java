package com.eguglielmelli.repositories;
import com.eguglielmelli.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByusername(String username);
    Optional<User> findByemail(String email);
}
