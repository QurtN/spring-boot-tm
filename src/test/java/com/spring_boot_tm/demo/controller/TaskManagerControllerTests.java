package com.spring_boot_tm.demo.controller;

import tools.jackson.databind.ObjectMapper;
import com.spring_boot_tm.demo.Repository.TaskManagerRepo;
import com.spring_boot_tm.demo.dto.TaskRequest;
import com.spring_boot_tm.demo.entity.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 Integration tests for TaskManagerController.
 Tests:
 -GET endpoints
 -POST endpoints
 -DELETE endpoints
 -Timer endpoints
 -JSON request/response handling
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TaskManagerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskManagerRepo taskRepo;

    @Autowired
    private ObjectMapper objectMapper;

    /*
    Clean up database after every test.
     */
    @BeforeEach
    void setup() {
        taskRepo.deleteAll();
    }

    /*
     TEST: Should create a task through REST API
     */
    @Test
    void shouldCreateTask() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("Angular lernen :)");
        request.setCompleted(false);
        request.setDurationSeconds(3600L);

        mockMvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON).
                        content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Angular lernen :)"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    /*
    TEST: endpoint should return all tasks
     */
    @Test
    void shouldReturnAllTasks() throws Exception {
        TaskManager task1=new TaskManager("Lernen", false);
        TaskManager task2=new TaskManager("Workout", false);

        taskRepo.save(task1);
        taskRepo.save(task2);

        mockMvc.perform(get("/tasks")).andExpect(status().isOk()).andExpect(jsonPath("$[0].title")
                                .value("Lernen"))
                .andExpect(jsonPath("$[1].title").value("Workout"));
    }

    /*
     TEST: Should delete task through API.
     */
    @Test
    void shouldDeleteTask() throws Exception {
        TaskManager task=new TaskManager("Lösch mich", false);

        taskRepo.save(task);

        mockMvc.perform(delete("/tasks/" + task.getId())).andExpect(status().isOk());
        assertEquals(0, taskRepo.findAll().size());
    }

    /*
    TEST: Create subtask to a parent task
     */
    @Test
    void shouldCreateSubtask() throws Exception {
        TaskManager parent=new TaskManager("Kochen", false);
        taskRepo.save(parent);
        TaskRequest request=new TaskRequest();
        request.setTitle("Einkaufen");
        request.setParentTaskId(parent.getId());
        mockMvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON)

                                .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Einkaufen"))
                .andExpect(jsonPath("$.parentTask.id").value(parent.getId()));
    }

    /*
    TEST: should start the timer
     */
    @Test
    void shouldStartTimer() throws Exception {
        TaskManager task=new TaskManager("Lernen", false);
        task.setDurationSeconds(3600L);
        taskRepo.save(task);
        mockMvc.perform(post("/tasks/" + task.getId() + "/start"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.paused").value(false));
    }

    /*
    TEST: should pause the timer
     */
    @Test
    void shouldPauseTimer() throws Exception {
        TaskManager task=new TaskManager("Lernen", false);
        task.setDurationSeconds(3600L);
        task.setPaused(false);
        task.setTimerStart(LocalDateTime.now().minusMinutes(1));
        task.setPaused(false);
        taskRepo.save(task);

        mockMvc.perform(post("/tasks/" + task.getId() + "/pause"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.paused").value(true));
    }

    /*
    TEST: should resume the timer
     */
    @Test
    void shouldResumeTimer() throws Exception {

        TaskManager task=new TaskManager("Lernen", false);
        task.setDurationSeconds(3600L);
        task.setPaused(true);
        taskRepo.save(task);
        mockMvc.perform(post("/tasks/" + task.getId() + "/resume"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.paused").value(false));
    }

    /*
    TEST: should save dueDate variable
     */
    @Test
    void shouldSaveDueDate() throws Exception {

        TaskRequest request=new TaskRequest();

        request.setTitle("Klausur");

        request.setDueDate(LocalDateTime.of(2026, 6, 1, 23, 21));
        mockMvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.dueDate").exists());
    }

    /*
    TEST: reject invalid parent task
     */
    @Test
    void shouldRejectInvalidParentTask() throws Exception {

        TaskRequest request=new TaskRequest();

        request.setTitle("Subtask");
        request.setParentTaskId(999L);
        mockMvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    /*
    TEST: should return an empty list, when no task exists
     */
    @Test
    void shouldReturnEmptyTaskList() throws Exception {
        mockMvc.perform(get("/tasks")).andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
