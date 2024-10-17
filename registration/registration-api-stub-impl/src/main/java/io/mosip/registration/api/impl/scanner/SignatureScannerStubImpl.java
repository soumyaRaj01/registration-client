package io.mosip.registration.api.impl.scanner;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.mosip.registration.api.docscanner.DeviceType;
import io.mosip.registration.api.docscanner.dto.DocScanDevice;
import io.mosip.registration.api.signaturescanner.SignatureService;

@Component
public class SignatureScannerStubImpl implements SignatureService  {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScannerStubImpl.class);
	private static final String SERVICE_NAME = "MOSIP-STUB";
	private static final String DEVICE_NAME = "STUB-SIGNATUREPAD";
	private static final List<String> SIGNATURE_STUB_PATHS = new ArrayList<>();
	private static int index = 0;

	    static {
			SIGNATURE_STUB_PATHS.add("/images/signature.png");

	    }

		@Override
		public String getServiceName() {
			return SERVICE_NAME;
		}


		@Override
		public BufferedImage scan(DocScanDevice docScanDevice, String deviceType) {
			try (InputStream inputStream = this.getClass().getResourceAsStream(getStubPath())) {
				BufferedImage bufferedImage = ImageIO.read(inputStream);
				return bufferedImage;
			} catch (IOException e) {
				LOGGER.error("Failed to stub document", e);
			}
			return null;
		}

		@Override
		public List<DocScanDevice> getConnectedDevices() {
			DocScanDevice docScanDevice = new DocScanDevice();
			docScanDevice.setServiceName(getServiceName());
			docScanDevice.setHeight(120);
			docScanDevice.setWidth(240);
			docScanDevice.setDeviceType(DeviceType.SIGNATURE_PAD);
			docScanDevice.setName(DEVICE_NAME);
			docScanDevice.setId(DEVICE_NAME);
			return Arrays.asList(docScanDevice);
		}

		@Override
		public void stop(DocScanDevice docScanDevice) {
			// Do nothing
		}

		private String getStubPath() {
			index = index % SIGNATURE_STUB_PATHS.size();
			return SIGNATURE_STUB_PATHS.get(index++);
		}

}
