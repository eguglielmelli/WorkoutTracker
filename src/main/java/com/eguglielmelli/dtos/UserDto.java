package com.eguglielmelli.dtos;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Class represents user data object
 * Used when a user is being created, this will pack up their info and send it to the database
 */
public class UserDto {

    @NotNull
    @NotEmpty
    private String fullName;

    @NotNull
    @NotEmpty
    private String username;

    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    @Email
    @NotEmpty
    private String email;

    @Min(0)
    private int age;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal weight;


    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal height;

    private boolean metricSystem = false;

    public UserDto() {};

    public UserDto(String fullName, String username, String email, String password, int age, BigDecimal height, BigDecimal weight, boolean metricSystem) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.metricSystem = metricSystem;
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public boolean isMetricSystem() {
        return metricSystem;
    }

    public void setMetricSystem(boolean metricSystem) {
        this.metricSystem = metricSystem;
    }
}
