package com.coolweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utility {

	/**
	 * 解析和处理服务器返回的省级数据
	 */
	public synchronized static boolean handleProvincesResponse(
			CoolWeatherDB coolWeatherDB, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0) {
				for (String p : allProvinces) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					// 将解析出来的数据存储到Province表
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析和处理服务器返回的市级数据
	 */
	public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,
			String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (String c : allCities) {
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					// 将解析出来的数据存储到City表
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析和处理服务器返回的县级数据
	 */
	public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,
			String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0) {
				for (String c : allCounties) {
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					// 将解析出来的数据存储到County表
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析服务器返回的JSON数据，并将解析出的数据存储到本地。
	 */
	public static void handleWeatherResponse(Context context, String response) {
		//System.out.println("in handleWeatherResponse: "+response);
		try {
			info last5 = new info();
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("data");
			last5.setCity(weatherInfo.getString("city"));
			last5.setCurtemp(weatherInfo.getString("wendu")+"℃");
			last5.setGanmao(weatherInfo.getString("ganmao"));
			JSONArray last5info = weatherInfo.getJSONArray("forecast");
			for(int i=0;i<last5info.length();i++){
				JSONObject day = (JSONObject) last5info.get(i);
				last5.last5days.add(new dayinfo(
						day.getString("date"),
						day.getString("high"),
						day.getString("low"),
						day.getString("type"),
						day.getString("fengxiang"),
						day.getString("fengli")
				));
			}
			saveWeatherInfo(context, last5);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将服务器返回的所有天气信息存储到SharedPreferences文件中。
	 */
	public static void saveWeatherInfo(Context context, info last5) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);  //标志位，辨别当前是否已经选中一个城市

		editor.putString("city",last5.getCity());
		editor.putString("wendu",last5.getCurtemp());
		editor.putString("ganmao",last5.getGanmao());
		for(int i=0;i<last5.last5days.size();i++){
			String date="date"+i;
			String high="high"+i;
			String low="low"+i;
			String type="type"+i;
			String fengxiang="dire"+i;
			String stre="stre"+i;
			editor.putString(date,last5.last5days.get(i).getDate());
			editor.putString(high,last5.last5days.get(i).getHigh().split(" ")[1]);
			editor.putString(low,last5.last5days.get(i).getLow().split(" ")[1]);
			editor.putString(type,last5.last5days.get(i).getType());
			editor.putString(fengxiang,last5.last5days.get(i).getWinddirection());
			editor.putString(stre,last5.last5days.get(i).getWindstrength());
		}

		//System.out.println("saveinfo:"+last5.toString());
//		editor.putString("city_name", cityName);
//		editor.putString("weather_code", weatherCode);
//		editor.putString("temp1", temp1);
//		editor.putString("temp2", temp2);
//		editor.putString("weather_desp", weatherDesp);
//		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}

}