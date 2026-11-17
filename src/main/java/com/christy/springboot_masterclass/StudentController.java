package com.christy.springboot_masterclass;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// StudentController – receives requests from users
@RestController // Provides REST API functionality
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Shared List at Class Level - POST: Add Student & GET: Returns List of Students
//    private ArrayList<Student> students = new ArrayList<>();
//
//    private int nextId = 1;

    // http://localhost:8081/students
    @PostMapping("/students") // CREATE METHOD
    // input validation - using @Valid
    public ResponseEntity<String> addStudent(
            @Valid @RequestBody StudentRequestDTO request) { // Receives and validates the incoming JSON

        Student student = new Student(); // Creates the database entity
        student.name = request.name; // Copy the validated requests into the entity/table
        student.course = request.course;
        student.level = request.level;

        Student savedStudent = studentRepository.save(student); // Saves it, and the database generates the ID

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedStudent.id)
                .toUri();

        return ResponseEntity
                .created(location)
                .body("Student " + student.name + " added!");      // Sets HTTP Status to 201 OK
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
            @RequestParam(defaultValue = "asc") String direction
    )

            // http://localhost:8081/students?page=1&size=3&sortBy=name&direction=asc - combining pagination and sorting

    {
        if (size < 1) {
            throw new IllegalArgumentException("Page size must be at least 1!"); // Size validation
        }

        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative!"); // Page number validation
        }

        if (!sortBy.equals("id")
                && !sortBy.equals("name")
                && !sortBy.equals("course")
                && !sortBy.equals("level")) {
            throw new IllegalArgumentException(
                    "Invalid sorting field: " + sortBy // Sorting fields must be id, name, course, or level
            );
        }

        if(!direction.equalsIgnoreCase("asc")
            && !direction.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException(
                    "Direction must be 'asc' or 'desc'!"
            );
        }

        Pageable pageable = PageRequest.of(    // creates the pagination and sorting instructions
                page,                          // specifies which page to retrieve
                size,                          // specifies how many students to return
                Sort.by(
                        direction.equalsIgnoreCase("desc")
                                ? Sort.Direction.DESC // sorts in descending order // http://localhost:8081/students?sortBy=name&direction=desc
                                : Sort.Direction.ASC, // sorts in ascending order
                        sortBy
                )
        );

        return studentRepository.findAll(pageable);  // retrieves only the requested page from the database
    }


    // http://localhost:8081/students/1
    @GetMapping("/students/{id}")
    public Student getStudentById(@PathVariable int id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(
                        "Student " + id + " not found!"
                ));
    }

    // http://localhost:8081/students/1/summary
    @GetMapping("/students/{id}/summary")
    public StudentSummaryDTO getStudentSummary(@PathVariable int id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(
                        "Student " + id + " not found!"
                ));

        StudentSummaryDTO dto = new StudentSummaryDTO(); // Creates a new DTO object
        dto.name = student.name; // Copies the student's name into the DTO
        dto.course = student.course; // Copies the student's course into the DTO

        return dto;
    }

    // http://localhost:8081/students/1
    @PutMapping("/students/{id}") // UPDATE METHOD
    public ResponseEntity<String> updateStudent(
            @PathVariable int id,
            @Valid @RequestBody StudentUpdateDTO request) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException( // Exception handled by GlobalExceptionHandler
                        "Student " + id + " not found!"
                ));

        student.name = request.name; // Update name, course, level
        student.course = request.course;
        student.level = request.level;

        studentRepository.save(student);

        return ResponseEntity.ok("Student " + id + " updated!");
    }

    // http://localhost:8081/students/1
    @PatchMapping("/students/{id}") // UPDATE METHOD
    public ResponseEntity<String> patchStudent(
            @PathVariable int id,
            @RequestBody StudentPatchDTO patchData) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(
                        "Student " + id + " not found!"
                ));

        // Reject supplied fields that are blank
        if (patchData.course != null && patchData.course.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Course cannot be blank!");
        }

        if (patchData.name != null && patchData.name.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Name cannot be blank!");
        }

        if (patchData.level != null && patchData.level.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Level cannot be blank!");
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

        // Persist the changes to H2
        studentRepository.save(student);

        return ResponseEntity.ok("Student " + id + " updated!");
    }

    // http://localhost:8081/students/1
    @DeleteMapping("/students/{id}") // DELETE METHOD
    public ResponseEntity<String> deleteStudent(@PathVariable int id) {

        Student student = studentRepository.findById(id) // Search for student by id
                .orElseThrow(() -> new StudentNotFoundException( // If found, returns Student object else throws exception
                        "Student " + id + " not found!"  // Exception handled by GlobalExceptionHandler
                ));

        studentRepository.delete(student);  // If found, deletes and returns 200 OK

        return ResponseEntity.ok("Student " + id + " deleted!");
    }

}

// http://localhost:8081/students/course/Java
//    @GetMapping("/students/course/{course}")
//    public List<Student> getStudentsByCourse(@PathVariable String course) {
//        return studentRepository.findByCourse(course);
//    }

// http://localhost:8081/students/level/Beginner
//    @GetMapping("/students/level/{level}")
//    public List<Student> getStudentsByLevel(@PathVariable String level) {
//        return studentRepository.findByLevel(level);
//    }

// http://localhost:8081/students/Java/Beginner
//    @GetMapping("/students/{course}/{level}")
//    public List<Student> getStudentsByCourseAndLevel(@PathVariable String course, @PathVariable String level) {
//        return studentRepository.findByCourseAndLevel(course, level);
//    }

//    @GetMapping("/students/email/{email}")
//    public List<Student> getStudentsByEmail(@PathVariable String email) {
//        return studentRepository.findByEmail(email);
//    }

//    }

//    @DeleteMapping("/students/{id}") // DELETE METHOD
//    public String deleteStudent(@PathVariable int id) {
//        if (studentRepository.existsById(id)) {
//            studentRepository.deleteById(id);
//            return "Student deleted!";
//        }
//        return "Student not found!";
//    }