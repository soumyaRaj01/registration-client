package io.mosip.registration.dto.payments;

import java.io.Serializable;

import lombok.Data;

@Data
public class CheckPRNInTransLogsRequestDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String prn;
}
