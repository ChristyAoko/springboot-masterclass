package com.christy.springboot_masterclass;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class HelloController {

    // URL: http://localhost:8081
    @GetMapping("/")
    public String hello() {

        return "Hello, Spring!";
    }

    // URL: http://localhost:8081/greet?name=Aoko
    @GetMapping("/greet")
    public String greet(@RequestParam String name) {

        return "Hello, " + name + "!";
    }

    // URL: http://localhost:8081/double?number=7
    @GetMapping("/double")
    public String doubleNo(@RequestParam int number) {
        int result = number * 2;
        return "Double is " + result;
    }

    // URL: http://localhost:8081/square?number=4
    @GetMapping("/square")
    public String square(@RequestParam int number) {
        int result = number * number;
        return "Square is " + result;
    }

    // URL: http://localhost:8081/student - SINGLE STUDENT ENDPOINT
    @GetMapping("/student")
    public Student student() {

        Student student = new Student();

        student.name = "Christy";
        student.course = "Spring Boot";
        student.level = "Beginner";

        return student;
    }

    @GetMapping("/pupil")
    public String getStudent() {
        return "Student list";
    }

    // URL: http://localhost:8081/students/student
    @GetMapping("/hello/student-count")
    public String getStudentCount() {
        return "There are 10 students";
    }

}