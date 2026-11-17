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

//@Table(name = "students")  // 👈 force JPA to use 'students'
//public class Student {
//    private int id;
//    private String name;
//    private String course;
//    private String level;
//
//    private String email;
//
//    // Getters
//    public int getId() {
//        return id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getCourse() {
//        return course;
//    }
//
//    public String getLevel() {
//        return level;
//    }
//
//    // Setters
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setCourse(String course) {
//        this.course = course;
//    }
//
//    public void setLevel(String level) {
//        this.level = level;
//    }
//}
