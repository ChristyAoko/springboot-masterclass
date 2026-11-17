package com.christy.springboot_masterclass;

import jakarta.validation.constraints.NotBlank;

public class StudentRequestDTO {

    // Receives validated request POST data
    @NotBlank(message = "Name cannot be empty!")
    public String name;

    @NotBlank(message = "Course cannot be empty!")
    public String course;

    @NotBlank(message = "Level cannot be empty!")
    public String level;
}
