package com.example.taskmanager.tasks;

import com.example.taskmanager.usersData.UserData;
import com.example.taskmanager.usersData.UserDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserDataRepository userDataRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public TaskService(
            TaskRepository taskRepository,
            UserDataRepository userDataRepository,
            MongoTemplate mongoTemplate
    ) {
        this.taskRepository = taskRepository;
        this.userDataRepository = userDataRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public Task getTask(
            String id
    ) throws NoSuchElementException {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isEmpty()) {
            throw new NoSuchElementException("Task with id " + id + " not found");
        }
        return optionalTask.get();
    }

    public UserData getUserData(
            String userId
    ) throws NoSuchElementException {
        Optional<UserData> optionalUserData = userDataRepository.findById(userId);
        if (optionalUserData.isEmpty()) {
            throw new NoSuchElementException("UserData with id " + userId + " not found");
        }
        return optionalUserData.get();
    }

    public void addTask(
            String title,
            String description
    ) {
        addMainTask(title, description);
    }
    public void addTask(
            String title,
            String description,
            String parentId
    ) {
        addNestedTask(title, description, parentId);
    }

    private void addMainTask(
            String title,
            String description
    ) throws NoSuchElementException {
        String userId = System.getenv("vasilcoin002_userId");
        UserData userData = getUserData(userId);

        Task task = taskRepository.save(new Task(title, description, 1, null));

        List<String> mainTasksIdList = userData.getMainTasksId();
        mainTasksIdList.add(task.getId());
        userData.setMainTasksId(mainTasksIdList);

        userDataRepository.save(userData);
    }

    private void addNestedTask(
            String title,
            String description,
            String parentId
    ) throws NoSuchElementException, IndexOutOfBoundsException {
        Task parentTask = getTask(parentId);

        // checking nesting
        final int MAX_NESTING_LEVEL = Integer.parseInt(System.getenv("MAX_NESTING_LEVEL"));
        int nesting_level = parentTask.getNesting_level() + 1;
        if (nesting_level > MAX_NESTING_LEVEL) {
            throw new IndexOutOfBoundsException(
                    "Forbidden creating task located on more than " + MAX_NESTING_LEVEL + " nesting level"
            );
        }

        Task task = taskRepository.save(new Task(title, description, nesting_level, parentId));

        List<String> parentNestedTasksId = parentTask.getNestedTasksId();
        parentNestedTasksId.add(task.getId());
        parentTask.setNestedTasksId(parentNestedTasksId);

        taskRepository.save(parentTask);
    }

    public void deleteTask(
            String taskId
    ) throws NoSuchElementException {
        Task task = getTask(taskId);

        // deleting all nested tasks
        taskRepository.deleteAllById(getNestedTasksId(taskId));

        // deleting task
        if (task.getNesting_level() > 1) {
            deleteNestedTask(taskId);
        }
        else {
            deleteMainTask(taskId);
        }
    }

    private void deleteMainTask(String taskId) {
        String userId = System.getenv("vasilcoin002_userId");
        UserData userData = getUserData(userId);

        // removing task id from userData
        List<String> userMainTasksIdList = userData.getMainTasksId();
        userMainTasksIdList.remove(taskId);
        userData.setMainTasksId(userMainTasksIdList);

        taskRepository.deleteById(taskId);
        userDataRepository.save(userData);
    }

    private void deleteNestedTask(String taskId) {
        Task task = getTask(taskId);
        String parentId = task.getParentId();
        Task parentTask = getTask(parentId);

        // removing taskId from parent
        List<String> parentNestedTasksId = parentTask.getNestedTasksId();
        parentNestedTasksId.remove(taskId);
        parentTask.setNestedTasksId(parentNestedTasksId);

        taskRepository.save(parentTask);
        taskRepository.deleteById(taskId);
    }

    public List<String> getNestedTasksId(
            String taskId
    ) throws NoSuchElementException {
        List<String> allNestedTasksId = new ArrayList<>();
        extendListToAllNestedIdList(taskId, allNestedTasksId);
        allNestedTasksId.remove(0);
        return allNestedTasksId;
    }

    private void extendListToAllNestedIdList(
            String taskId,
            List<String> allNestedIdList
    ) throws NoSuchElementException {
        Task task = getTask(taskId);
        allNestedIdList.add(taskId);
        for (String nestedTaskId : task.getNestedTasksId()) {
            extendListToAllNestedIdList(nestedTaskId, allNestedIdList);
        }
    }
}
