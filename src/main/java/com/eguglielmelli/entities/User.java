package com.eguglielmelli.entities;

import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotNull
    @NotEmpty
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotNull
    @NotEmpty
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotNull
    @NotEmpty
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @Email
    @NotEmpty
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Min(0)
    @Column(name = "age")
    private int age;

    @Column(name = "weight", scale = 1, precision = 5)
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal weight;

    @Column(name = "height", scale = 1, precision = 5)
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal height;

    @NotNull
    @Column(name = "metric_system", nullable = false)
    private boolean metricSystem = false;

    @NotNull
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;


    public User() {
    }

    public User(String fullName, String username, String password,
                String email, int age, BigDecimal weight, BigDecimal height,
                boolean metricSystem, boolean isDeleted) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.metricSystem = metricSystem;
        this.isDeleted = isDeleted;
    }
    //Strictly for testing purposes, otherwise, database will assign id
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
