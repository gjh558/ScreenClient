package com.example.screenclient;

import android.provider.MediaStore.Video;
import android.util.Log;

public class PollFrameData extends Thread {
	
	private byte[] aFrame = new byte[409600];
	private VideoDecoder mDecoder;

	private boolean isRunning = false;
	private boolean isPause = false;
	private boolean isStop = false;
	
	
	public PollFrameData(VideoDecoder decoder) {
		this.mDecoder = decoder;
	}
	
	public boolean getThreadState() {
		return isRunning;
	}
	
	public void StopThread() {
		//this.interrupt();
		isStop = true;
	}
	
	public void setPause(boolean pause)
	{
		isPause = pause;
	}
	public boolean getPause()
	{
		return isPause;
	}
	
	public void run() {
		while (!isStop) {
			
			int size = JniClass.getAFrame(aFrame);
			if (size > 0) {
				if (!isPause) {
					mDecoder.decodeAndShow(aFrame, 0, size);
				}
			}
		}
		
		Log.v("PollFrameData", "pooling exit...");
	}

}
