package com.bpmonitor.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bpmonitor.enums.OperationalStatus;
import com.bpmonitor.models.Subprocess;


/**
 * Repository interface for accessing and performing operations on Subprocess entities.
 * This interface provides methods to find subprocesses by name, process ID, or status, 
 * as well as methods for counting and deleting subprocesses related to a particular process.
 */
@Repository
public interface SubprocessRepository extends JpaRepository<Subprocess, Long> {

	/**
	 * Fetches subprocesses from the DB with the given name.
	 * @param name - name of the subprocess.
	 * @return a list of subprocesses with the name passed.
	 */
	public List<Subprocess> findSubprocessBySubprocessName(String name);
	
	/**
	 * Fetches all Subprocesses tied to one process.
	 * @param processsID - ID of the process I want the subprocesses tied to.
	 * @return a list of all subprocesses tied to a determined process.
	 */
	public List<Subprocess> findSubprocessByProcess_ProcessID(Long processID);
	
	/**
	 * Fetches all subprocesses from the DB with the given OperationalStatus.
	 * @param status - can be UP, DOWN or COMPROMISED.
	 * @return a list of subprocesses whose status matches the passed status.
	 */
	public List<Subprocess> findSubprocessBySubprocessStatus(OperationalStatus status);
	
	/**
	 * Deletes all subprocesses tied to a process by it's ID.
	 * @param processID - ID of the process whose subprocesses we want to delete.
	 */
	public void deleteSubprocessByProcess_ProcessID(Long processID);
	
	/**
	 * Counts all subprocesses tied to a determined process.
	 * @param processID - ID of the process whose subprocesses I wanna count.
	 * @return int value representing the number of subprocesses tied to the process.
	 */
	public int countSubprocessByProcess_ProcessID(Long processID);
	
	/**
	 * Counts all subprocess with the given status.
	 * @param status - can be UP, DOWN, COMPROMISED.
	 * @return an int value representing the number of subprocesses
	 * whose OperationalStatus matches the passed status.
	 */
	public int countSubprocessBySubprocessStatus(OperationalStatus status);
}
