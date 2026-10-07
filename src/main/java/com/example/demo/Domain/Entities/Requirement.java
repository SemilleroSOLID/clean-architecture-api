package com.example.demo.Domain.Entities;

import java.io.Serializable;

public class Requirement implements Serializable {
    private int id;
    private String name;
    private boolean grade;

    public Requirement(){}

    public Requirement(int id, String name, boolean grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
    }

    public boolean isGrade() {
        return grade;
    }

    public void setGrade(boolean grade) {
        this.grade = grade;
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

    @Override
    public String toString() {
        return "Requirement{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
