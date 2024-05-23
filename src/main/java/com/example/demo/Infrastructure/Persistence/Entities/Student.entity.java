package com.example.demo.Infrastructure.Persistence.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Student", schema = "dbo")
public class StudentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nameStudent", nullable = false, length = 255)
    private String nameStudent;

     @Column(name = "program", nullable = false, length = 255)
    private String program;

     @Column(name = "average", nullable = false, length = 255)
    private Integer average;

     @Column(name = "stratum", nullable = false, length = 255)
    private Integer stratum;

    @OneToMany(mappedBy="student")
    private Set<RequestEntity> requests;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNameStudent() {
        return nameStudent;
    }

    public void setNameStudent(String nameStudent) {
        this.nameStudent = nameStudent;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public Integer getAverage() {
        return average;
    }

    public void setAverage(Integer average) {
        this.average = average;
    }

    public Integer getStratum() {
        return stratum;
    }

    public void setStratum(Integer stratum) {
        this.stratum = stratum;
    }

    public Set<RequestEntity> getRequests() {
        return requests;
    }

    public void setRequests(Set<RequestEntity> requests) {
        this.requests = requests;
    }

}