package com.spring_boot_tm.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This entity handles the core structure of the tasks.
 * <p>
 * It supports normal tasks, timed tasks (with durationSeconds>0) and
 * hierarchical structures of task with a parent->subtask relationship.
 * It also tracks the completion of the tasks.
 * </p>
 *
 * <p>
 *     Every task can optionally belong to a parent task and
 *     may also contain timer functionality.
 * </p>
 */
@Entity
public class TaskManager {
    /**
     * Unique id of the task
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Title of the task
     */
    private String title;

    /**
     * Indicates whether the task is completed.
     */
    private boolean completed = false;

    /*
    Parent task used for task grouping.
    For example:

    Parent task: Kochen
    Subtasks: Einkaufen, Geschirr waschen
     */
    @ManyToOne
    @JoinColumn(name = "parentTaskId")
    private TaskManager parentTask;

    /**
     * Optional dueDate for task completion
     */

   private LocalDateTime dueDate;

    /**
     * Total timer duration in seconds.
     */
    private Long durationSeconds = 0L;

    /**
     * Timestamp when the timer has been started.
     */
    private LocalDateTime timerStart;

    /**
     * Variable to confirm when the timer has been paused.
     */
    private boolean paused = false;

    /**
     * Total elapsed timer duration in seconds.
     * Used to set completion status to true once it surpasses the duration.
     */
    private Long elapsedSeconds = 0L;


    /**
     * Default constructor needed for JPA.
     */
    public TaskManager() {
    }

    /**
     * Creates a task with a given title and completion state.
     * @param title name or title of the task
     * @param completed Completion status of the task
     */
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
