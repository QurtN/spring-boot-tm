package com.spring_boot_tm.demo.service;

import com.spring_boot_tm.demo.entity.TaskManager;
import com.spring_boot_tm.demo.Repository.TaskManagerRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TaskService {

    private final TaskManagerRepo taskRepo;

    public TaskService(TaskManagerRepo taskRepo) {
        this.taskRepo = taskRepo;
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
       }
        TaskManager updatedTm = taskRepo.save(tm);
       return updatedTm;
   }

    //Check if task is done
  public void checkTaskCompletion(Long id){
      TaskManager tm=taskRepo.findById(id).orElseThrow();
      if(tm.getElapsedSeconds()>=tm.getDurationSeconds()) {
          tm.setCompleted(true);
          taskRepo.save(tm);
      }
   }
}
