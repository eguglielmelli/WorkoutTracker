package com.eguglielmelli.controllers;

import com.eguglielmelli.dtos.UserDto;
import com.eguglielmelli.entities.User;
import com.eguglielmelli.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void createUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setFullName("Test user");
        userDto.setEmail("test@gmail.com");
        userDto.setPassword("password");
        userDto.setMetricSystem(true);
        userDto.setHeight(BigDecimal.valueOf(75.0));
        userDto.setWeight(BigDecimal.valueOf(150.0));
        userDto.setAge(30);
        userDto.setUsername("test_user");

        User user = new User("Test User", "test_user", "encodedPassword","test@gmail.com",30,BigDecimal.valueOf(150.0), BigDecimal.valueOf(75.0),
                true,false);

        when(userService.createUser(any(UserDto.class))).thenReturn(user);

        mockMvc.perform(post("/api/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"fullName\": \"Test User\", \"username\": \"test_user\", \"password\": \"password\", \"email\": \"test@gmail.com\", \"age\": 30, \"metricSystem\": false, \"height\": 70, \"weight\": 175 }"))
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
    public void deleteUser() throws Exception {
        Long userId = 1L;

        when(userService.deleteUser(userId)).thenReturn(true);

        mockMvc.perform(delete("/api/users/{id}/delete",userId)).andExpect(status().isOk());

        when(userService.deleteUser(userId)).thenReturn(false);

        mockMvc.perform(delete("/api/users/{id}/delete",userId)).andExpect(status().isNotFound());

    }

    @Test
    public void changeWeight() {

    }

}
