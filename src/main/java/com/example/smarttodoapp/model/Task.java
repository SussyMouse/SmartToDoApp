package com.example.smarttodoapp.model;

import java.time.LocalDate;

public class Task {
    private String name;
    private String description;
    private String category;
    private Integer priority;
    private LocalDate dueDate;
    private boolean completed;

    // Jackson needs a no-arg constructor
    public Task() {}

    public Task(String name, String description, String category,
                Integer priority, LocalDate dueDate, boolean completed) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    // Getters & setters (all fields)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}