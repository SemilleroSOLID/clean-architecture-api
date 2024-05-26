package com.example.demo.Domain.Entities;

import java.util.List;

public class Student {
    private int id;
    private String name;
    private String program;
    private double average;
    private int stratum;
    private List<Request> studentRequestList;

    public Student(){}

    public Student(int id, String name, String program, double average, int stratum, List<Request> studentRequestList) {
        this.id = id;
        this.name = name;
        this.program = program;
        this.average = average;
        this.stratum = stratum;
        this.studentRequestList = studentRequestList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public List<Request> getStudentRequestList() {
        return studentRequestList;
    }

    public void setStudentRequestList(List<Request> studentRequestList) {
        this.studentRequestList = studentRequestList;
    }

    public int getStratum() {
        return stratum;
    }

    public void setStratum(int stratum) {
        this.stratum = stratum;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", program='" + program + '\'' +
                ", average=" + average +
                ", stratum=" + stratum +
                ", studentRequestList=" + studentRequestList +
                '}';
    }
}
