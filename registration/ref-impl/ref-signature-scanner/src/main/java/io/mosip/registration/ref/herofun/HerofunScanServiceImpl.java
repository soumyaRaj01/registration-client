package io.mosip.registration.ref.herofun;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.mosip.registration.api.docscanner.DeviceType;
import io.mosip.registration.api.docscanner.dto.DocScanDevice;
import io.mosip.registration.api.signaturescanner.SignatureService;

@Component
public class HerofunScanServiceImpl implements SignatureService {

	private static final Logger LOGGER = LoggerFactory.getLogger(HerofunScanServiceImpl.class);

	@Override
	public String getServiceName() {
		return "HeroFun SignPad";
	}

	@Override
	public BufferedImage scan(DocScanDevice docScanDevice, String deviceType) {

		SignaturePad signaturepad = SignaturePad.INSTANCE;
		BufferedImage bufferedImage = null;
		if (signaturepad.HWInit(docScanDevice.getWidth(), docScanDevice.getHeight()) == 1) {
			try {
				bufferedImage = getSignatureImage(signaturepad);
				signaturepad.HWClearSig();
			} catch (InterruptedException e) {
				LOGGER.error("Failed to scan", e);
			} catch (ExecutionException e) {
				LOGGER.error("Failed to scan", e);
			}catch (Exception e) {
				LOGGER.error("Failed to scan", e);
			}
			
		}
		return bufferedImage;
	}

	@Override
	public List<DocScanDevice> getConnectedDevices() {
		SignaturePad signaturepad = SignaturePad.INSTANCE;
		LOGGER.info("JNA loader path--------------" + System.getProperty("jna.library.path"));
		List<DocScanDevice> devices = new ArrayList<>();
		if(signaturepad.HWGetDeviceStatus()==1) {
			DocScanDevice docScanDevice = new DocScanDevice();
			docScanDevice.setHeight(signaturepad.HWGetDeviceH());
			docScanDevice.setWidth(signaturepad.HWGetDeviceW());
			docScanDevice.setDeviceType(DeviceType.SIGNATURE_PAD);
			docScanDevice.setServiceName(getServiceName());
			docScanDevice.setId("HeroFunDevice");
			docScanDevice.setName("HeroFunDevice");
			devices.add(docScanDevice);
		}

		return devices;
	}

	@Override
	public void stop(DocScanDevice docScanDevice) {
		SignaturePad signaturepad = SignaturePad.INSTANCE;
		signaturepad.HWClose();

	}

	public BufferedImage byteArrayToBufferedImage(byte[] byteArray) {
		BufferedImage bufferedImage = null;
		try {
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
			bufferedImage = ImageIO.read(byteArrayInputStream);
		} catch (IOException e) {
			LOGGER.error("Failed to open serial port", e);
		}
		return bufferedImage;
	}

	public BufferedImage getSignatureImage(SignaturePad signaturepad) throws InterruptedException, ExecutionException {
		// Create an ExecutorService to handle the thread and return the result
		ExecutorService executorService = Executors.newSingleThreadExecutor();

		// Define the task to be executed in the thread
		Callable<BufferedImage> task = () -> {
			try {
				int statusCode = 0;
				while (statusCode != 1) { // 0 indicates the user has signed (success)
					// Check the status of the signature pad
					statusCode = signaturepad.HWIsOK();
					if (statusCode == 0) {
						LOGGER.info("Signature pad not ready. Waiting...");
					} else if (statusCode == 1) {
						LOGGER.info("User has signed.");
					} else {
						LOGGER.info("Unknown status code: " + statusCode);
					}
					Thread.sleep(1000); // Wait for 1 second before checking again
				}

				byte[] outPng = new byte[1024]; // Assume max size of 1024 bytes
				int[] outPngLength = new int[1]; // This will hold the length of the data
				int result = signaturepad.HWGetPng(outPng, outPngLength);
				while (result == -3) { // If result is -2, buffer was too small
					// Increase the buffer size by doubling it
					LOGGER.info("Buffer too small. Increasing size...");
					outPng = new byte[outPng.length * 2]; // Double the buffer size
					result = signaturepad.HWGetPng(outPng, outPngLength); // Call again with larger buffer
				}

				// Convert byte array to BufferedImage
				return byteArrayToBufferedImage(outPng);

			} catch (InterruptedException e) {
				LOGGER.error("Failed to open serial port", e);
				throw new RuntimeException(e);
			}
		};

		// Submit the task to the executor service
		Future<BufferedImage> future = executorService.submit(task);

		// Wait for the task to complete and get the result
		BufferedImage bufferedImage = future.get();

		// Shutdown the executor service
		executorService.shutdown();

		return bufferedImage;
	}
}
