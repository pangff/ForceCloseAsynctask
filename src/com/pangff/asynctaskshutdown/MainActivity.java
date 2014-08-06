package com.pangff.asynctaskshutdown;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	Button stop ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final MyAsynctask myAsynctask = new MyAsynctask();
		stop = (Button) findViewById(R.id.stop);
		stop.setText("启动");
		stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(stop.getText().equals("启动")){
					stop.setText("停止");
					myAsynctask.execute("");
				}else{
					Log.e("dddd", "调用停止");
					//myAsynctask.getLooper().quit();
					myAsynctask.forceStop();
				}
			}
		});
	}
	
	
	public class MyAsynctask extends AsyncTask<String, String, String>{
		Handler handler;
		Thread asynThread;
		public  final static int QUIT_FLAG = -1;
		public  final static int FINISH_FLAG = 1;
		
		/**
		 * 强制关闭，返回只是返回调用是否成功，不代表关闭成功
		 * @return
		 */
		public boolean forceStop(){
			if(handler!=null){
				handler.sendEmptyMessage(QUIT_FLAG);
				return true;
			}else{
				return false;
			}
		}
		@Override
		protected String doInBackground(String... params) {
			Looper.prepare();
			handler = new Handler(){
				@Override
				public void dispatchMessage(Message msg) {
					super.dispatchMessage(msg);
					if(msg.what==QUIT_FLAG){
						Log.e("ddd", "接受到关闭消息");
						Looper.myLooper().quit();
					}
					if(msg.what==FINISH_FLAG){
						Log.e("ddd", "接受到完成消息");
						Looper.myLooper().quit();
					}
				}
			};
			/**
			 * 耗时操作要令起线程
			 */
			if(asynThread==null){
				asynThread = new Thread(){
					@Override
					public void run() {
						super.run();
						Log.e("ddd", "后台耗时数据执行");
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(handler!=null){
							handler.sendEmptyMessage(FINISH_FLAG);
						}
					}
				};
				asynThread.start();
			}
			Looper.loop();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.e("ddd", "异步线程完成");
			stop.setText("启动");
		}

		@Override
		protected void onPreExecute() {
			Log.e("ddd", "异步线程开始");
			super.onPreExecute();
		}
		
	}
}
