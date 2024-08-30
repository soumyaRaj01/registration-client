package io.mosip.registration.api.docscanner;

import java.awt.image.BufferedImage;
import java.util.List;

import io.mosip.registration.api.docscanner.dto.DocScanDevice;

public interface DocScannerService {

    String getServiceName();

    BufferedImage scan(DocScanDevice docScanDevice, String deviceType);

    List<DocScanDevice> getConnectedDevices();

    void stop(DocScanDevice docScanDevice);
}
