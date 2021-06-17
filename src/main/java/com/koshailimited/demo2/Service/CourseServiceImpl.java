package com.koshailimited.demo2.Service;

import com.koshailimited.demo2.entities.Courses;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService{

    List<Courses> list;

    public CourseServiceImpl(){
        list = new ArrayList<>();
        list.add(new Courses(1803121, "Flutter", "The basics of flutter."));
        list.add(new Courses(1803122, "Springboot","Creating rest api using rest api"));
    }

    @Override
    public List<Courses> getCourses() {
        return list;
    }
}
