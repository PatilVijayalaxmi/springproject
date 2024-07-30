package com.example.springproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springproject.dto.StudentDetails;

public interface StudentRepository  extends JpaRepository<StudentDetails,Integer> {

}
