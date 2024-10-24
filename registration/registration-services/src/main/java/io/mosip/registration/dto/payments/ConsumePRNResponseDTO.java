package io.mosip.registration.dto.payments;

import java.io.Serializable;

import lombok.Data;

@Data
public class ConsumePRNResponseDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String prnNum;
	private boolean consumedSucess;
	private String regIdTaggedToPrn;

}
