package com.spring_boot_tm.demo.service;

import com.spring_boot_tm.demo.Repository.TaskManagerRepo;
import com.spring_boot_tm.demo.dto.TaskRequest;
import com.spring_boot_tm.demo.entity.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskManagerRepo tmRepo;

    //Cleanup database before every test
    @BeforeEach
    void setup() {
        tmRepo.deleteAll();
    }
/*
--------------------------------Tasks creation tests---------------------------------------------------------------------
 */
    /*
    TEST: Basic task creation
     */
    @Test
    void shouldCreateSimpleTask() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Meine coole Web-App entwickeln");
        request.setCompleted(false);
        TaskManager createdTask = taskService.createTask(request);
        assertNotNull(createdTask.getId());

        assertEquals("Meine coole Web-App entwickeln", createdTask.getTitle());
        assertFalse(createdTask.isCompleted());
    }

    /*
     TEST: creating a subtask and referencing it to a parent Task
     */
    @Test
    void shouldCreateSubtaskWithParent() {
        TaskManager parent = new TaskManager("Kochen", false);
        tmRepo.save(parent);
        //when we want to create a subtask, we always have to go through the TaskRequest class
        //Else, we might run into problems with resolving JSON arguments properly
        TaskRequest request = new TaskRequest();
        request.setTitle("Einkaufen");
        request.setParentTaskId(parent.getId());
        TaskManager subtask = taskService.createTask(request);

        assertNotNull(subtask.getParentTask());
        assertEquals(parent.getId(), subtask.getParentTask().getId());
    }

    /*
    TEST: Completion of a parent task, when all subtasks are set to be completed
     */
    @Test
    void parentTaskShouldCompleteWhenAllSubtasksComplete() {
        TaskManager parent =  new TaskManager("Kochen", false);
        tmRepo.save(parent);
        TaskManager subtask1 = new TaskManager("Einkaufen", true);
        subtask1.setParentTask(parent);
        TaskManager subtask2 = new TaskManager("Geschirr waschen", true);
        subtask2.setParentTask(parent);
        tmRepo.save(subtask1);
        tmRepo.save(subtask2);
        taskService.updateParentCompletion(parent.getId());
        TaskManager updatedParent = tmRepo.findById(parent.getId()).orElseThrow();
        assertTrue(updatedParent.isCompleted());
    }

    /*
     TEST: Parent task stays incomplete if at least one subtask is incomplete.
     */
    @Test
    void shouldNotCompleteParentIfSubtaskIncomplete() {
        TaskManager parent = new TaskManager("Kochen", false);
        tmRepo.save(parent);
        TaskManager subtask1 = new TaskManager("Geschirr waschen", true);
        subtask1.setParentTask(parent);
        TaskManager subtask2 = new TaskManager("Zutaten vorbereiten", false);
        subtask2.setParentTask(parent);

        tmRepo.save(subtask1);
        tmRepo.save(subtask2);

        taskService.updateParentCompletion(parent.getId());
        TaskManager updatedParent = tmRepo.findById(parent.getId()).orElseThrow();

        assertFalse(updatedParent.isCompleted());
    }
