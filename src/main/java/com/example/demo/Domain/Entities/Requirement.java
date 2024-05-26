package com.example.demo.Domain.Entities;

public class Requirement {
    private int id;
    private String name;

    public Requirement(){}

    public Requirement(int id, String name) {
        this.id = id;
        this.name = name;
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
