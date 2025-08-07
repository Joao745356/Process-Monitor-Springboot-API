package com.bpmonitor.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bpmonitor.enums.OperationalStatus;
import com.bpmonitor.models.System;


/**
 * Repository interface for acessing and performing operations on EDPRSystem entities.
 * It provides methods to find by systems by SystemName and status.
 *  It also includes count methods for systems by their status.
 */
@Repository
public interface SystemRepository extends JpaRepository<System, Long>{



	
	
	/**
	 * Fetches a list of all edprInterfaces with the given name.
	 * @param name - name I wanna fetch interfaces by
	 * @return a list of interfaces from the database
	 */
	public List<System> findEDPRSystemBySystemName(String name);
	
	/**
	 * Fetches a list of all edprInterfaces with a given status.
	 * @param status - can be (UP, DOWN, COMPROMISED.
	 * @return a list of interfaces from the database
	 */
	public List<System> findEDPRInterfaceByCurrentStatus(OperationalStatus status);
	
	/**
	 * counts all rows in the EDPRSystem table where status equals the given status.
	 * @param status - can be (UP, DOWN, COMPROMISED.
	 * @return a count of all Interfaces with the current status
	 */
	public int countEDPRSystemsByCurrentStatus(OperationalStatus status);
}
