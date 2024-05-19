package com.example.demo.Infrastructure.Persistence.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "ConvocationTypes", schema = "dbo")
public class ConvocationTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "convocationTypeName", nullable = false, length = 255)
    private String convocationTypeName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getConvocationTypeName() {
        return convocationTypeName;
    }

    public void setConvocationTypeName(String convocationTypeName) {
        this.convocationTypeName = convocationTypeName;
    }
}
