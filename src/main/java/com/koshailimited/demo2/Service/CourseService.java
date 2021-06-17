package com.koshailimited.demo2.Service;

import com.koshailimited.demo2.entities.Courses;

import java.util.List;

public interface CourseService {
    public List<Courses> getCourses();

    public Courses getCourse(long courseId);

    public Courses addCourse(Courses course);
}
