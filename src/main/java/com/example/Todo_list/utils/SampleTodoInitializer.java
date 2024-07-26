package com.example.Todo_list.utils;

import com.example.Todo_list.entity.*;
import com.example.Todo_list.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Class for initializing sample todos for a user.
 */
@Service
@RequiredArgsConstructor
public class SampleTodoInitializer {

    private final StateRepository stateRepository;
    private final ToDoRepository toDoRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;

    /**
     * Initializes sample todos for a user.
     *
     * @param user the user for whom the sample todos are to be initialized
     */
    public void initUserToDo(User user) {
        List<ToDo> todos = user.getTodoList();

        ToDo todo1 = new ToDo();
        todo1.setOwner(user);
        todo1.setTitle("Sample Project #1");
        todo1.setDescription("This is a sample project created by the system.");
        toDoRepository.save(todo1);

        ToDo todo2 = new ToDo();
        todo2.setOwner(user);
        todo2.setTitle("Sample Project #2");
        todo2.setDescription("This is a sample project created by the system.");
        toDoRepository.save(todo2);

        ToDo todo3 = new ToDo();
        todo3.setOwner(user);
        todo3.setTitle("Sample Project #3");
        todo3.setDescription("This is a sample project created by the system.");
        toDoRepository.save(todo3);

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setContent("Sample Comment");
        commentRepository.save(comment);

        Task task1 = new Task();
        task1.setName("Sample Task #1");
        task1.setDescription("This is a sample task created by the system.");
        task1.setTodo(todo1);
        task1.setState(stateRepository.findByName("New").get());
        task1.setPriority(Priority.TRIVIAL);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setName("Sample Task #2");
        task2.setDescription("This is a sample task created by the system.");
        task2.setTodo(todo1);
        task2.setState(stateRepository.findByName("In Progress").get());
        task2.setPriority(Priority.LOW);
        taskRepository.save(task2);

        Task task3 = new Task();
        task3.setName("Sample Task #3");
        task3.setDescription("This is a sample task created by the system.");
        task3.setTodo(todo1);
        task3.setState(stateRepository.findByName("Completed").get());
        task3.setPriority(Priority.MEDIUM);
        taskRepository.save(task3);

        Task task4 = new Task();
        task4.setName("Sample Task #4");
        task4.setDescription("This is a sample task created by the system.");
        task4.setTodo(todo1);
        task4.setState(stateRepository.findByName("In Progress").get());
        task4.setPriority(Priority.HIGH);
        taskRepository.save(task4);

        Task task5 = new Task();
        task5.setName("Sample Task #5");
        task5.setDescription("This is a sample task created by the system.");
        task5.setTodo(todo1);
        task5.setState(stateRepository.findByName("Under Review").get());
        task5.setPriority(Priority.URGENT);
        taskRepository.save(task5);

        task1.getAssignedUsers().add(user);
        user.getAssignedTasks().add(task1);
        taskRepository.save(task1);

        task1.setComments(List.of(comment));
        comment.setTask(task1);
        commentRepository.save(comment);

        todo1.setTasks(List.of(task1, task2, task3, task4, task5));

        todos.addAll(List.of(todo1, todo2, todo3));

        Notification notification = new Notification();
        notification.setTitle("Sample Notification");
        notification.setMessage("This is a sample notification created by the system.");
        notification.setUser(user);
        notification.setCreatedAt(ZonedDateTime.now());
        notificationRepository.save(notification);

        Notification signUpNotification = new Notification();
        signUpNotification.setTitle("Please read me!");
        if (user.getIsGuest()) {
            signUpNotification.setMessage("Your account will be deleted after 30 minutes of use. Please sign up to access all features.");
        } else {
            signUpNotification.setMessage("Feel free to change your role to 'Admin' in your profile to access all features.");
        }
        signUpNotification.setUser(user);
        signUpNotification.setCreatedAt(ZonedDateTime.now());
        notificationRepository.save(signUpNotification);
    }
}
