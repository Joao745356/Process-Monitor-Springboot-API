package com.bpmonitor.repositories;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bpmonitor.enums.TaskRecurrence;
import com.bpmonitor.enums.TaskStatus;
import com.bpmonitor.enums.TaskType;
import com.bpmonitor.models.Task;




/**
 * task responsible for CRUD operations related to task. 
 * @author joao7
 *
 */

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{//extends JPARepository<Task, Integer> 
	
	/** built in repo methods from springboot
	 * 
	 * Method Signature	Description
	 *	T save(T entity)							Save or update an entity
	 *	List<T> findAll()							Find all entities
	 *	Optional<T> findById(ID id)					Find one by ID
	 *	boolean existsById(ID id)					Check existence by ID
	 *	long count()								Count all entities
	 *	void deleteById(ID id)						Delete by ID
	 *	void delete(T entity)						Delete a specific entity
	 *	void deleteAll()							Delete all entities
	 *	void deleteAll(Iterable<? extends T>)		Delete multiple entities
	 *	List<T> findAllById(Iterable<ID>)			Find multiple by IDs
	 *	List<T> saveAll(Iterable<T>)				Save a list of entities
	 * 
	 */
	

	/**
	 * finds all tasks of a certain type
	 * @param taskType - type of task we wanna check for 
	 * @return a list containing all tasks of that type
	 */
    //List<Task> findByTaskType(TaskType taskType);
    
    /**
  	 * ESTES MÉTODOS SÃO FEITOS SEGUINDO A TABELA https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
  	 * ESTE NAO TEM UNDERSCORE PORQUE RECURRENCE ESTÁ EM TASK!
  	 */
    
    /**
     * returns all tasks of a certain recurrence.
     * @param recurrence - recurrence of a task we wanna check for.
     * @return a list containing all tasks with that recurrence.
     */
    List<Task> findTaskByRecurrence(TaskRecurrence recurrence);
    
    /**
     * returns all tasks of a certain status.
     * @param status - status of a task we wanna check for.
     * @return a list containing all tasks with that status.
     */
    List<Task> findTaskByTaskStatus(TaskStatus status);
    
    
    /**
	 * ESTES MÉTODOS SÃO FEITOS SEGUINDO A TABELA https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
	 * E FUNCIONAM BY param1daclasse1_atributoQueQueroDaClasse1
	 */
    
    /**
     * Fetches all Tasks of a certain activity.
     * @param activityId - ID of the activity we wanna check.
     * @return a list of tasks that belong to that activity.
     */
    List<Task> findTaskByActivity_ActivityID(Long activityId);
	
    /**alternative
    @Query("SELECT t FROM Task t WHERE t.activity.activityID = :activityId")
    List<Task> findTaskByActivity_ActivityID(Long activityId); */
    
    /**
     * counts all tasks belonging to an acitivity
     * @params ActivityID - activity to which we wish to check.
     * @return int value representing the count of tasks belonging to the task.
     */
    public int countTasksByActivity_ActivityID(Long ActivityID);
    
    /**
     * deletes all tasks belonging to an activity.
     * @param ActivityID - Activity whose tasks we want to delete. 
     */
    public void deleteTasksByActivity_ActivityID(Long ActivityID);
    
    
    /**
     * Find All tasks that belong to a subprocess.
     * @param subprocessID - subprocess to which we wanna check their tasks.
     * @return list of all tasks belonging to that subprocess
     */
    public List<Task> findTaskByActivity_Subprocess_SubprocessID(Long subprocessID);

    /**
     * Find All tasks that belong to a system. 
     * @param id - system this task tests
     * @return - list of all task that test this system.
     */
	public List<Task> findBySystem_EdprSystemID(Long id);

	/**
     * Find All tasks that belong to an interface. 
     * @param id - system this task tests
     * @return - list of all task that test this system.
     */
	public List<Task> findByEdprInterface_EdprInterfaceID(Long id);

	

    
}
