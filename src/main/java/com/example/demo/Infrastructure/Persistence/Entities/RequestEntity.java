package com.example.demo.Infrastructure.Persistence.Entities;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.List;

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

}
