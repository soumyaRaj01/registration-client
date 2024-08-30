package io.mosip.registration.api.docscanner.dto;

import io.mosip.registration.api.docscanner.DeviceType;
import lombok.Data;

@Data
public class DocScanDevice {

    private String serviceName;
    private String id;
    private String name;
    private DeviceType deviceType;
    private String firmware;
    private String serial;
    private String model;

    private int dpi;
    //accepts 4 elements, x, y, width, height (in pixels)
    private int[] frame;
    private int height;
    private int width;
    private String mode;
}
