package com.bpmonitor.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bpmonitor.enums.TaskStatus;
import com.bpmonitor.models.TaskValidationResult;

/**
 * 
 */
@Repository
public interface TaskValidationResultRepository extends JpaRepository<TaskValidationResult, Long>{

	/**
	 * This method fetches all the taskValidationResults of a certain task.
	 * @param taskID - ID of the task we want to check the results of.
	 * @return List<TaskValidationResult> of all the results of this task.
	 */
	public List<TaskValidationResult> findTaskValidationResultByTask_TaskID(Long taskID);
	
	/**
	 * This method fetches all taskValidatioResults that belong to a task by it's name.
	 * @param name - name of the Task we want to check the results of.
	 * @return List<TaskValidationResult> of all the results of this task.
	 */
	public List<TaskValidationResult> findTaskValidationResultByTask_TaskName(String name);
	
	/**
	 * This method fetches all taskValidatioResults that contain the desired result
	 * @param status - status we want to check for.
	 * @return List<TaskValidationResult> of all the results with given status.
	 */
	public List<TaskValidationResult> findTaskValidationResultByStatus(TaskStatus status);
	
	/**
	 * This method fetches all TaskValidationResults that sit between timeStart and timeEnd
	 * @param timeStart - LocalDateTime we want to start at.
	 * @param timeEnd - LocalDateTime we want to end at.
	 * @return List<TaskValidationResult> of all the results in the given time period.
	 */
	public List<TaskValidationResult> findTaskValidationResultByTimestampBetween(LocalDateTime timeStart, LocalDateTime timeEnd);
	
	//  add this method in service layer -> public void deleteTaskValidationResultByStatus();
	
	
	/**
	 * deletes all the taskResults belonging to a task by it's ID.
	 * @param taskID - ID of the task whose results we wish to delete.
	 */
	public void deleteTaskValidationResultByTask_TaskID(Long taskID);
	
	/**
	 * counts all the TaskValidationResult rows with the given TaskStatus.
	 * @param status - status we wish to count for, can be 
	 *  UNRUN,    
     *  SUCCESS,  
     *  FAIL   
	 * @return - int value representing the number of taskResults with the result.
	 */
	public int countTaskValidationResultByStatus(TaskStatus status);
	
	
	/**
	 * checks if TaskResults for a given task exist.
	 * @param TaskID - ID of the task we wish to check for.
	 * @return true if there are results, false if there aren't.
	 */ 
	public boolean existsTaskValidationResultByTask_TaskID(Long TaskID);
	
	
	/**
	 * returns all TaskValidationResult before X date.
	 * @param status - status we wanna check for.
	 * @param howLongAgo - LocalDateTime of how long ago we wished to look for.
	 * @return
	 */
	public List<TaskValidationResult> findByStatusAndTimestampBefore(TaskStatus status, LocalDateTime howLongAgo);

	
	/**
	 * returns latest results for a task.
	 * @param taskId
	 * @return
	 */
	public Optional<TaskValidationResult> findTopByTask_TaskIDOrderByTimestampDesc(Long taskId);
	
	
	
}
