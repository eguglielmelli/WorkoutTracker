package com.eguglielmelli.controllers;
import com.eguglielmelli.dtos.UserDto;
import com.eguglielmelli.dtos.UserUpdateDto;
import com.eguglielmelli.entities.User;
import com.eguglielmelli.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid UserDto userDto) {
        User createdUser = userService.createUser(userDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<User> deleteUser(@PathVariable Long id) {
        boolean isDeleted = userService.deleteUser(id);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<User> updateUserInfo(@PathVariable Long id, @RequestBody UserUpdateDto userUpdateDto) {
        boolean updated = userService.updateUserInfo(id, userUpdateDto);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/info")
    public ResponseEntity<?> getUserInfo(@PathVariable Long id) {
        Optional<User> foundUser = userService.getUserInfo(id);
        return new ResponseEntity<>(foundUser,HttpStatus.OK);
    }
}
