package com.example.taskmanager;

import com.example.taskmanager.usersData.UserDataRepository;
import com.example.taskmanager.tasks.TaskRepository;
import com.example.taskmanager.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskManagerApplication implements CommandLineRunner {

    private final TaskRepository taskRepository;
    private final UserDataRepository mainTasksIdRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskManagerApplication(TaskRepository taskRepository, UserDataRepository mainTasksIdRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.mainTasksIdRepository = mainTasksIdRepository;
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
