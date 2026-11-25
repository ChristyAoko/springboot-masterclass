package com.christy.springboot_masterclass;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// StudentRepository – communicates with the database
public interface StudentRepository // interface - java structure that defines methods that a class can use
        extends JpaRepository<Student, Integer> {

    List<Student> findByCourse(String course);  // Method Declaration - Spring Data JPA generates SQL Query based on method name

    List<Student> findByCourseAndLevel(String course, String level); // Retrieves students whose course and level both match the supplied values

    List<Student> findByCourseOrderByNameAsc(String course); // Order in ascending order the students whose course match the supplied values

    List<Student> findByNameContaining(String keyword); // Searches for records where the specified text appears anywhere within the field

    List<Student> findByNameContainingIgnoreCase(String keyword); // Makes the search more flexible and predictable i.e. case-insensitive

    boolean existsByName(String name); // Checks if a matching record exists

    long countByCourse(String course); // Counts all matching records

    long deleteByCourse(String course); // Deletes matching records and reports how many were deleted

}