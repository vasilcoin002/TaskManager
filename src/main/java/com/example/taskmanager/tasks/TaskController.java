package com.example.taskmanager.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public Task getTask(@RequestParam(name = "id") String id) {
        return taskService.getTask(id);
    }

    @PostMapping
    public void addTask(
            @RequestParam(name = "title") String title,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "parentId", required = false) String parentId
    ) {
        if (parentId == null) {
            taskService.addTask(title, description);
        }
        else {
            taskService.addTask(title, description, parentId);
        }
    }

    @DeleteMapping
    public void deleteTask(
            @RequestParam(name = "id") String id
    ) {
        taskService.deleteTask(id);
    }

//    @PutMapping
//    public void updateTask(
//            @RequestParam(name = "id") String id,
//            @RequestParam(name = "title", required = false) String title,
//            @RequestParam(name = "description", required = false) String description,
//            @RequestParam(name = "parentId", required = false) String parentId
//    ) {
//        taskService.updateTask(id, title, description, parentId);
//    }
}
