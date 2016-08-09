package com.example.screenclient;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class Loop2GetFrameThread extends Thread{
	
	private Context context;
	private PollFrameData mPoll;
	private byte[] aFrame = new byte[409600];
	private VideoDecoder mDecoder;
	private String studentId;
	private String roomId;
	private String serverIp;
	
	private boolean isRunning = false;
	private boolean isPause = false;
	private boolean isStop = false;
	
	private static final String RTSP_HEAD = "rtsp://";
	private String rtsp_port;

	public Loop2GetFrameThread(Context context, VideoDecoder decoder, String ip, String id, String cid)
	{
		mDecoder = decoder;
		serverIp = ip;
		studentId = id;
		roomId = cid;
		rtsp_port = Integer.toString((Integer.valueOf(cid) + 8554)); //8554
		this.context = context;
		mPoll = new PollFrameData(decoder);
	}
	
	
	public boolean getThreadState() {
		return isRunning;
	}
	
	public void StopThread() {
		//this.interrupt();
		JniClass.stopRtspClient();
		isStop = true;
		mPoll.StopThread();
	}
	
	public void start() {
		super.start();
		mPoll.start();
	}
	
	public void setPause(boolean pause)
	{
		mPoll.setPause(pause);
	}
	public boolean getPause()
	{
		return mPoll.getPause();
	}
	
	public void run() {
		isRunning = true;
		String rtsp_url = RTSP_HEAD + serverIp + ":" + rtsp_port + "/" + roomId;
		int ret = JniClass.startRtspClient(rtsp_url);
		if (ret < 0)
		{
			Log.v("IN APK", "start rtsp client failed");
//			Message message = new Message();
//			message.what = ret;
//			mHandler.sendMessage(message);
			//return;
		}
		
		isRunning = false;
		
		JniClass.stopRtspClient();
	}
	
	
	Handler mHandler = new Handler(){
		public void handleMessage(Message m){
			int res = m.what;
			if (res < 0) {
				Toast.makeText(context, "join room failed", Toast.LENGTH_SHORT).show();
			}
		}
	};
}
