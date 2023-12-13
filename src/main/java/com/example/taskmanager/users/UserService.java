package com.example.taskmanager.users;

import com.example.taskmanager.usersData.UserData;
import com.example.taskmanager.usersData.UserDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;
    private MongoTemplate mongoTemplate;
    private UserDataRepository mainTasksIdRepository;

    @Autowired
    public UserService(UserRepository userRepository, MongoTemplate mongoTemplate, UserDataRepository mainTasksIdRepository) {
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
        this.mainTasksIdRepository = mainTasksIdRepository;
    }

    public void createUser(String username, String email) {
        // Finding user in database
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        List<User> usersList = mongoTemplate.find(query, User.class);

        // If user found
        if (!usersList.isEmpty()) {
            throw new IllegalArgumentException("user with email " + email + " already exists. " +
                    "Can't create new user with the same email");
        }
        // If user not found
        else {
            User user = new User(username, email);
            user = userRepository.save(user);
            UserData userData = new UserData(user.getId(), new ArrayList<>());
            mainTasksIdRepository.save(userData);
        }
    }
}
