package com.eguglielmelli.dtos;
import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * This class is used for when a user is updating their info
 * Similar to the regular dto, will pack up the information and send it back to the DB
 */
public class UserUpdateDto {

    @Email
    private String email;


    private String password;

    @Size(min = 1, message = "Username must not be empty")
    private String username;


    private boolean isMetricSystem;

    @Min(0)
    private int age;

    @DecimalMin("0.0")
    private BigDecimal weight;

    @DecimalMin("0.0")
    private BigDecimal height;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isMetricSystem() {
        return isMetricSystem;
    }

    public void setMetricSystem(boolean metricSystem) {
        isMetricSystem = metricSystem;
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
}
