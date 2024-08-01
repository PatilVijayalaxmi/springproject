package com.example.springproject.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springproject.dto.MyUSer;


public interface MyUserRepository extends JpaRepository<MyUSer,Integer> {

    boolean existsByEmail(String email);

    MyUSer findByEmail(String email);



}
