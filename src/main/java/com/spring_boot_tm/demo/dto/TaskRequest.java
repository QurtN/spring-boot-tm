package com.spring_boot_tm.demo.dto;

import java.time.LocalDateTime;

/**
 * DTO used for task creation requests.
 * <p>
 *     Separates incoming API request data from JSONs
 *     from the database entity model (the TaskManager class).
 * </p>
 */
public class TaskRequest {

    /**
     * Title of the task
     */
    private String title;
    /**
     * Optional completion state
     */
    private Boolean completed;
    /**
     * parent task id of an optional parent task
     */
    private Long parentTaskId;
    /**
     * Optional due date for task completion
     */
    private LocalDateTime dueDate;
    /**
     * Optional timer duration in seconds
     */
    private Long durationSeconds;

    /**
     * Default constructor for the TaskRequest class
     */
    public TaskRequest() {
    }



    public String getTitle() {
        return title;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public Long getParentTaskId() {
        return parentTaskId;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public Long getDurationSeconds() {
        return durationSeconds;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public void setParentTaskId(Long parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public void setDurationSeconds(Long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
}
