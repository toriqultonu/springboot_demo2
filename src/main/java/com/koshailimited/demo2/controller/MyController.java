package com.koshailimited.demo2.controller;

import com.koshailimited.demo2.Service.CourseService;
import com.koshailimited.demo2.entities.Courses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MyController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/home")
    public String home(){
        return "Welcome to courses application";
    }

    //get the courses
    @GetMapping("/courses")
    public List<Courses> getCourses(){
        return this.courseService.getCourses();
    }
}