/*
---------------------------------------Tests for startTimer-----------------------------------------------------
 */
    /*
     * TEST: Does the "startTimer" functionality of our Tasks properly work
     */
    @Test
    void shouldStartTimer() {
        TaskManager task = new TaskManager("Lernen", false);
        task.setDurationSeconds(3600L);
        tmRepo.save(task);
        TaskManager updatedTask = taskService.startTimer(task.getId());

        assertNotNull(updatedTask.getTimerStart());
        assertFalse(updatedTask.isPaused());
    }

    /*
     * TEST: Functionality of pausing the timer
     */
    @Test
    void shouldPauseTimer() {
        TaskManager task = new TaskManager("Lernen", false);

        task.setDurationSeconds(3600L);
        task.setTimerStart(LocalDateTime.now().minusMinutes(10));
        tmRepo.save(task);
        TaskManager updatedTask = taskService.pauseTimer(task.getId());

        assertTrue(updatedTask.isPaused());
        assertTrue(updatedTask.getElapsedSeconds() > 0);
    }

    /*
     TEST: Timer does not start twice
     */
    @Test
    void shouldNotStartTimerTwice() {

        TaskManager task = new TaskManager("Lernen", false);
        task.setDurationSeconds(3600L);
        tmRepo.save(task);
        //First start
        taskService.startTimer(task.getId());
        //save original date
        TaskManager startedTask = tmRepo.findById(task.getId()).orElseThrow();
        LocalDateTime originalStart = startedTask.getTimerStart();
        //second start
        taskService.startTimer(task.getId());
        TaskManager updatedTask = tmRepo.findById(task.getId()).orElseThrow();

        //assert that timerStart did not change by 0.05 seconds
        Duration difference = Duration.between(originalStart, updatedTask.getTimerStart());

        assertTrue(difference.toMillis() < 50);
    }

    /*
     TEST: Pause does not work if timer is not started
     */
    @Test
    void shouldThrowIfTimerNotStarted() {
        TaskManager task = new TaskManager("Lernen", false);
        tmRepo.save(task);
        //using the RunTimeException class, assert that we throw an error here
        assertThrows(RuntimeException.class,()->taskService.pauseTimer(task.getId()));
    }

    /*
   TEST: Reject timer operations when no duration exists.
    */
    @Test
    void shouldRejectTimerWithoutDuration() {
        TaskManager task=new TaskManager("Lernen", false);
        task.setDurationSeconds(0L);
        tmRepo.save(task);
        assertThrows(IllegalStateException.class,()->taskService.startTimer(task.getId()));
        assertThrows(IllegalStateException.class,()->taskService.pauseTimer(task.getId()));
        assertThrows(IllegalStateException.class,()->taskService.resumeTimer(task.getId()));
    }

    /*
    --------------------------------TESTS for proper deletion and exception handling--------------------------------------------
     */

    /*
     TEST: Recursive deletion of all subtasks and the parent task, when deleting a parent task
     */
    @Test
    void shouldDeleteParentAndSubtasks() {
        TaskManager parent = new TaskManager("Kochen", false);
        tmRepo.save(parent);
        TaskManager subtask1 = new TaskManager("Einkaufen", false);
        subtask1.setParentTask(parent);
        TaskManager subtask2 = new TaskManager("Geschirr waschen", false);
        subtask2.setParentTask(parent);

        tmRepo.save(subtask1);
        tmRepo.save(subtask2);

        taskService.deleteTask(parent.getId());
        //make sure that the database is completely empty (note that we check this within the database,
        //not needing any of our self-made classes to reduce error potential here
        List<TaskManager> remainingTasks = tmRepo.findAll();
        assertEquals(0, remainingTasks.size());
    }

    /*
     TEST: Proper functionality of dueDate saving
     */
    @Test
    void shouldSaveDueDate() {

        TaskRequest request = new TaskRequest();
        request.setTitle("Klausurvorbereitung");
        LocalDateTime dueDate = LocalDateTime.of(2026, 6, 1, 18, 0);
        request.setDueDate(dueDate);
        TaskManager task = taskService.createTask(request);

        assertEquals(dueDate, task.getDueDate());
    }

    /*
    TEST: defaultDuration is set correctly when creating a task
     */
    @Test
    void shouldUseDefaultDurationWhenNull() {

        TaskRequest request = new TaskRequest();
        request.setTitle("duration test");
        TaskManager task = taskService.createTask(request);

        assertEquals(0L, task.getDurationSeconds());
    }



    /*
     TEST: Negative duration should not be allowed
     */
    @Test
    void shouldRejectNegativeDuration() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Kaputte Task");
        request.setDurationSeconds(-100L);
        //using the RunTimeException class, assert that we throw an error here
        assertThrows(IllegalArgumentException.class,()->taskService.createTask(request));
    }

    /*
     TEST: Should throw exception when parent task does not exist.
     */
    @Test
    void shouldThrowWhenParentTaskNotFound() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Subtask");
        //This parent task will not exist
        request.setParentTaskId(999L);

        assertThrows(RuntimeException.class,()->taskService.createTask(request));
    }

    /*
    TEST: throw error if task does not exist, when starting timer
     */
    @Test
    void shouldThrowWhenTaskDoesNotExist() {
        assertThrows(RuntimeException.class, () -> taskService.startTimer(999L));
    }

    /*
    TEST: Timer sets the task to completed, when the duration time is surpassed
     */
    @Test
    void shouldCompleteTaskWhenTimerExpires() {
        TaskManager task = new TaskManager("Lernen", false);
        task.setDurationSeconds(60L);
        task.setTimerStart(LocalDateTime.now().minusMinutes(2));
        task.setPaused(false);
        tmRepo.save(task);
        TaskManager updatedTask = taskService.pauseTimer(task.getId());

        assertTrue(updatedTask.isCompleted());
    }

    /*
    TEST: Recursive deletion of subtasks with a parent task, with a subtask, with its own subtask again
     */
    @Test
    void shouldDeleteNestedSubtasksRecursively() {
        TaskManager parent = new TaskManager("Kochen", false);
        tmRepo.save(parent);
        //first subtask
        TaskManager subtask = new TaskManager("Einkaufen", false);
        subtask.setParentTask(parent);
        tmRepo.save(subtask);
        //subtask of the subtask
        TaskManager subsubtask = new TaskManager("Milch kaufen", false);
        subsubtask.setParentTask(subtask);
        tmRepo.save(subsubtask);

        //delete the biggest parent task
        taskService.deleteTask(parent.getId());
        //check if database is empty
        assertEquals(0, tmRepo.findAll().size());
    }

    /*
    -------------------------TESTS FOR resumeTimer()-------------------------------------------------------
     */


    /*
     TEST: Timer should resume correctly.
     */
    @Test
    void shouldResumeTimer() {
        TaskManager task = new TaskManager("Lernen", false);
        task.setPaused(true);
        //set the elapsed seconds to a value that is later to be checked
        task.setElapsedSeconds(120L);
        tmRepo.save(task);

        TaskManager updatedTask = taskService.resumeTimer(task.getId());

        assertFalse(updatedTask.isPaused());
        assertNotNull(updatedTask.getTimerStart());
        //previously elapsed seconds should remain the same
        assertEquals(120L, updatedTask.getElapsedSeconds());
    }

    /*
    TEST: Resuming the timer fails, if the timer is not paused.
     */
    @Test
    void shouldThrowIfTimerNotPaused() {
        TaskManager task = new TaskManager("Lernen", false);
        task.setPaused(false);
        tmRepo.save(task);

        assertThrows(IllegalStateException.class,()->taskService.resumeTimer(task.getId()));
    }

    /*
    TEST: Throw error if the task does not exist, when resuming the timer
     */
    @Test
    void shouldThrowIfResumeTaskDoesNotExist() {
        assertThrows(RuntimeException.class,()->taskService.resumeTimer(999L));
    }
}