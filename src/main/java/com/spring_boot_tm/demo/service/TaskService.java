package com.spring_boot_tm.demo.service;

import com.spring_boot_tm.demo.dto.TaskRequest;
import com.spring_boot_tm.demo.entity.TaskManager;
import com.spring_boot_tm.demo.Repository.TaskManagerRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TaskService {

    private final TaskManagerRepo taskRepo;

    public TaskService(TaskManagerRepo taskRepo) {
        this.taskRepo = taskRepo;
    }

    public TaskManager createTask(@RequestBody TaskRequest request) {
        TaskManager tm = new TaskManager();
        tm.setTitle(request.getTitle());
        if (request.getCompleted() != null) {
            tm.setCompleted(request.getCompleted());
        } else {
            tm.setCompleted(false);
        }

        if (request.getParentTaskId() != null) {
            TaskManager parentTask = taskRepo.findById(request.getParentTaskId()).orElseThrow();
            tm.setParentTask(parentTask);
        }
        tm.setDueDate(request.getDueDate());
        if (request.getDurationSeconds() != null) {
            if (request.getDurationSeconds() < 0) throw new IllegalArgumentException("DurationSeconds cannot be negative!");
            tm.setDurationSeconds(request.getDurationSeconds());
        } else {
            tm.setDurationSeconds(0L);
        }

        return taskRepo.save(tm);
    }

    //Auto-complete Parent task, when all subtasks are complete
    public void updateParentCompletion(Long parentId) {
        List<TaskManager> subtasks = taskRepo.findByParentTaskId(parentId);
        boolean allCompleted = subtasks.stream().allMatch(TaskManager::isCompleted);
        TaskManager parent = taskRepo.findById(parentId).orElseThrow();
        parent.setCompleted(allCompleted);
        taskRepo.save(parent);
    }

    //starting the timer

   public TaskManager startTimer(Long id) {
       TaskManager tm = taskRepo.findById(id).orElseThrow();
       tm.setTimerStart(LocalDateTime.now());
       tm.setPaused(false);
       TaskManager updatedTm = taskRepo.save(tm);
       return updatedTm;
   }

  public TaskManager pauseTimer(Long id) {

 TaskManager tm = taskRepo.findById(id).orElseThrow();

       if(tm.getTimerStart()!=null){
           long seconds = ChronoUnit.SECONDS.between(tm.getTimerStart(),LocalDateTime.now());
           tm.setElapsedSeconds(tm.getElapsedSeconds()+seconds);
           tm.setPaused(true);
            //check completion of task
           if (tm.getElapsedSeconds()>=tm.getDurationSeconds()) tm.setCompleted(true);
           //reset timer
           tm.setTimerStart(null);
           tm.setPaused(false);
       }
       else {
           throw new RuntimeException("Timer has not been started!");
       }
        TaskManager updatedTm = taskRepo.save(tm);
       return updatedTm;
   }

    public TaskManager resumeTimer(Long id) {
        TaskManager tm = taskRepo.findById(id).orElseThrow();
        //Timer must be paused to use this method
        if (!tm.isPaused()) throw new IllegalStateException("Timer is not paused!");
        //restart timer from current time
        tm.setTimerStart(LocalDateTime.now());
        tm.setPaused(false);

        return taskRepo.save(tm);
    }

    public void deleteTask(Long id) {
        List<TaskManager> subtasks=taskRepo.findByParentTaskId(id);
        for (TaskManager subtask:subtasks) {
            deleteTask(subtask.getId());
        }
        taskRepo.deleteById(id);
    }
}
