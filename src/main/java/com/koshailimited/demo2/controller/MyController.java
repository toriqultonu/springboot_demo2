package com.koshailimited.demo2.controller;

import com.koshailimited.demo2.entities.Courses;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @GetMapping("/home")
    public String home(){
        return "Welcome to courses application";
    }

    //get the courses
    public List<Courses> getCourses(){

    }
}
