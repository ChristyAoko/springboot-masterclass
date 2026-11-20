package com.christy.springboot_masterclass;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service // Tells Spring that this class is a Service component
public class StudentService {

    private final StudentRepository studentRepository; // References the repository, final - can only be assigned once

    public StudentService(StudentRepository studentRepository) { // Spring automatically supplies the repository when creating the service
        this.studentRepository = studentRepository;  // i.e. Constructor Injection
    }

    // Business Logic

    // Create and save a student
    public Student createStudent(StudentRequestDTO request) {

        Student student = new Student(); // Creates a new Student entity
        student.name = request.name; // Copy the validated requests into the entity/table
        student.course = request.course;
        student.level = request.level;

        return studentRepository.save(student); // Saves the entity through StudentRepository
    }
    public Page<Student> getAllStudents(
            int page, int size, String sortBy, String direction) {

        // http://localhost:8081/students?page=1&size=3&sortBy=name&direction=asc - combining pagination and sorting
        if (size < 1) {
            throw new IllegalArgumentException("Page size must be at least 1!"); // Size validation
        }

        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative!"); // Page number validation
        }

        if (!"id".equals(sortBy)
                && !"name".equals(sortBy)
                && !"course".equals(sortBy)
                && !"level".equals(sortBy)) {
            throw new IllegalArgumentException(
                    "Invalid sorting field: " + sortBy  // Sorting fields must be id, name, course, or level
            );
        }

        if (!"asc".equalsIgnoreCase(direction)
                && !"desc".equalsIgnoreCase(direction)) {
            throw new IllegalArgumentException(
                    "Direction must be 'asc' or 'desc'!"
            );
        }

        Pageable pageable = PageRequest.of(  // creates the pagination and sorting instructions
                page,                        // specifies which page to retrieve
                size,                        // specifies how many students to return
                Sort.by(
                        direction.equalsIgnoreCase("desc")
                                ? Sort.Direction.DESC  // sorts in descending order // http://localhost:8081/students?sortBy=name&direction=desc
                                : Sort.Direction.ASC,  // sorts in ascending order
                        sortBy
                )
        );

        return studentRepository.findAll(pageable);  // retrieves only the requested page from the database

    }

    // Retrieves a student
    public Student getStudentById(int id) {
        return studentRepository.findById(id) // Search for student by id, if found, returns Student object else throws exception
                .orElseThrow(() -> new StudentNotFoundException( // Exception handled by GlobalExceptionHandler
                        "Student " + id + " not found!"
                ));

    }

    // Retrieves a student and converts it into a summary DTO
    public StudentSummaryDTO getStudentSummary(int id) {

        Student student = getStudentById(id);

        StudentSummaryDTO dto = new StudentSummaryDTO(); // Creates a new DTO object
        dto.name = student.name; // Copies the student's name into the DTO
        dto.course = student.course; // Copies the student's course into the DTO

        return dto;
    }

    public void updateStudent(int id, StudentUpdateDTO request) {

        Student student = getStudentById(id);

        student.name = request.name; // Update name, course, level
        student.course = request.course;
        student.level = request.level;

        studentRepository.save(student);
    }

    public void patchStudent(int id, StudentPatchDTO patchData) {

        Student student = getStudentById(id);

        // Reject supplied fields that are blank and Update only fields included in the request
        if (patchData.course != null && patchData.course.isBlank()) {
            throw new IllegalArgumentException("Course cannot be blank!");
        }

        if (patchData.name != null && patchData.name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank!");
        }

        if (patchData.level != null && patchData.level.isBlank()) {
            throw new IllegalArgumentException("Level cannot be blank!");
        }

        // Update only fields included in the request
        if (patchData.course != null) {
            student.course = patchData.course;
        }

        if (patchData.name != null) {
            student.name = patchData.name;
        }

        if (patchData.level != null) {
            student.level = patchData.level;
        }

        studentRepository.save(student);

    }

    public void deleteStudent(int id) {

        Student student = getStudentById(id);

        studentRepository.delete(student);

    }

}