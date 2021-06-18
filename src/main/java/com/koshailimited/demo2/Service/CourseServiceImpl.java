package com.koshailimited.demo2.Service;

import com.koshailimited.demo2.entities.Courses;
import com.koshailimited.demo2.model.CourseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService{

//    List<Courses> list;

        @Autowired
        private CourseDao courseDao;

    public CourseServiceImpl(){

//        list = new ArrayList<>();
//        list.add(new Courses(1803121, "Flutter", "The basics of flutter."));
//        list.add(new Courses(1803122, "Springboot","Creating rest api using rest api"));

    }

    @Override
    public List<Courses> getCourses() {

        return courseDao.findAll();
    }

    @Override
    public Courses getCourse(long courseId) {

//        Courses c = null;
//
//        for(Courses courses:list){
//            if(courses.getId() == courseId){
//                c = courses;
//                return  c;
//            }
//        }
        return courseDao.getOne(courseId);
    }

    @Override
    public Courses addCourse(Courses course) {
//        list.add(course);
        courseDao.save(course);
        return course;
    }

    @Override
    public Courses updateCourse(Courses course) {
//        list.forEach(e ->{
//            if(e.getId() == course.getId()){
//                e.setTitle(course.getTitle());
//                e.setDescription(course.getDescription());
//            }
//        });
        courseDao.save(course);
        return course;
    }

    @Override
    public void deleteCourse(long parseLong) {
//        list = this.list.stream().filter(e-> e.getId() != parseLong).collect(Collectors.toList());
        Courses entity = courseDao.getById(parseLong);
        courseDao.delete(entity);
    }


}
