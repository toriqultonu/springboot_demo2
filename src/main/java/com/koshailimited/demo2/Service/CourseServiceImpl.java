package com.koshailimited.demo2.Service;

import com.koshailimited.demo2.entities.Courses;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public Courses updateCourse(Courses course) {
        list.forEach(e ->{
            if(e.getId() == course.getId()){
                e.setTitle(course.getTitle());
                e.setDescription(course.getDescription());
            }
        });
        return course;
    }

    @Override
    public void deleteCourse(long parseLong) {
        list = this.list.stream().filter(e-> e.getId() != parseLong).collect(Collectors.toList());
    }


}
