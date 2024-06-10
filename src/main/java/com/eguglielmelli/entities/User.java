package com.eguglielmelli.entities;

import javax.validation.constraints.NotNull;

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
    @Column(name = "full_name",nullable = false)
    private String fullName;

    @NotNull
    @Column(name = "username", nullable = false)
    private String username;

    @NotNull
    @Column(name = "password",nullable = false)
    private String password;

    @NotNull
    @Column(name = "email",nullable = false,unique = true)
    private String email;

    @Column(name = "age")
    private int age;

    @Column(name = "weight",scale = 1, precision = 5)
    private BigDecimal weight;

    @Column(name = "height", scale = 1, precision = 5)
    private BigDecimal height;

    @Column(name = "metric_system",nullable = false)
    private boolean metricSystem;


    public User(String fullName, String username, String password,
                String email, int age, BigDecimal weight, BigDecimal height, boolean metricSystem) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.metricSystem = metricSystem;
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
}
