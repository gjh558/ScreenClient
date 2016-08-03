package com.example.screenclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class ClientThread extends Thread{
	
	private Socket mSocket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private boolean isStop = false;
	private boolean isRunning = false;
	private boolean isPause = false;
	
	private String serverIp = "192.168.1.107";
	private int port = 12345;
	private String studentId;
	private String roomId;
	
	private Context context;
	private byte[] header = new byte[4];
	
	private VideoDecoder mDecoder;
	
	public ClientThread(Context context, VideoDecoder decoder, String ip, String id, String cid) {
		mDecoder = decoder;
		serverIp = ip;
		studentId = id;
		roomId = cid;
		this.context = context;
	}
	
	private boolean init() {
		boolean res = false;
		try {
		mSocket = new Socket(serverIp, port);
		
		inputStream = mSocket.getInputStream();
		outputStream = mSocket.getOutputStream();
		res = true;
		} catch(Exception e) {
			e.printStackTrace();
			res = false;
		}
		
		return res;
	}
	
	private void uninit() {
		
		try {
			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}
			if (mSocket != null) {
				mSocket.close();
				mSocket = null;
			}
			isRunning = false;
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	private int byteArrayToUint32(byte[] src, int offset) throws IllegalArgumentException {
	      int mask = 0xff;
	      int temp = 0;
	      int dest = 0;

	      if (src.length < 4 + offset) {
	         throw new IllegalArgumentException("Failed to translate " + src.length + " bytes to uint32 with " + offset + " offset");
	      }

	      for (int i = 0; i < 4; i++) {
	         dest <<= 8;
	         temp = src[offset + 3-i]&mask;
	         dest |= temp;
	      }

	      return dest;
	   }
	
	public void setPause(boolean pause)
	{
		isPause = pause;
	}
	public boolean getPause()
	{
		return isPause;
	}
	
	public void StopThread() {
		//this.interrupt();
		isStop = true;
	}
	
	public boolean getThreadState() {
		return isRunning;
	}
	public void run(){
		isRunning = true;
		init();
		
		 int type_side = 2;
	        int id = Integer.valueOf(studentId);
	        int cid = Integer.valueOf(roomId);


	        //send id ... to tcp server
	        byte[] type_side_bytes = Utility.uint32ToByteArray(type_side);
	        byte[] id_bytes = Utility.uint32ToByteArray(id);
	        byte[] cid_bytes = Utility.uint32ToByteArray(cid);

	        byte[] settings = new byte[12];
	        System.arraycopy(type_side_bytes, 0, settings, 0, 4);
	        System.arraycopy(id_bytes, 0, settings, 4, 4);
	        System.arraycopy(cid_bytes, 0, settings, 8, 4);
	        
	        try {
				outputStream.write(settings);
				inputStream.read(settings);
				int ret = Utility.byteArrayToUint32(settings, 0);
				Message message = new Message();
				 message.what = ret;
	             mHandler.sendMessage(message);
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		while (!isStop) {
			if (inputStream != null) {
				try {
					int readed = inputStream.read(header);
					
					if (readed != 4) {
						Log.v("client thread", "cannot read header info from server");
						return;
					}
					
					int total_size = byteArrayToUint32(header, 0);
					Log.v("client thread", "total  size is " + total_size);
					byte[] data = new byte[total_size];
					
					int remain_size = total_size;
					int pos = 0;
					while (remain_size > 0) {
						readed = inputStream.read(data, pos, remain_size);
						
						remain_size -= readed;
						pos += readed;
					}
					if (!isPause){
						mDecoder.decodeAndShow(data, 0, total_size);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
			}
		}
		
		uninit();
	}

	 Handler mHandler = new Handler(){
	        @Override
	        public void handleMessage(Message msg) {
	            if (msg.what == 0) {
	                Toast.makeText(context, "create succeed", Toast.LENGTH_SHORT).show();
	            }else {
	                Toast.makeText(context, "create failed", Toast.LENGTH_SHORT).show();
	            }
	        }
	    };
}
