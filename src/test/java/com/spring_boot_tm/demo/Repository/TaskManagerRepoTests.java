package com.spring_boot_tm.demo.Repository;

import com.spring_boot_tm.demo.entity.TaskManager;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TaskManagerRepoTests {


    @Autowired
    private TaskManagerRepo taskRepo;

    /*
    TEST: should save tasks properly
     */
    @Test
    void shouldSaveTask() {
        TaskManager task=new TaskManager("Lernen", false);
        TaskManager savedTask=taskRepo.save(task);
        assertNotNull(savedTask.getId());
        assertEquals("Lernen", savedTask.getTitle());
    }

    /*
    TEST: should find a subtask by the ID of the parent task
     */
    @Test
    void shouldFindSubtasksByParentId() {
        TaskManager parent=new TaskManager("Kochen", false);
        taskRepo.save(parent);
        TaskManager subtask=new TaskManager("Einkaufen", false);

        subtask.setParentTask(parent);
        taskRepo.save(subtask);

        List<TaskManager> subtasks=taskRepo.findByParentTaskId(parent.getId());

        assertEquals(1,subtasks.size());
        assertEquals("Einkaufen",subtasks.get(0).getTitle());
    }

    /*
    TEST: Should return an empty list if parent task has no subtasks
     */
    @Test
    void shouldReturnEmptyListIfNoSubtasksExist() {
        TaskManager parent=new TaskManager("Kochen", false);
        taskRepo.save(parent);
        List<TaskManager> subtasks=taskRepo.findByParentTaskId(parent.getId());

        assertTrue(subtasks.isEmpty());
    }

    /*
    TEST: should save Parent and subtask relationship
     */
    @Test
    void shouldSaveParentSubtaskRelationship() {

        TaskManager parent=new TaskManager("Fitness", false);
        taskRepo.save(parent);

        TaskManager subtask=new TaskManager("Pushups", false);
        subtask.setParentTask(parent);
        taskRepo.save(subtask);
        TaskManager savedSubtask= taskRepo.findById(subtask.getId()).orElseThrow();

        assertNotNull(savedSubtask.getParentTask());
        assertEquals("Fitness", savedSubtask.getParentTask().getTitle());
    }

    /*
    TEST: should delete a task correctly
     */
    @Test
    void shouldDeleteTask() {
        TaskManager task=new TaskManager("Lösch mich", false);
        taskRepo.save(task);
        taskRepo.deleteById(task.getId());
        boolean exists=taskRepo.findById(task.getId()).isPresent();

        assertFalse(exists);
    }

    /*
    TEST: should save the durationSeconds variable
     */
    @Test
    void shouldSaveDurationSeconds() {
        TaskManager task=new TaskManager("Lernen", false);
        task.setDurationSeconds(3600L);
        taskRepo.save(task);
        TaskManager savedTask = taskRepo.findById(task.getId()).orElseThrow();

        assertEquals(3600L, savedTask.getDurationSeconds());
    }

    /*
    TEST: should save the complete state of the task, i.e. the completed boolean
     */
    @Test
    void shouldSaveCompletedState() {
        TaskManager task=new TaskManager("Workout", true);

        taskRepo.save(task);
        TaskManager savedTask=taskRepo.findById(task.getId()).orElseThrow();

        assertTrue(savedTask.isCompleted());
    }

    /*
    TEST: should save the paused state (boolean) of the task
     */
    @Test
    void shouldSavePausedState() {

        TaskManager task=new TaskManager("Lernen", false);
        task.setPaused(true);
        taskRepo.save(task);

        TaskManager savedTask = taskRepo.findById(task.getId()).orElseThrow();

        assertTrue(savedTask.isPaused());
    }

    /*
    TEST: should find all of the tasks saved in the database
     */
    @Test
    void shouldFindAllTasks() {
        TaskManager task1=new TaskManager("Task 1", false);
        TaskManager task2=new TaskManager("Task 2", false);

        taskRepo.save(task1);
        taskRepo.save(task2);

        List<TaskManager> tasks = taskRepo.findAll();

        assertEquals(2, tasks.size());
    }

    /*
    TEST: should be able to handle multiple subtasks
     */
    @Test
    void shouldHandleMultipleSubtasks() {
        TaskManager parent=new TaskManager("Programming", false);

        taskRepo.save(parent);
        TaskManager subtask1 =new TaskManager("Backend", false);

        subtask1.setParentTask(parent);

        TaskManager subtask2 = new TaskManager("Frontend", false);
        subtask2.setParentTask(parent);

        taskRepo.save(subtask1);
        taskRepo.save(subtask2);

        List<TaskManager> subtasks = taskRepo.findByParentTaskId(parent.getId());

        assertEquals(2, subtasks.size());
    }
}
