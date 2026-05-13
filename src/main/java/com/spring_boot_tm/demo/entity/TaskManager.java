package com.spring_boot_tm.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class TaskManager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private boolean completed = false;

    //Grouping tasks into groups of tasks

    @ManyToOne
    @JoinColumn(name = "parentTaskId")
    private TaskManager parentTask;

    //Time functionality

   private LocalDateTime dueDate;

    //Time in seconds
    private Long durationSeconds = 0L;

    //start time of Timer
    private LocalDateTime timerStart;

    //variable to confirm pausing
    private boolean paused = false;

    //keeping track of already elapsed time
    private Long elapsedSeconds = 0L;



    public TaskManager() {
    }


    public TaskManager(String title, boolean completed) {
        this.title = title;
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setParentTask(TaskManager parentTask) {
        this.parentTask = parentTask;
    }

    public TaskManager getParentTask() {
        return parentTask;
    }

   public LocalDateTime getDueDate() {
       return dueDate;
   }
   public void setDueDate(LocalDateTime dueDate) {
       this.dueDate = dueDate;
   }
   public LocalDateTime getTimerStart() {
       return timerStart;
   }

   public void setTimerStart(LocalDateTime timerStart) {
       this.timerStart = timerStart;
   }

    public Long getElapsedSeconds() {
        return elapsedSeconds;
    }

    public void setElapsedSeconds(Long elapsedSeconds) {
        this.elapsedSeconds = elapsedSeconds;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public Long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }


}
