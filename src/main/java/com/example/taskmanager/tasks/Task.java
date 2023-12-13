package com.example.taskmanager.tasks;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "tasks")
public class Task {
    @Id
    private String id;
    private String title;
    private String description;
    private Integer nesting_level;
    private List<String> nestedTasksId = new ArrayList<>();
    private String parentId;

    public Task(String title, String description, Integer nesting_level, String parentId) {
        this.title = title;
        this.description = description;
        this.nesting_level = nesting_level;
        this.parentId = parentId;
    }
}
