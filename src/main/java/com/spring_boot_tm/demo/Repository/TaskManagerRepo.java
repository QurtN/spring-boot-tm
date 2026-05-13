package com.spring_boot_tm.demo.Repository;

import com.spring_boot_tm.demo.entity.TaskManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TaskManagerRepo extends JpaRepository<TaskManager, Long>{
    List<TaskManager> findByParentTaskId(Long parentId);
}
