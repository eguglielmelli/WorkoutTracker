package com.eguglielmelli.controllers;
import com.eguglielmelli.dtos.UserDto;
import com.eguglielmelli.entities.User;
import com.eguglielmelli.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

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
    @PutMapping("/{id}/changeWeight")
    public ResponseEntity<User> changeWeight(@PathVariable Long id, @RequestBody Map<String, BigDecimal> requestBody) {
        BigDecimal weight = requestBody.get("weight");
        boolean weightUpdated = userService.changeWeight(id, weight);

        return weightUpdated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    @PutMapping("/{id}/changeHeight")
    public ResponseEntity<User> changeHeight(@PathVariable Long id, @RequestBody Map<String,BigDecimal> requestBody) {
        BigDecimal height = requestBody.get("height");
        boolean heightUpdated = userService.changeHeight(id, height);

        return heightUpdated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

}
