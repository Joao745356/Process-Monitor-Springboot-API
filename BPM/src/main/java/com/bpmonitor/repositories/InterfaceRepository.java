package com.bpmonitor.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bpmonitor.enums.OperationalStatus;
import com.bpmonitor.models.Interface;
import com.bpmonitor.models.System;

/**
 * Repository interface for acessing and performing operations on EDPRInterface entities.
 * It provides methods to find by interfaces by origin, destination, by interfaceName, status.
 *  It also includes count methods for interfaces by their status.
 */
@Repository
public interface InterfaceRepository extends JpaRepository<Interface, Long>{

	
	
	
	/**
	 * Fetches a list of all edprInterfaces that originate in a certain system.
	 * @param sysOrigin - the system this interface originates from
	 * @return a list of interfaces from the database
	 */
	public List<Interface> findEDPRInterfaceByOrigin(System sysOrigin);
	
	/**
	 * Fetches a list of all edprInterfaces that connect to a certain system.
	 * @param sysDestiny - the system this interface originates from
	 * @return a list of interfaces from the database
	 */
	public List<Interface> findEDPRInterfaceByDestination(System sysDestiny);
	
	/**
	 * Fetches a list of all edprInterfaces with the given name.
	 * @param name - name I wanna fetch interfaces by
	 * @return a list of interfaces from the database
	 */
	public List<Interface> findEDPRInterfaceByEdprInterfaceName(String name);
	
	/**
	 * Fetches a list of all edprInterfaces with a given status.
	 * @param status - can be (UP, DOWN, COMPROMISED.
	 * @return a list of interfaces from the database
	 */
	public List<Interface> findEDPRInterfaceByCurrentStatus(OperationalStatus status);
	
	/**
	 * counts all rows in the edprInterface table where status equals the given status.
	 * @param status - can be (UP, DOWN, COMPROMISED.
	 * @return a count of all Interfaces with the current status
	 */
	public int countEDPRInterfacesByCurrentStatus(OperationalStatus status);

	/**
	 * deletes all interfaces associated with the given system.
	 * @param edprSystem
	 */
	public void deleteByOriginOrDestination(System edprSystem, System edprSystem2);

	/**
	 * fetches all interfaces associated with either system.
	 * @param edprSystem
	 * @param edprSystem2
	 * @return
	 */
	public List<Interface> findByOriginOrDestination(System edprSystem, System edprSystem2);

	 /**
     * finds all interfaces with origin in this system.
     * @param system
     * @return
     */
	public List<Interface> findByOrigin(System system);


	/**
     * finds all interfaces with destination in this system.
     * @param system
     * @return
     */
	public List<Interface> findByDestination(System system);
	
	
}
