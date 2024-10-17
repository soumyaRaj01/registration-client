package io.mosip.registration.ref.herofun;
import com.sun.jna.Library;
import com.sun.jna.Native;

public interface SignaturePad extends Library {

	SignaturePad INSTANCE = Native.load("HWTablet", SignaturePad.class);
	
	 // Method to get device status
    public  int HWGetDeviceStatus();

    public  int HWGetDeviceW();
    
    public  int HWGetDeviceH();

    // Method to initialize with image width and height
    public  int HWInit(int imgWidth, int imgHeight);
    // Method to close hardware
    public  int HWClose();

    // Method to clear signal
    public  int HWClearSig();
    
    public  int HWGetJpg(byte[] pOutPng, int[] nOutPng);
    
    public  int HWSavePng(String pngPath);

    public  int HWGetPng(byte[] pOutPng, int[] nOutPng);
    
    public  int HWSaveJpg(String pngPath);

    public  int HWIsOK();


}
