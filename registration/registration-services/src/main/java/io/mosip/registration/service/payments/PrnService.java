package io.mosip.registration.service.payments;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.dto.payments.CheckPRNInTransLogsRequestDTO;
import io.mosip.registration.dto.payments.CheckPRNInTransLogsResponseDTO;
import io.mosip.registration.dto.payments.CheckPRNStatusRequestDTO;
import io.mosip.registration.dto.payments.CheckPRNStatusResponseDTO;
import io.mosip.registration.dto.payments.ConsumePRNRequestDTO;
import io.mosip.registration.dto.payments.ConsumePRNResponseDTO;
import io.mosip.registration.dto.payments.PrnMainResponseWrapperDTO;


/**
 * This service class helps in the checking of PRN status and consuming of PRNs in the 
 * NIRA Payment Gateway service
 * 
 * 
 * @author Ibrahim Nkambo
 */


@Service
public class PrnService {
	
	private static final Logger LOGGER = AppConfig.getLogger(PrnService.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${nira.payment.gateway.service.check-prn-status}")
	private String urlCheckPrnStatus;
	
	@Value("${nira.payment.gateway.service.check-transaction-logs}")
	private String urlCheckTransLog;
	
	@Value("${nira.payment.gateway.service.consume-prn}")
	private String urlConsumePrn;
	
	@Autowired
	ObjectMapper objectMapper;

	public CheckPRNStatusResponseDTO checkPRNStatus(String prn) {
	    CheckPRNStatusRequestDTO requestDTO = new CheckPRNStatusRequestDTO();
	    requestDTO.setPrn(prn);

	    CheckPRNStatusResponseDTO response = null;
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

	    PrnMainResponseWrapperDTO<?> returnedResponse = null;

	    try {
	        returnedResponse = sendHttpRequest(
	            urlCheckPrnStatus, HttpMethod.POST, headers, requestDTO, PrnMainResponseWrapperDTO.class
	        );

	        if (returnedResponse == null) {
	            LOGGER.warn("The returned response is null, unable to process PRN status.");
	            return null;
	        }

	        if (returnedResponse.getResponse() == null && returnedResponse.getErrors() != null
	            && !returnedResponse.getErrors().isEmpty()) {
	            LOGGER.error("Errors in the returned response: {}", returnedResponse.getErrors().get(0).getMessage());
	            return null;
	        }

	        if (returnedResponse.getResponse() != null && !"".equals(returnedResponse.getResponse())) {
	            response = objectMapper.convertValue(returnedResponse.getResponse(), CheckPRNStatusResponseDTO.class);
	        }
	    } catch (Exception e) {
	        LOGGER.error("Error occurred while checking PRN status: {}", e.getMessage(), e);
	    }

	    return response;
	}
	
	public CheckPRNInTransLogsResponseDTO checkPrnInTransLogs(String prn) {
		CheckPRNInTransLogsRequestDTO requestDTO = new CheckPRNInTransLogsRequestDTO();
	    requestDTO.setPrn(prn);

	    CheckPRNInTransLogsResponseDTO response = null;
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

	    PrnMainResponseWrapperDTO<?> returnedResponse = null;

	    try {
	        returnedResponse = sendHttpRequest(
	            urlCheckTransLog, HttpMethod.POST, headers, requestDTO, PrnMainResponseWrapperDTO.class
	        );

	        if (returnedResponse == null) {
	            LOGGER.warn("The returned response is null, unable to check PRN in transc logs.");
	            return null;
	        }

	        if (returnedResponse.getResponse() == null && returnedResponse.getErrors() != null
	            && !returnedResponse.getErrors().isEmpty()) {
	            LOGGER.error("Errors in the returned response: {}", returnedResponse.getErrors().get(0).getMessage());
	            return null;
	        }

	        if (returnedResponse.getResponse() != null && !"".equals(returnedResponse.getResponse())) {
	            response = objectMapper.convertValue(returnedResponse.getResponse(), CheckPRNInTransLogsResponseDTO.class);
	        }
	    } catch (Exception e) {
	        LOGGER.error("Error occurred while checking PRN status: {}", e.getMessage(), e);
	    }

	    return response;
		
		
	}
	
	public ConsumePRNResponseDTO consumePrn(String prn, String registrationId) {
		ConsumePRNRequestDTO requestDTO = new ConsumePRNRequestDTO();
	    requestDTO.setPrn(prn);
	    requestDTO.setRegId(registrationId);

	    ConsumePRNResponseDTO response = null;
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

	    PrnMainResponseWrapperDTO<?> returnedResponse = null;

	    try {
	        returnedResponse = sendHttpRequest(
	            urlConsumePrn, HttpMethod.POST, headers, requestDTO, PrnMainResponseWrapperDTO.class
	        );

	        if (returnedResponse == null) {
	            LOGGER.warn("The returned response is null, unable to check PRN in transc logs.");
	            return null;
	        }

	        if (returnedResponse.getResponse() == null && returnedResponse.getErrors() != null
	            && !returnedResponse.getErrors().isEmpty()) {
	            LOGGER.error("Errors in the returned response: {}", returnedResponse.getErrors().get(0).getMessage());
	            return null;
	        }

	        if (returnedResponse.getResponse() != null && !"".equals(returnedResponse.getResponse())) {
	            response = objectMapper.convertValue(returnedResponse.getResponse(), ConsumePRNResponseDTO.class);
	        }
	    } catch (Exception e) {
	        LOGGER.error("Error occurred while checking PRN status: {}", e.getMessage(), e);
	    }

	    return response;
		
	}


    private <T> T sendHttpRequest( String url, HttpMethod httpMethod, HttpHeaders headers, Object requestBody, Class<T> responseType) {

        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<T> response = restTemplate.exchange(url, httpMethod, entity, responseType);
        return response.getBody();
    }
	
}
