package com.christy.springboot_masterclass;

import jdk.dynalink.linker.LinkerServices;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// StudentRepository – communicates with the database
public interface StudentRepository extends JpaRepository<Student, Integer> {

}

//
//    // Default CRUD methods are already available:
//    // - findAll()
//    // - findById(Integer id)
//    // - save(Student student)
//    // - deleteById(Integer id)
//
//
//    // CUSTOM QUERY METHODS:
//    // Find all students by course --> SELECT * FROM students WHERE course = 'Java';
//    List<Student> findByCourse(String course);
//
//    // Find all students by level --> SELECT * FROM students WHERE level = 'Beginner';
//    List<Student> findByLevel(String level);
//
//    // Find students by both course and level --> SELECT * FROM students WHERE course = 'Java' AND level = 'Beginner';
//    List<Student> findByCourseAndLevel(String course, String level);
//
//    List<Student> findByEmail(String email);
//}
