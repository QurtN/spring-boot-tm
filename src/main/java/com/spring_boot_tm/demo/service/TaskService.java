package com.spring_boot_tm.demo.service;

import com.spring_boot_tm.demo.dto.TaskRequest;
import com.spring_boot_tm.demo.entity.TaskManager;
import com.spring_boot_tm.demo.Repository.TaskManagerRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * This class represents the service layer for the task manager logic.
 * <p>
 * It handles:
 * </p>
 *
 * <ul>
 *   <li>task creation</li>
 *   <li>parent and subtask relations</li>
 *   <li>timer functionality</li>
 *   <li>automatic task completion</li>
 *   <li>recursive task deletion</li>
 *</ul>
 * <p>
 * The business logic contained within this class is separated from the controller layer of the task manager.
 * </p>
 */
@Service
public class TaskService {

    private final TaskManagerRepo taskRepo;

    /**
     * Creates the service with repository injection.
     *
     * @param taskRepo repository for task persistence within a database
     */
    public TaskService(TaskManagerRepo taskRepo) {
        this.taskRepo = taskRepo;
    }

    /**
     * Creates a new task using the task request DTO.
     * <p>
     *     Supports: normal tasks, subtasks and timed tasks
     * </p>
     * @param request DTO object containing task data
     * @return saved task entity
     * @throws ResponseStatusException whenever a parent task does not exist
     * @throws IllegalArgumentException durationSeconds needs to be a positive value when set
     */
    public TaskManager createTask(@RequestBody TaskRequest request) {
        TaskManager tm = new TaskManager();
        tm.setTitle(request.getTitle());
        if (request.getCompleted() != null) {
            tm.setCompleted(request.getCompleted()); //fill the value with data obtained from request DTO
        } else {
            tm.setCompleted(false); //default value of task
        }

        if (request.getParentTaskId() != null) {
            TaskManager parentTask = taskRepo.findById(request.getParentTaskId()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent task not found!"));
            tm.setParentTask(parentTask);
        }
        tm.setDueDate(request.getDueDate()); //this might be null
        if (request.getDurationSeconds() != null) {
            if (request.getDurationSeconds() < 0) throw new IllegalArgumentException("DurationSeconds cannot be negative!");
            tm.setDurationSeconds(request.getDurationSeconds());
        } else {
            tm.setDurationSeconds(0L);
        }

        return taskRepo.save(tm);
    }

    /**
     * <p>
     * Sets the completion status of a parent task to true, when all of
     * its subtask have completion status equal to true.
     * </p>
     * @param parentId The ID of the parent task
     * @throws java.util.NoSuchElementException when ID of parent can not be found within database
     */
    public void updateParentCompletion(Long parentId) {
        List<TaskManager> subtasks = taskRepo.findByParentTaskId(parentId);
        boolean allCompleted = subtasks.stream().allMatch(TaskManager::isCompleted);
        TaskManager parent = taskRepo.findById(parentId).orElseThrow();
        parent.setCompleted(allCompleted);
        taskRepo.save(parent);
    }

    /**
     * Starts the timer for a valid time task.
     *
     * <p>
     *     A task is considered a valid time task if its durationSeconds value is greater than 0.
     *     All of the time-sensitive methods like startTimer, pauseTimer and resumeTimer can then
     *     be used.
     * </p>
     * @param id task id
     * @return updated task
     * @throws IllegalStateException if task has no durationSeconds value greater than 0.
     * @throws java.util.NoSuchElementException when task id can not be found within the database
     */
   public TaskManager startTimer(Long id) {
       TaskManager tm = taskRepo.findById(id).orElseThrow();
       tm.setTimerStart(LocalDateTime.now());
       tm.setPaused(false);
       TaskManager updatedTm = taskRepo.save(tm);
       if(tm.getDurationSeconds() <= 0L) throw new IllegalStateException("Task has no timer duration and can not be started!");
       return updatedTm;
   }

    /**
     * Pauses the timer, if task is a valid time task.
     * @param id task id
     * @return  updated task
     * @throws IllegalStateException when task has no valid durationSeconds value
     * @throws java.util.NoSuchElementException if task can not be found within database
     * @throws RuntimeException when Timer has not been started yet, i.e. timerStart DateTime is null
     */
  public TaskManager pauseTimer(Long id) {
        TaskManager tm = taskRepo.findById(id).orElseThrow();
       if(tm.getDurationSeconds() <= 0L) throw new IllegalStateException("Task has no timer duration and can not be paused!");
       if(tm.getTimerStart()!=null){
           long seconds = ChronoUnit.SECONDS.between(tm.getTimerStart(),LocalDateTime.now());
           tm.setElapsedSeconds(tm.getElapsedSeconds()+seconds);
           tm.setPaused(true);
            //check completion of task
           if (tm.getElapsedSeconds()>=tm.getDurationSeconds()){
               tm.setCompleted(true);
               //reset timer
               tm.setTimerStart(null);
               tm.setPaused(false);
           }
       }
       else {
           throw new RuntimeException("Timer has not been started!");
       }
        TaskManager updatedTm = taskRepo.save(tm);
       return updatedTm;
   }

    /**
     * Resumes the timer after it has been started once.
     * @param id task id
     * @return updated task
     * @throws IllegalStateException if task has not been paused or task timer has no duration
     * @throws java.util.NoSuchElementException when task can not be found within database
     */
    public TaskManager resumeTimer(Long id) {
        TaskManager tm = taskRepo.findById(id).orElseThrow();
        //Timer must be paused to use this method
        if (!tm.isPaused()) throw new IllegalStateException("Timer is not paused!");
        //Timer must have a duration
        if(tm.getDurationSeconds() <= 0L) throw new IllegalStateException("Task has no timer duration and can not be resumed!");
        //restart timer from current time
        tm.setTimerStart(LocalDateTime.now());
        tm.setPaused(false);

        return taskRepo.save(tm);
    }

    /**
     * Deletion logic for a task.
     * <p>
     *     If the task is a parent task, all of the subtasks are deleted first.
     *     If any of those is a parent task as well, the method is called recursively
     *     until all of the tasks have been deleted.
     * </p>
     * @param id task id
     */
    public void deleteTask(Long id) {
        List<TaskManager> subtasks=taskRepo.findByParentTaskId(id);
        for (TaskManager subtask:subtasks) {
            //recursive deletion of the subtasks
            deleteTask(subtask.getId());
        }
        taskRepo.deleteById(id);
    }
}
