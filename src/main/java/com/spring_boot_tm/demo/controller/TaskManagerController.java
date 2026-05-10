package com.spring_boot_tm.demo.controller;

import com.spring_boot_tm.demo.entity.TaskManager;
import com.spring_boot_tm.demo.Repository.TaskManagerRepo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")

public class TaskManagerController {
    private final TaskManagerRepo tmRepository;

    public TaskManagerController(TaskManagerRepo tmRepository) {
        this.tmRepository = tmRepository;
    }

    @GetMapping
    public List<TaskManager> getAllTasks() {
        return tmRepository.findAll();
    }

    @PostMapping
    public TaskManager createTask(@RequestBody TaskManager task) {
        return tmRepository.save(task);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        tmRepository.deleteById(id);
    }
}
