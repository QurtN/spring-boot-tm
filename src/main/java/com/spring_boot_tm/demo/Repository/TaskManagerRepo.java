package com.spring_boot_tm.demo.Repository;

import com.spring_boot_tm.demo.entity.TaskManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for task persistence.
 * <p>
 *     This class provides database operations for:
 * </p>
 * <ul>
 * <li>creating tasks</li>
 * <li>updating tasks</li>
 * <li>deleting tasks</li>
 * <li>retrieving tasks</li>
 * <li>handling parent/subtask relations</li>
 * </ul>
 *
 * <p>
 *     Uses Spring Data JPA to automatically generate common CRUD operations.
 * </p>
 */
public interface TaskManagerRepo extends JpaRepository<TaskManager, Long>{
    /**
     * Finds all subtasks belonging to a parent task.
     * <p>Used for recursive deletion, parent completion as well as subtask retrieval.</p>
     * @param parentId task id of parent task
     * @return list of all subtasks belonging to parent task
     */
    List<TaskManager> findByParentTaskId(Long parentId);
}
