package com.example.taskmanager.usersData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "usersData")
public class UserData {
    @Id
    private String userId;
    private List<String> mainTasksId;

    public UserData(String userId, List<String > mainTasksId) {
        this.userId = userId;
        this.mainTasksId = mainTasksId;
    }
}
