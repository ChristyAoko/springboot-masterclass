package com.christy.springboot_masterclass;

import jakarta.validation.constraints.NotBlank;

public class StudentUpdateDTO {

    // Validated data for a PUT request
    @NotBlank(message = "Name cannot be empty!")
    public String name;

    @NotBlank(message = "Course cannot be empty!")
    public String course;

    @NotBlank(message = "Level cannot be empty!")
    public String level;
}
