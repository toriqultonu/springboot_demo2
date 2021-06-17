package com.koshailimited.demo2.controller;

import com.koshailimited.demo2.Service.CourseService;
import com.koshailimited.demo2.entities.Courses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/courses/{courseId}")
    public Courses getCourse(@PathVariable String courseId){

        return this.courseService.getCourse(Long.parseLong(courseId));
    }

    @PostMapping(path = "/courses", consumes = "application/json")
    public Courses addCourse(@RequestBody Courses course){

        return this.courseService.addCourse(course);
    }
}
