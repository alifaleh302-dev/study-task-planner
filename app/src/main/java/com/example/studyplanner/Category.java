package com.example.studyplanner;

/**
 * Represents a task category (study, work, personal, ...).
 * The {@code taskCount} field is not stored in the table; it is filled by
 * aggregate queries when the categories screen is shown.
 */
public class Category {

    private long id;
    private String name;
    private String color;
    private int taskCount;

    public Category() {
    }

    public Category(long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }
}
