package com.bpmonitor.DTOs.response;
import com.bpmonitor.enums.OperationalStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * this DTO represents an EDPRInterface without loading
 * the tasks in order to avoid the overload of data.
 * @author joao7
 *
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterfaceTreeNodeDTO {
	private Long InterfaceID;
	private String InterfaceName;
	private Long originId;
	private Long destinationId;
	private OperationalStatus status;
}
