package com.christy.springboot_masterclass;

import jdk.dynalink.linker.LinkerServices;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// StudentRepository – communicates with the database
public interface StudentRepository extends JpaRepository<Student, Integer> {

}