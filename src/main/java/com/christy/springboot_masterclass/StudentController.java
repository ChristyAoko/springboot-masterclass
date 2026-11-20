package com.christy.springboot_masterclass;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


// StudentController – receives requests from users
@RestController // Provides REST API functionality
public class StudentController {

    private final StudentService studentService;

    // Controller accesses to the Service.
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // http://localhost:8081/students
    @PostMapping("/students") // CREATE METHOD
    // input validation - using @Valid
    public ResponseEntity<String> addStudent(
            @Valid @RequestBody StudentRequestDTO request) { // Receives and validates the incoming JSON

        Student savedStudent = studentService.createStudent(request); // Saves it via the service, and the database generates the ID

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedStudent.id)
                .toUri();

        return ResponseEntity
                .created(location)
                .body("Student " + savedStudent.name + " added!"); // Sets HTTP Status to 201 Created
    }

    // http://localhost:8081/students - sort by name, A to Z
    @GetMapping("/students")
    public Page<Student> getAllStudents(
            // http://localhost:8081/students?page=0&size=3 --> requests the 1st page with up to 3 students
            // http://localhost:8081/students?page=1&size=3 --> requests the next page
            @RequestParam(defaultValue = "0") int page,  // 0 - start with the first page, pagination starts at 0
            @RequestParam(defaultValue = "3") int size,   // 3 - return up to 3 students per page
            // http://localhost:8081/students?sortBy=name
            @RequestParam(defaultValue = "name") String sortBy,  // http://localhost:8081/students?sortBy=course - sort by course, A to Z
            @RequestParam(defaultValue = "asc") String direction) {

        return studentService.getAllStudents(page, size, sortBy, direction);
    }

    // http://localhost:8081/students/1
    @GetMapping("/students/{id}")
    public Student getStudentById(@PathVariable int id) {
        return studentService.getStudentById(id);
    }

    // http://localhost:8081/students/1/summary
    @GetMapping("/students/{id}/summary")
    public StudentSummaryDTO getStudentSummary(@PathVariable int id) {
        return studentService.getStudentSummary(id);
    }

    // http://localhost:8081/students/1
    @PutMapping("/students/{id}") // UPDATE METHOD
    public ResponseEntity<String> updateStudent(
            @PathVariable int id,
            @Valid @RequestBody StudentUpdateDTO request) {

        studentService.updateStudent(id, request);

        return ResponseEntity.ok("Student " + id + " updated!");
    }

    // http://localhost:8081/students/1
    @PatchMapping("/students/{id}") // UPDATE METHOD
    public ResponseEntity<String> patchStudent(
            @PathVariable int id,
            @RequestBody StudentPatchDTO patchData) {

        studentService.patchStudent(id, patchData);

        return ResponseEntity.ok("Student " + id + " updated!");
    }

    // http://localhost:8081/students/1
    @DeleteMapping("/students/{id}") // DELETE METHOD
    public ResponseEntity<String> deleteStudent(@PathVariable int id) {

        studentService.deleteStudent(id);

        return ResponseEntity.ok("Student " + id + " deleted!");
    }

}