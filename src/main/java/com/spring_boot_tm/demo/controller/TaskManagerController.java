package com.spring_boot_tm.demo.controller;

import com.spring_boot_tm.demo.entity.TaskManager;
import com.spring_boot_tm.demo.Repository.TaskManagerRepo;
import com.spring_boot_tm.demo.service.TaskService;
import com.spring_boot_tm.demo.dto.TaskRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")

public class TaskManagerController {
    private final TaskManagerRepo tmRepository;
    private final TaskService taskService;

    public TaskManagerController(TaskManagerRepo tmRepository, TaskService taskService) {
        this.tmRepository = tmRepository;
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskManager> getAllTasks() {
        return tmRepository.findAll();
    }

    @PostMapping
    public TaskManager createTask(@RequestBody TaskRequest request) {
        return taskService.createTask(request);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    @PostMapping("/{id}/start")
   public TaskManager startTimer(@PathVariable Long id) { return taskService.startTimer(id);}

   @PostMapping("/{id}/pause")
  public TaskManager pauseTimer(@PathVariable Long id) { return taskService.pauseTimer(id);}

    @PostMapping("/{id}/resume")
    public TaskManager resumeTimer(@PathVariable Long id) {return taskService.resumeTimer(id);}

    @PostMapping("/{id}/check-parent")
    public void checkParent(@PathVariable Long id) { taskService.updateParentCompletion(id);}
}

