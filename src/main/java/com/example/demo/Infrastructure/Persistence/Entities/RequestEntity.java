package com.example.demo.Infrastructure.Persistence.Entities;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.Set;

@Entity
@Table(name = "Request", schema = "dbo")
public class RequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "creationDate", nullable = false)
    private Date creationDate;

    @Column(name = "requestState", nullable = false)
    private Integer requestState;

    @Column(name = "convocationId", nullable = false)
    private Integer convocationId;

    @Column(name = "studentId", nullable = false)
    private Integer studentId;

    @OneToMany(mappedBy="request")
    private Set<RequestRequirementEntity> requestRequirements;

    @ManyToOne
    @JoinColumn(name="studentId", nullable=false)
    private StudentEntity student;

    @ManyToOne
    @JoinColumn(name="convocationId", nullable=false)
    private ConvocationEntity convocation;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getRequestState() {
        return requestState;
    }

    public void setRequestState(Integer requestState) {
        this.requestState = requestState;
    }

    public Integer getConvocationId() {
        return convocationId;
    }

    public void setConvocationId(Integer convocationId) {
        this.convocationId = convocationId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Set<RequestRequirementEntity> getRequestRequirements() {
        return requestRequirements;
    }

    public void setRequestRequirements(Set<RequestRequirementEntity> requestRequirements) {
        this.requestRequirements = requestRequirements;
    }

    public StudentEntity getStudent() {
        return student;
    }

    public void setStudent(StudentEntity student) {
        this.student = student;
    }

    public ConvocationEntity getConvocation() {
        return convocation;
    }

    public void setConvocation(ConvocationEntity convocation) {
        this.convocation = convocation;
    }

}