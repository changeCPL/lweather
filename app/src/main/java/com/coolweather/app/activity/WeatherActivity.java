package com.coolweather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;
import com.coolweather.app.util.autolocate;

public class WeatherActivity extends Activity implements OnClickListener{

//	private LocationManager locationManager;
//	private Location location;
	String provider,curlocation="";


	private LinearLayout weatherInfoLayout,futurelayout; //
	/**
	 * 用于显示城市名
	 */
	private TextView wendu,ganmao,dire0;
	private TextView cityNameText;
	/**
	 * 用于显示发布时间
	 */
	private TextView publishText;
	/**
	 * 用于显示天气描述信息
	 */
	private TextView weatherDespText;
	/**
	 * 用于显示气温1
	 */
	private TextView temp1Text;
	/**
	 * 用于显示气温2
	 */
	private TextView temp2Text;
	/**
	 * 用于显示当前日期
	 */
	private TextView currentDateText;
	/**
	 * 切换城市按钮
	 */

	private TextView date1,date2,date3,date4;
	private TextView low1,low2,low3,low4;
	private TextView high1,high2,high3,high4;
	private TextView type1,type2,type3,type4;
	private TextView dire1,dire2,dire3,dire4;

	private Button switchCity,locate;
	/**
	 * 更新天气按钮
	 */
	private Button refreshWeather;


