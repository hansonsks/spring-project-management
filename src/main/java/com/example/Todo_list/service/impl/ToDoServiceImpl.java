package com.example.Todo_list.service.impl;

import com.example.Todo_list.entity.ToDo;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.exception.NullEntityException;
import com.example.Todo_list.exception.UserIsToDoOwnerException;
import com.example.Todo_list.repository.ToDoRepository;
import com.example.Todo_list.repository.UserRepository;
import com.example.Todo_list.service.ToDoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ToDoServiceImpl implements ToDoService {

    private final static Logger logger = LoggerFactory.getLogger(ToDoServiceImpl.class);
    private final ToDoRepository toDoRepository;
    private final UserRepository userRepository;

    @Override
    public ToDo save(ToDo toDo) {
        if (toDo == null) {
            logger.error("ToDoService.save(): Attempting to save null ToDo");
            throw new NullEntityException(this.getClass().getName(), "ToDo cannot be null");
        }

        logger.info("ToDoService.save(): Saving " + toDo);
        return toDoRepository.save(toDo);
    }

    @Override
    public ToDo findToDoById(Long id) {
        Optional<ToDo> todo = toDoRepository.findById(id);

        if (todo.isPresent()) {
            logger.info("ToDoService.findToDoById(): Found " + todo.get());
            return todo.get();
        } else {
            logger.error("ToDoService.findToDoById(): No ToDo with id=" + id + " was found");
            throw new EntityNotFoundException("No ToDo with id=" + id + " was found");
        }
    }

    @Override
    public void deleteToDoById(Long id) {
        ToDo todo = this.findToDoById(id);
        logger.info("ToDoService.deleteToDoById(): Deleting " + todo);
        toDoRepository.delete(todo);
    }

    @Override
    public List<ToDo> findAllToDo() {
        logger.info("ToDoService.getAllToDo(): Finding all ToDos");
        return toDoRepository.findAll();
    }

    @Override
    public List<ToDo> findAllToDoOfUserId(Long userId) {
        logger.info("ToDoService.getAllToDoOfUserId(): Finding all ToDos of ownerId/userId=" + userId);
        return toDoRepository.findTodoByOwnerId(userId);
    }

    @Override
    public void addCollaborator(Long toDoId, Long collaboratorId) {
        ToDo todo = this.findToDoById(toDoId);
        User user = this.findCollaboratorById(todo, collaboratorId);

        logger.info("ToDoService.addCollaborator(): " +
                "Adding User with userId=" + collaboratorId + " as a collaborator into ToDo with toDoId=" + toDoId);

        todo.getCollaborators().add(user);
        user.getCollaborators().add(todo);
        toDoRepository.save(todo);

        logger.info("Updated ToDo: " + todo);
        logger.info("Updated User: " + user);
    }

    @Override
    public void removeCollaborator(Long toDoId, Long collaboratorId) {
        ToDo todo = this.findToDoById(toDoId);
        User user = this.findCollaboratorById(todo, collaboratorId);

        logger.info("ToDoService.removeCollaborator(): " +
                "Deleting User with userId=" + collaboratorId + " from ToDo collaborators with toDoId=" + toDoId);

        todo.getCollaborators().remove(user);
        user.getCollaborators().remove(todo);
        toDoRepository.save(todo);

        logger.info("Updated ToDo: " + todo);
        logger.info("Updated User: " + user);
    }

    private User findCollaboratorById(ToDo toDo, Long collaboratorId) {
        Optional<User> collaborator = userRepository.findById(collaboratorId);

        if (collaborator.isPresent()) {
            logger.info("ToDoService.findCollaboratorById(): Found " + collaborator.get());

            if (isOwnerOfToDo(toDo, collaborator.get())) {
                logger.error("ToDoService.findCollaboratorById(): " +
                        "User with userId=" + collaboratorId + " is owner of ToDo with toDoId=" + toDo.getId());
                throw new UserIsToDoOwnerException(toDo.getId(), collaboratorId);
            }

            return collaborator.get();
        } else {
            logger.error("ToDoService.findCollaboratorById(): No user with userId=" + collaboratorId + " was found");
            throw new EntityNotFoundException("No User with userId=" + collaboratorId + " was found");
        }
    }

    private boolean isOwnerOfToDo(ToDo toDo, User collaborator) {
        return collaborator.equals(toDo.getOwner());
    }
}
