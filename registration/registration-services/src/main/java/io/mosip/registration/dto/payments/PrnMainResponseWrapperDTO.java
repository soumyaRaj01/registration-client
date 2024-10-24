package io.mosip.registration.dto.payments;

import java.io.Serializable;
import java.util.List;


import io.mosip.kernel.core.exception.ServiceError;
import lombok.Data;

@Data
public class PrnMainResponseWrapperDTO<T> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private String version;
	private String responsetime;

	private T response;
	
	/** The error details. */
	private List<ServiceError> errors;
}
