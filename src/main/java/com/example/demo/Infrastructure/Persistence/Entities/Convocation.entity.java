package com.example.demo.Infrastructure.Persistence.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Convocation", schema = "dbo")
public class ConvocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

     @Column(name = "convocationDescription", nullable = false, length = 255)
    private String convocationDescription;

     @Column(name = "convocationTypeId", nullable = false, length = 255)
    private Integer convocationTypeId;

     @Column(name = "startDate", nullable = false, length = 255)
    private Date startDate;

     @Column(name = "endDate", nullable = false, length = 255)
    private Date endDate;

     @Column(name = "stateConvocationId", nullable = false, length = 255)
    private Integer stateConvocationId;
    
    @OneToMany(mappedBy="convocation")
    private Set<ConvocationRequirementEntity> convocationRequirements;

    @OneToMany(mappedBy="convocation")
    private Set<RequestEntity> Requests;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getConvocationDescription() {
        return convocationDescription;
    }

    public void setConvocationDescription(String convocationDescription) {
        this.convocationDescription = convocationDescription;
    }

    public Integer getConvocationTypeId() {
        return convocationTypeId;
    }

    public void setConvocationTypeId(Integer convocationTypeId) {
        this.convocationTypeId = convocationTypeId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getStateConvocationId() {
        return stateConvocationId;
    }

    public void setStateConvocationId(Integer stateConvocationId) {
        this.stateConvocationId = stateConvocationId;
    }

    public Set<ConvocationRequirementEntity> getConvocationRequirements() {
        return convocationRequirements;
    }

    public void setConvocationRequirements(Set<ConvocationRequirementEntity> convocationRequirements) {
        this.convocationRequirements = convocationRequirements;
    }

    public Set<RequestEntity> getRequests() {
        return Requests;
    }

    public void setRequests(Set<RequestEntity> Requests) {
        this.Requests = Requests;
    }

}
