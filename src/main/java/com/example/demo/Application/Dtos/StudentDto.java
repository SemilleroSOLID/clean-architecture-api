package com.example.demo.Application.Dtos;

import java.io.Serializable;

public class StudentDto implements Serializable {
    private int id;
    private String name;
    private String email;
    private String program;
    private double average;
    private int stratum;

    public StudentDto() {}

    public StudentDto(int id, String name, String email, String program, double average, int stratum) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.program = program;
        this.average = average;
        this.stratum = stratum;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public int getStratum() {
        return stratum;
    }

    public void setStratum(int stratum) {
        this.stratum = stratum;
    }
}
