package com.eguglielmelli.controllers;

import com.eguglielmelli.config.SecurityConfig;
import com.eguglielmelli.dtos.UserDto;
import com.eguglielmelli.dtos.UserUpdateDto;
import com.eguglielmelli.entities.User;
import com.eguglielmelli.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createUser_Success() throws Exception {
        //basic test case that should also easily succeed
        UserDto userDto = new UserDto();
        userDto.setFullName("Test User");
        userDto.setEmail("test@gmail.com");
        userDto.setPassword("password");
        userDto.setMetricSystem(true);
        userDto.setHeight(BigDecimal.valueOf(75.0));
        userDto.setWeight(BigDecimal.valueOf(150.0));
        userDto.setAge(30);
        userDto.setUsername("test_user");

        User user = new User("Test User", "test_user", "encodedPassword", "test@gmail.com", 30, BigDecimal.valueOf(150.0), BigDecimal.valueOf(75.0), true, false);

        when(userService.createUser(any(UserDto.class))).thenReturn(user);

        mockMvc.perform(post("/api/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"fullName\": \"Test User\", \"username\": \"test_user\", \"password\": \"password\", \"email\": \"test@gmail.com\", \"age\": 30, \"metricSystem\": true, \"height\": 75, \"weight\": 150 }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.username").value("test_user"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.metricSystem").value(true))
                .andExpect(jsonPath("$.height").value(75))
                .andExpect(jsonPath("$.weight").value(150));
    }

    @Test
    public void deleteUser_Success() throws Exception {
        Long userId = 1L;

        when(userService.deleteUser(userId)).thenReturn(true);

        mockMvc.perform(delete("/api/users/{id}/delete", userId))
                .andExpect(status().isOk());

        when(userService.deleteUser(userId)).thenReturn(false);

        mockMvc.perform(delete("/api/users/{id}/delete", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateUserInfoTest_Success() throws Exception {
        //basic update test that should easily succeed
        Long userId = 1L;
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setEmail("update_user@gmail.com");
        userUpdateDto.setAge(35);

        when(userService.updateUserInfo(any(Long.class), any(UserUpdateDto.class))).thenReturn(true);
        String userUpdateDtoJson = objectMapper.writeValueAsString(userUpdateDto);
        mockMvc.perform(put("/api/users/{id}/update", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userUpdateDtoJson))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserInfoTest_Success() throws Exception {
        Long userId = 1L;

        // Create a user object to be returned by the service
        User user = new User();
        user.setId(userId);
        user.setFullName("Test User");
        user.setUsername("test_user");
        user.setEmail("test@gmail.com");
        user.setAge(30);
        user.setWeight(BigDecimal.valueOf(150.0));
        user.setHeight(BigDecimal.valueOf(75.0));
        user.setMetricSystem(true);
        user.setDeleted(false);

        when(userService.getUserInfo(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/{id}/info", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.username").value("test_user"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.weight").value(150.0))
                .andExpect(jsonPath("$.height").value(75.0))
                .andExpect(jsonPath("$.metricSystem").value(true))
                .andExpect(jsonPath("$.deleted").value(false));
    }

}
