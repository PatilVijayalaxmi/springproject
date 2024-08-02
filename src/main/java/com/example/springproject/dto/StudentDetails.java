package com.example.springproject.dto;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;

 @Data
@Entity
public class StudentDetails {
     @Id
    @GeneratedValue(generator = "x")
    @SequenceGenerator(name = "x",initialValue = 1001,allocationSize = 1)
    private int id;
    private  String name;
    private String standard;//class
    private LocalDate dob;
    private Long mobilenumber;
    private int subject1;
    private int subject2;
    private int subject3;
    private int subject4;
    private int subject5;
    private int subject6;

   private String picture;
}

