package com.example.screenclient;

import java.util.Calendar;

import android.R.interpolator;
import android.R.mipmap;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements SurfaceHolder.Callback{
	
	private SurfaceView surfaceView;
	private VideoDecoder mDecoder;
	//private ClientThread mClientThread;
	private Loop2GetFrameThread mLoop2GetFrameThread;
	private int width = 720;
	private int height = 1080;
	
	private String mIp = "127.0.0.1";
	
	private String studentId;
	private String roomId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		surfaceView = (SurfaceView)findViewById(R.id.surfaceView1);
		
		surfaceView.getHolder().addCallback(this);
		
//		if (isExpired()) {
//        	
//        	MainActivity.this.finish();
//        	startActivity(new Intent(MainActivity.this, LicenseActivity.class));
//        }

		checkPreviousSettings();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.start) {
			 if (studentId == null || roomId == null || studentId.length() <= 0 || roomId.length() <= 0) {
             	Toast.makeText(MainActivity.this, "Please input valid IDs", Toast.LENGTH_SHORT).show();
             	return false;
             }
			 
			if (mDecoder == null && mLoop2GetFrameThread == null) {
				mDecoder = new VideoDecoder();
				mLoop2GetFrameThread = new Loop2GetFrameThread(MainActivity.this, mDecoder, mIp, studentId, roomId);
				if (!mLoop2GetFrameThread.getThreadState()) {
					
					mDecoder.initDecoder(width, height, surfaceView.getHolder().getSurface());
					mLoop2GetFrameThread.start();
				}
			} else {
				if (mLoop2GetFrameThread.getPause()) {
					mLoop2GetFrameThread.setPause(false);
				}else {
					Toast.makeText(MainActivity.this, "Already running...", Toast.LENGTH_SHORT).show();
				}
			}
			return true;
		}else if (id == R.id.pause) {
			if (mLoop2GetFrameThread != null && mDecoder != null) {
				mLoop2GetFrameThread.setPause(true);
//				if (mClientThread.getThreadState()) {
//					
//					
////					try {
////						Thread.sleep(1000);
////					} catch (InterruptedException e) {
////						// TODO Auto-generated catch block
////						e.printStackTrace();
////					}
////					
//					mClientThread.StopThread();
//					//mClientThread = null;
//					
//					while (mClientThread.getThreadState()) {
//						try {
//							Thread.sleep(100);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//					mClientThread = null;
//					mDecoder.uinitDecoder();
//					mDecoder = null;
//					
//				}
			}
			return true;
		} else if (id == R.id.settings) {
			showSettingDialog();
			return true;
		} else if (id == R.id.exit) {
			mLoop2GetFrameThread.StopThread();
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (mLoop2GetFrameThread != null && mDecoder != null) {
			if (mLoop2GetFrameThread.getThreadState()) {
				mLoop2GetFrameThread.StopThread();
				//mClientThread = null;
				
				while (mLoop2GetFrameThread.getThreadState()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				mLoop2GetFrameThread = null;
				mDecoder.uinitDecoder();
				mDecoder = null;
				
			}
		}
	}
	
	private boolean checkPreviousSettings() {
		SharedPreferences settings = getSharedPreferences("SCREENCLIENT", 0);
		String ip_prev = settings.getString("IP", null);
		this.width = settings.getInt("WIDTH", 360);
		this.height = settings.getInt("HEIGHT", 540);
		this.studentId = settings.getString("ID", null);
		this.roomId = settings.getString("RID", null);
		
		if (ip_prev != null) {
			this.mIp = ip_prev;
			Toast.makeText(MainActivity.this, "The server address is " + mIp, Toast.LENGTH_SHORT).show();
			return true;
		} else {
			Toast.makeText(MainActivity.this, "Please set the server address", Toast.LENGTH_SHORT).show();
			return false;
		}
	}
	
	private void showSettingDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Settings");
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);
        
        final EditText editTextIp = (EditText)view.findViewById(R.id.editText_ip);
        final EditText editTextWidth = (EditText)view.findViewById(R.id.editText_width);
        final EditText editTextHeight = (EditText)view.findViewById(R.id.editText_height);
        final EditText editTextId = (EditText)view.findViewById(R.id.editText_id);
        final EditText editTextRoomId = (EditText)view.findViewById(R.id.editText_room);
        
        SharedPreferences settings = getSharedPreferences("SCREENCLIENT", 0);
        final SharedPreferences.Editor editor = settings.edit();
        
        String set_ip = settings.getString("IP", "");
        int aWidth = settings.getInt("WIDTH", 360);
        int aHeight = settings.getInt("HEIGHT", 640);
        String set_id = settings.getString("ID", "");
        String set_roomid = settings.getString("RID", "");
        
        if (mIp != null)
        	editTextIp.setText(set_ip);
        editTextHeight.setText(Integer.toString(aHeight));
        editTextWidth.setText(Integer.toString(aWidth));
        editTextId.setText(set_id);
        editTextRoomId.setText(set_roomid);
        
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String a = editTextIp.getText().toString().trim();
                String aWidth = editTextWidth.getText().toString().trim();
                String aHeight = editTextHeight.getText().toString().trim();
                studentId = editTextId.getText().toString().trim();
                roomId = editTextRoomId.getText().toString().trim();
                
                if (studentId == null || roomId == null || studentId.length() <= 0 || roomId.length() <= 0) {
                	Toast.makeText(MainActivity.this, "Please input valid IDs", Toast.LENGTH_SHORT).show();
                	return;
                }
                
                mIp = a;
                width = Integer.valueOf(aWidth);
                height = Integer.valueOf(aHeight);
               
                editor.putString("IP", mIp);
                editor.putInt("WIDTH", width);
                editor.putInt("HEIGHT", height);
                editor.putString("ID", studentId);
                editor.putString("RID", roomId);
                editor.commit();
                //    将输入的用户名和密码打印出来
                Toast.makeText(MainActivity.this, "Server address: " + mIp + ", Resolution: " + aWidth + "x" + aHeight, Toast.LENGTH_SHORT).show();
            } 
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                
            }
        });
        builder.show();
	}
	
	private boolean isExpired() {
    	SharedPreferences settings = getSharedPreferences("SCREENCLIENT", 0);
    	
    	int y = settings.getInt("YEAR", 0);
    	int m = settings.getInt("MONTH", 0);
    	int d = settings.getInt("DAY", 0);
    	String license = settings.getString("LICENSE", "invalid");
    	
    	Calendar c = Calendar.getInstance();
		int cur_y = c.get(Calendar.YEAR);
		int cur_m = c.get(Calendar.MONTH);
		int cur_d = c.get(Calendar.DAY_OF_MONTH);
    	
    	if (y == 0 || m == 0 || d == 0) {
    		//the first time install the software, write date to data base
    		SharedPreferences.Editor editor = settings.edit();
    		
    		editor.putInt("YEAR", cur_y);
    		editor.putInt("MONTH", cur_m);
    		editor.putInt("DAY", cur_d);
    		editor.putString("LICENSE", "invalid");
    		
    		editor.commit();
    		
    		return false;
    	} else {
    		if (license.equalsIgnoreCase("1990825")) {
    			return false;
    		} else if (cur_y > y) {
    			return true;
    		} else if (cur_y == y && cur_m > m) {
    			return true;
    		} else if (cur_m == m && cur_d > d) {
    			return true;
    		} else {
    			return false;
    		}
    	}
    }
}
