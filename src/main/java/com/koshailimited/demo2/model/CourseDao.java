package com.koshailimited.demo2.model;

import com.koshailimited.demo2.entities.Courses;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseDao extends JpaRepository<Courses, Long> {

}
