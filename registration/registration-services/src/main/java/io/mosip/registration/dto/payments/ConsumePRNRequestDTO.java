package io.mosip.registration.dto.payments;

import java.io.Serializable;

import lombok.Data;

@Data
public class ConsumePRNRequestDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String regId;
	private String prn;
}
