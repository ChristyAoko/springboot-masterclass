package com.christy.springboot_masterclass;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity  // Tells JPA To Map This Java Class To A dB Table
@Table(name = "students") // 👈 force JPA to use 'students'
public class Student {

    @Id  // Marks 'id' As The Primary Key - PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // dB assigns a unique ID automatically when a new student is saved
    public int id;

    // input validation - using @NotBlank
    @NotBlank(message = "Name cannot be empty!") // Spring's new error message
    public String name;

    @NotBlank(message = "Course cannot be empty!")
    public String course;

    @NotBlank(message = "Level cannot be empty!")
    public String level;
}