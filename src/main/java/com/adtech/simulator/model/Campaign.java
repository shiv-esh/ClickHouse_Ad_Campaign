package com.adtech.simulator.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "campaigns")
public class Campaign {

    @Id
    private Long id;
    private String name;
    private Double budget;
    private String imageUrl;
    private String status;

    public Campaign() {
    }

    public Campaign(Long id, String name, Double budget, String imageUrl, String status) {
        this.id = id;
        this.name = name;
        this.budget = budget;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
