package com.example.screenclient;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.R.integer;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

public class VideoDecoder {
	 
	MediaCodec mDecoder;
	int mCount = 0;
	 
	FileOutputStream fileOutputStream = null;
	
	public void initDecoder(int width, int height, Surface surface) {
		
		mDecoder = MediaCodec.createDecoderByType("video/avc");
		
		MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", width, height); 
		mDecoder.configure(mediaFormat, surface, null, 0);
		
		mDecoder.start();
		
		try {  
			fileOutputStream = new FileOutputStream("/sdcard/test.h264");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 

	public void decodeAndShow(byte[] data, int offset, int size){
		if (mDecoder == null) {
			return;
		}
		
//		try {
//			fileOutputStream.write(data, offset, size);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		ByteBuffer[] inputBuffers = mDecoder.getInputBuffers();
		int inputBufferIndex = mDecoder.dequeueInputBuffer(-1);
		
		if (inputBufferIndex >= 0) {
			ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
			inputBuffer.clear();
			inputBuffer.put(data, offset, size);
			mDecoder.queueInputBuffer(inputBufferIndex, 0, size, mCount * 1000000 / 25, 0);
			mCount++;
			
		}
		
		MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
		int outputBufferIndex = mDecoder.dequeueOutputBuffer(bufferInfo, 0);
		
		while(outputBufferIndex >= 0) { 
			mDecoder.releaseOutputBuffer(outputBufferIndex, true);
			outputBufferIndex = mDecoder.dequeueOutputBuffer(bufferInfo, 0);
		}
	}
	
	public void uinitDecoder() {
		try {
			fileOutputStream.close();
			mDecoder.stop();
			mDecoder.release();
			//mDecoder = null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
