package com.bpmonitor.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bpmonitor.enums.TaskStatus;
import com.bpmonitor.models.TaskError;

@Repository
/**
 * Repository interface for accessing and performing operations on TaskError entities.
 * This interface provides methods to find TaskError by their tasks, 
 * as well as methods for counting and deleting TaskErrors related to a particular Task.
 */
public interface TaskErrorRepository extends JpaRepository<TaskError, Long>{

	/**
	 * fetches all taskErrors for a determined TaskValidationResult
	 * @param taskID
	 * @return
	 */
	public List<TaskError> findTaskErrorByTaskValidationResult_Task_TaskID(Long taskID);
	
	/**
	 * fetches all taskErrors for a determined TaskValidationResult
	 * @param taskID - task to which I wanna count it's errors.
	 * @return int value with a count of all the task error for a given taskID
	 */
	public int countTaskErrorByTaskValidationResult_Task_TaskID(Long taskID);
	
	/**
	 * deletes all taskErrors for a determined Task.
	 * @param taskID - task to which I wanna delete it's errors.
	 */
	public void deleteTaskErrorByTaskValidationResult_Task_TaskID(Long taskID);

	/**
	 * 
	 * @param fail
	 * @param oneWeekAgo
	 * @return
	 */
	public List<TaskError> findByTimestampBefore( LocalDateTime oneWeekAgo);
}
