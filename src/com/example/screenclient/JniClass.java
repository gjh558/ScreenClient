package com.example.screenclient;

public class JniClass {
	
	public static native int startRtspClient(String url);
	public static native int getAFrame(byte[] buffer);
	public static native void stopRtspClient();
	
	
	static {
		System.loadLibrary("live555");
	}

}
