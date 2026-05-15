package com.spring_boot_tm.demo.controller;

import com.spring_boot_tm.demo.entity.TaskManager;
import com.spring_boot_tm.demo.Repository.TaskManagerRepo;
import com.spring_boot_tm.demo.service.TaskService;
import com.spring_boot_tm.demo.dto.TaskRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
/**
 * This class represents the controller layer of the task manager.
 * <p>It handles GET, POST an DELETE REST-API endpoints and uses
 * the TaskService class to edit the tasks as well as the TaskRequest
 * DTO to read out the transferred data from the JSON layer.
 * Provided endpoints are:</p>
 * <ul>
 *     <li>creating tasks</li>
 *     <li>deleting tasks</li>
 *     <li>retrieving tasks</li>
 *     <li>starting tasks</li>
 *     <li>pausing tasks</li>
 *     <li>checking (parent) task completion tasks</li>
 * </ul>
 */
public class TaskManagerController {
    private final TaskManagerRepo tmRepository;
    private final TaskService taskService;

    /**
     * Constructor for the controller layer with dependency injection.
     * @param tmRepository repository for task persistence within a database
     * @param taskService service class to delegate the business logic to
     */
    public TaskManagerController(TaskManagerRepo tmRepository, TaskService taskService) {
        this.tmRepository = tmRepository;
        this.taskService = taskService;
    }

    /**
     * Returns all stored tasks within the database.
     * @return list of all tasks currently within the database
     */
    @GetMapping
    public List<TaskManager> getAllTasks() {
        return tmRepository.findAll();
    }

    /**
     * Creates a new task.
     * <p>
     * Example request:
     * </p>
     *
     * <pre>
     * {
     * "title": "Lernen",
     * "durationSeconds": 3600
     * }
     * </pre>
     * @param request DTO containing task information
     * @return created task
     */
    @PostMapping
    public TaskManager createTask(@RequestBody TaskRequest request) {
        return taskService.createTask(request);
    }

    /**
     * Deleted a task by id.
     * <p>Also deletes all subtasks recursively.</p>
     * @param id id of task to be deleted
     */
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    /**
     * Starts the timer for a task with a durationSeconds value greater than zero.
     * @param id task id
     * @return updated task with active timer
     */
    @PostMapping("/{id}/start")
   public TaskManager startTimer(@PathVariable Long id) { return taskService.startTimer(id);}

    /**
     * Pauses the timer for a given task
     * @param id given task
     * @return updated task with paused timer
     */
   @PostMapping("/{id}/pause")
  public TaskManager pauseTimer(@PathVariable Long id) { return taskService.pauseTimer(id);}

    /**
     * resumes the timer of a given task, given that the timer was started once.
     * @param id task id
     * @return updated task with resumed timer
     */
    @PostMapping("/{id}/resume")
    public TaskManager resumeTimer(@PathVariable Long id) {return taskService.resumeTimer(id);}

    /**
     * Checks whether a parent task should be automatically markes as completed.
     * <p>
     *     A parent task becomes completed if all subtasks are completed.
     * </p>
     * @param id parent task id
     */
    @PostMapping("/{id}/check-parent")
    public void checkParent(@PathVariable Long id) { taskService.updateParentCompletion(id);}
}

