package io.mosip.registration.api.signaturescanner;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.api.docscanner.dto.DocScanDevice;
import lombok.NonNull;



@Component
public class SignatureFacade {
	
	   private static final Logger LOGGER = LoggerFactory.getLogger(SignatureFacade.class);

	    
	    @Autowired
	    private List<SignatureService> signatureServiceList;
	    
	    /**
	     * Provides all the devices including both SCANNER and CAMERA devices
	     *
	     * @return
	     */
	    public List<DocScanDevice> getConnectedDevices() {
	        List<DocScanDevice> allDevices = new ArrayList<>();
	        if (signatureServiceList == null || signatureServiceList.isEmpty()) {
	            LOGGER.warn("** NO SIGNATURE SCANNER SERVICE IMPLEMENTATIONS FOUND!! **");
	            return allDevices;
	        }

	        for (SignatureService service : signatureServiceList) {
	            try {
	                Objects.requireNonNull(service.getConnectedDevices()).forEach(device -> {
						allDevices.add(device);
	                });
	            } catch (Throwable t) {
	                LOGGER.error("Failed to get connected device list from service " + service.getServiceName(), t);
	            }
	        }

	        return allDevices;
	    }

	    public BufferedImage scanDocument(@NonNull DocScanDevice docScanDevice, String deviceType) throws Exception {

	        LOGGER.debug("Selected device details with configuration fully set : {}", docScanDevice);
	        Optional<SignatureService> result = signatureServiceList.stream()
					.filter(s -> s.getServiceName().equals(docScanDevice.getServiceName())).findFirst();

	        if (result.isPresent()) {
				return result.get().scan(docScanDevice, deviceType);
	        }
	        return null;
	    }


	    public void stopDevice(@NonNull DocScanDevice docScanDevice) {
	        try {
	            if (signatureServiceList == null || signatureServiceList.isEmpty())
	                return;

				Optional<SignatureService> result = signatureServiceList.stream()
	                    .filter(s -> s.getServiceName().equals(docScanDevice.getServiceName())).findFirst();

	            if (result.isPresent())
	                result.get().stop(docScanDevice);
	        } catch (Exception e) {
	            LOGGER.error("Error while stopping device {}", docScanDevice.getModel(), e);
	        }
	    }
}
