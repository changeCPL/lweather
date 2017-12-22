package com.coolweather.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.coolweather.app.receiver.AutoUpdateReceiver;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				updateWeather();
			}
		}).start();
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int anHour = 8 * 60 * 60 * 1000; // 这是8小时的毫秒数
		long triggerAtTime = SystemClock.elapsedRealtime() + anHour;//SystemClock.elapsedRealtime()获取系统开机至今的毫秒数
		Intent i = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);//
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * 更新天气信息。
	 */
	private void updateWeather() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String city = prefs.getString("city", "");
		String address = "http://wthrcdn.etouch.cn/weather_mini?city=" + city;
        //System.out.println("autoupdate address: "+address);
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				Log.d("TAG", response);
				//System.out.println("autoupdate handleWeatherResponse");
				Utility.handleWeatherResponse(AutoUpdateService.this, response);
			}

			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}
		});
	}

}
