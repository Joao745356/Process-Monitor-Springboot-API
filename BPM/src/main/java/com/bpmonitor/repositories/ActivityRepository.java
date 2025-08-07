package com.bpmonitor.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bpmonitor.enums.OperationalStatus;
import com.bpmonitor.models.Activity;
import com.bpmonitor.models.Subprocess;


/**
 * Repository interface for acessing and performing operations on Activity entities.
 * It provides methods to find by activities by name, status, by subprocessID, 
 * deleting activities related to a subprocess, as well as count activities by their status.
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long>{
	
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
	 * possible custom methods
	 * Method Name	Description
			findByFieldName(Type fieldValue)	Find by a field
			findByFieldNameAndOtherField(...)	Use multiple conditions
			findByFieldNameContaining(String str)	LIKE %str% (for partial matches)
			findByFieldNameIgnoreCase(String str)	Case-insensitive match
			countByFieldName(...)	Count by field
			deleteByFieldName(...)	Delete using a condition
			existsByFieldName(...)	Returns true/false if entity with field exists
			findTop1ByOrderByFieldDesc()	Get the latest or highest value entity
			findFirst10ByFieldOrderByFieldAsc(...)	Get a limited sorted list
			findByFieldIn(Collection<Type> values)	WHERE field IN (...)
			findByFieldBetween(A, B)	WHERE field BETWEEN A AND B
	 */
	
	/**
	 *
	 * should return all activities with the following status.
	 * if you wish to return all activities that are UP, DOWN or COMPROMISED.
	 * 
	 *  UP,  // the system is up and running
	 *	DOWN, // the system is down
	 *	COMPROMISED // the system is compromised (unsure on how to implement this one)
	 * 
	 * @param activityStatus - UP, DOWN, COMPROMISED are acceptable values 
	 * @return list of activities with the given status in param activityStatus
	 */
	List<Activity> findActivityByActivityStatus(OperationalStatus activityStatus);
	
	
	/**
	 * fetches from Database all activities that belong to the subprocess
	 * with the given subprocessID.
	 * 
	 * @param subprocessID - subprocess that owns this activity
	 * @return returns all activities belonging to 1 subprocess
	 */
	List<Activity> findActivityBySubprocess_SubprocessID(Long subprocessID);
	
	/**
	 * 
	 * @param name - name I wanna search an activity by.
	 * @return a list with all activities sharing the same name.
	 */
	List<Activity> findActivityByActivityName(String name);
	
	/**
	 * 
	 * @param idStart
	 * @param idEnd
	 * @return all activities between idStart and end.
	 */
	List<Activity> findActivityByActivityIDBetween(Long idStart, long idEnd);
	
	
	/**
	 *  deletes all Activities belonging to a subprocess.
	 * @param subprocess of the activity
	 * @return number of deleted activities
	 */
	void deleteActivityBySubprocess(Subprocess subprocessID);
	
	
	/**
	 * deletes all activies belonging to subprocess.id
	 * @param subprocessID - id of the subprocess whose activities I wish to delete
	 * @return number of activities deleted
	 */
	void deleteActivityBySubprocess_SubprocessID(Long subprocessID);
	
	/**
	 * Counts all activities with a determined status.
	 * @param status - The operational status to filter by (UP, DOWN, or COMPROMISED).
	 * @return the count of activities matching the given status.
	 */
	int countActivityByActivityStatus(OperationalStatus status);
	
	
	
	
}
