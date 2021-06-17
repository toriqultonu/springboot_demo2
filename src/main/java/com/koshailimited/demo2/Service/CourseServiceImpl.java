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

    @Override
    public Courses getCourse(long courseId) {

        Courses c = null;

        for(Courses courses:list){
            if(courses.getId() == courseId){
                c = courses;
                return  c;
            }
        }
        return null;
    }

    @Override
    public Courses addCourse(Courses course) {
        list.add(course);
        return course;
    }


}