	private  autolocate forlocate ;



	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);


		// 初始化各控件
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		futurelayout = (LinearLayout) findViewById(R.id.future);

		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		wendu=(TextView) findViewById(R.id.wendu);
		ganmao=(TextView) findViewById(R.id.ganmao);
		dire0=(TextView) findViewById(R.id.wind) ;
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		locate = (Button) findViewById(R.id.locate);

		date1=(TextView) findViewById(R.id.date1);
		date2=(TextView) findViewById(R.id.date2);
		date3=(TextView) findViewById(R.id.date3);
		date4=(TextView) findViewById(R.id.date4);
		low1=(TextView) findViewById(R.id.low1);
		low2=(TextView) findViewById(R.id.low2);
		low3=(TextView) findViewById(R.id.low3);
		low4=(TextView) findViewById(R.id.low4);
		high1=(TextView) findViewById(R.id.high1);
		high2=(TextView) findViewById(R.id.high2);
		high3=(TextView) findViewById(R.id.high3);
		high4=(TextView) findViewById(R.id.high4);
		type1=(TextView) findViewById(R.id.type1);
		type2=(TextView) findViewById(R.id.type2);
		type3=(TextView) findViewById(R.id.type3);
		type4=(TextView) findViewById(R.id.type4);
		dire1=(TextView) findViewById(R.id.dire1);
		dire2=(TextView) findViewById(R.id.dire2);
		dire3=(TextView) findViewById(R.id.dire3);
		dire4=(TextView) findViewById(R.id.dire4);



		String countyCode = getIntent().getStringExtra("county_code");//取出县级号
		String cityname = getIntent().getStringExtra("cityname");
		if (!TextUtils.isEmpty(countyCode)) {
			// 有县级代号时就去查询天气
			System.out.println("from select countyCode:"+countyCode);
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);//查询前,设置控件不可见，但这个View仍然会占用在xml文件中所分配的布局空间，不重新layout 布局
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}else if(!TextUtils.isEmpty(cityname)){
			System.out.println("from search cityname:"+cityname);
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			currentDateText.setVisibility(View.INVISIBLE);
			wendu.setVisibility(View.INVISIBLE);
			ganmao.setVisibility(View.INVISIBLE);
			futurelayout.setVisibility(View.INVISIBLE);
			queryWeatherInfo(cityname,true);
		}
		else {
			// 没有县级代号时就直接显示本地天气
			showWeather();//以后可修改为　获取当前位置的当前天气
		}
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		locate.setOnClickListener(this);

	}




	//实现接口的方式为button点击事件设置了监听器
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		    case R.id.switch_city:
			    Intent intent = new Intent(this, ChooseAreaActivity.class);
			    intent.putExtra("from_weather_activity", true);
			    startActivity(intent);
			    finish();
			    break;

		    case R.id.refresh_weather:
			    publishText.setText("同步中...");
				weatherInfoLayout.setVisibility(View.INVISIBLE);
				cityNameText.setVisibility(View.INVISIBLE);
				currentDateText.setVisibility(View.INVISIBLE);
				wendu.setVisibility(View.INVISIBLE);
				ganmao.setVisibility(View.INVISIBLE);
				futurelayout.setVisibility(View.INVISIBLE);

			    String cityname = (String) cityNameText.getText();
			    if (!TextUtils.isEmpty(cityname)) {
					System.out.println("refresh city:"+cityname);
			    	queryWeatherInfo(cityname,true);
			    }
			    else {
					publishText.setText("同步失败...");
					showWeather();
				}
			    break;
            case R.id.locate:
            	//System.out.println("locating...");
                Toast.makeText(WeatherActivity.this, "定位中... ", Toast.LENGTH_SHORT).show();
				forlocate = new autolocate(this);
				forlocate.mLocationClient.start();

				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
				String district = prefs.getString("cur_district","");
				queryWeatherInfo(district, true);
				break;
            default:
			    break;
		}
	}



	
	/**
	 * 查询县级代号所对应的天气代号。
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}

	/**
	 * 查询天气代号所对应的天气。
	 */
	private void queryWeatherInfo(String weatherCode, boolean refresh) {
		String address ;
		address=(refresh)?("http://wthrcdn.etouch.cn/weather_mini?city="+weatherCode):( "http://wthrcdn.etouch.cn/weather_mini?citykey=" + weatherCode);
		//System.out.println("address:"+address);
		queryFromServer(address, "weatherCode");
	}



	/**
	 * 根据传入的地址和类型去向服务器查询天气代号或者天气信息。
	 */
	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(final String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						// 从服务器返回的数据中解析出天气代号
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode,false);
						}
					}
				} else if ("weatherCode".equals(type)) {
					// 处理服务器返回的天气信息
					//System.out.println("weatherCode Utility.handleWeatherResponse: "+response);
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						publishText.setText("同步失败");
					}
				});
			}
		});//HttpUtil.sendHttpRequest()
	}
	
	/**
	 * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
	 */
	private void showWeather() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		cityNameText.setText( prefs.getString("city", ""));
		temp1Text.setText(prefs.getString("low0", ""));
		temp2Text.setText(prefs.getString("high0", ""));
		weatherDespText.setText(prefs.getString("type0", ""));
		wendu.setText(prefs.getString("wendu", ""));
		ganmao.setText(prefs.getString("ganmao", ""));
		publishText.setText("已更新");
		currentDateText.setText(prefs.getString("date0", ""));

		date1.setText(prefs.getString("date1", ""));
		date2.setText(prefs.getString("date2", ""));
		date3.setText(prefs.getString("date3", ""));
		date4.setText(prefs.getString("date4", ""));
		low1.setText(prefs.getString("low1", ""));
		low2.setText(prefs.getString("low2", ""));
		low3.setText(prefs.getString("low3", ""));
		low4.setText(prefs.getString("low4", ""));
		high1.setText(prefs.getString("high1", ""));
		high2.setText(prefs.getString("high2", ""));
		high3.setText(prefs.getString("high3", ""));
		high4.setText(prefs.getString("high4", ""));
		type1.setText(prefs.getString("type1", ""));
		type2.setText(prefs.getString("type2", ""));
		type3.setText(prefs.getString("type3", ""));
		type4.setText(prefs.getString("type4", ""));
		dire0.setText(prefs.getString("dire0", ""));
		dire1.setText(prefs.getString("dire1", ""));
		dire2.setText(prefs.getString("dire2", ""));
		dire3.setText(prefs.getString("dire3", ""));
		dire4.setText(prefs.getString("dire4", ""));



		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		currentDateText.setVisibility(View.VISIBLE);
		wendu.setVisibility(View.VISIBLE);
		ganmao.setVisibility(View.VISIBLE);
		futurelayout.setVisibility(View.VISIBLE);

		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
	}



}