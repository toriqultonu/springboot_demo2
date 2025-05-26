package com.tts.patientservice;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class Student {
    private String name;
    private double gpa;

    public Student(String name, double gpa) {
        this.name = name;
        this.gpa = gpa;
    }

    public String getName() {
        return name;
    }

    public double getGpa() {
        return gpa;
    }
}

public class test {

    public static void main(String[] args) {

        List<Student> students = new ArrayList<>();
        students.add(new Student("Alice", 3.5));
        students.add(new Student("Bob", 3.8));
        students.add(new Student("Charlie", 3.5));
        students.add(new Student("David", 3.9));

        students.sort(Comparator.comparing(Student::getGpa).reversed().thenComparing(Student::getName));
        System.out.println("Students sorted by name:");
        for (Student student : students) {
            System.out.println(student.getName() + " - GPA: " + student.getGpa());
        }
    }
}
