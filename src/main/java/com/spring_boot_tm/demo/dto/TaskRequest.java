package com.spring_boot_tm.demo.dto;

import java.time.LocalDateTime;


public class TaskRequest {

    private String title;

    private Boolean completed;

    private Long parentTaskId;

    private LocalDateTime dueDate;

    private Long durationSeconds;

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
