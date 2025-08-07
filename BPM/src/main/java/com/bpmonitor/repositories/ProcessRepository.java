package com.bpmonitor.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bpmonitor.enums.OperationalStatus;
import com.bpmonitor.models.Process;


/**
 * Repository interface for accessing and performing operations on Process entities.
 * This interface provides methods to find Processes by name, process ID, or status, 
 * as well as methods for counting and deleting processes.
 */
@Repository
public interface ProcessRepository extends JpaRepository<Process, Long>{

	/**
	 * Fetches all process with a given name.
	 * @param name - name of a process
	 * @return List of all processes with the name in the DB.
	 */
	public List<Process> findProcessByProcessName(String name);
	
	/**
	 * Fetches all processes with a given status.
	 * @param status - can be UP, DOWN, COMPROMISED.
	 * @return List of all processes with the given status in the DB.
	 */
	public List<Process> findProcessByProcessStatus(OperationalStatus status);
	
	/**
	 * Deletes all processes from the database with the given name.
	 * @param name
	 */
	public void deleteProcessByProcessName(String name);

	/**
	 * Checks if a given process with said name exists in the DB.
	 * @param name - name I wanna check for in the DB.
	 * @return true if it exists, false if not.
	 */
	public boolean existsByProcessName(String name);
	
	/**
	 * counts all processes with a given status.
	 * @param  status - can be UP, DOWN, COMPROMISED.
	 * @return int value of the count.
	 */
	public int countProcessesByProcessStatus(OperationalStatus status);
}
