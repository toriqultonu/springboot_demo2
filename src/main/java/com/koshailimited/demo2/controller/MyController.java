package com.koshailimited.demo2.controller;

import com.koshailimited.demo2.Service.CourseService;
import com.koshailimited.demo2.entities.Courses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MyController {

    @Autowired
    private CourseService courseService;

    @GetMapping(path = "/home")
    public String home(){
        return "Welcome to courses application";
    }

    //get the courses
    @GetMapping(path = "/courses")
    public List<Courses> getCourses(){
        return this.courseService.getCourses();
    }

    @GetMapping(path = "/courses/{courseId}")
    public Courses getCourse(@PathVariable String courseId){

        return this.courseService.getCourse(Long.parseLong(courseId));
    }
    //add courses
    @PostMapping(path = "/courses", consumes = "application/json")
    public Courses addCourse(@RequestBody Courses course){

        return this.courseService.addCourse(course);
    }
    //update courses
    @PutMapping(path = "/courses")
    public Courses updateCourses(@RequestBody Courses course){
        return this.courseService.updateCourse(course);
    }

    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<HttpStatus> deleteCourse(@PathVariable String courseId){
        try{
            this.courseService.deleteCourse(Long.parseLong(courseId));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
