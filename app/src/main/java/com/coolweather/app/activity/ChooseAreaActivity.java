package com.coolweather.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;
import com.coolweather.app.util.fileop;
import com.coolweather.app.util.sendrequest;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	public static final int LEVEL_SEARCH=3;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private EditText search;
	private TextView confirm;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	/**
	 * 省列表
	 */
	private List<Province> provinceList;
	/**
	 * 市列表
	 */
	private List<City> cityList;
	/**
	 * 县列表
	 */
	private List<County> countyList,searchcountrylist;
	/**
	 * 选中的省份
	 */
	private Province selectedProvince;
	/**
	 * 选中的城市
	 */
	private City selectedCity;
	/**
	 * 当前选中的级别
	 */
	private int currentLevel;
	/**
	 * 是否从WeatherActivity中跳转过来。
	 */
	private boolean isFromWeatherActivity;

	private myapp data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//
//		AdManager.getInstance(this).init("cf9c2a749cd97145","289874826c698edd", false);
		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("city_selected", false) && !isFromWeatherActivity) {
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		search=(EditText) findViewById(R.id.searchbox);
		confirm=(TextView) findViewById(R.id.confirm);
		//confirm.setVisibility(View.VISIBLE);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);

		data=(myapp) getApplication();

		//System.out.println("in choose :citymap.size-"+data.citymap.size()+" ,countymap.size-"+data.countymap.size());
//		try {
//			initdb();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				final String content = search.getText().toString();
				if(!content.isEmpty()) {
					confirm.setClickable(true);
					confirm.setEnabled(true);
					confirm.setTextColor(Color.BLUE);
					confirm.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							searchedittext(content);
						}
					});
				} else {
					confirm.setClickable(false);
					confirm.setEnabled(false);
					confirm.setTextColor(Color.GRAY);
				}



			}
		});



		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(index);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					queryCounties();
				} else if (currentLevel == LEVEL_COUNTY || currentLevel==LEVEL_SEARCH) {
					String countyCode = (currentLevel == LEVEL_COUNTY)?(countyList.get(index).getCountyCode()):(searchcountrylist.get(index).getCountyCode());
					Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
			}
		});
		queryProvinces();  // 加载省级数据
	}

	private void searchedittext(String condition)
	{
		if(data.citymap.get(condition)==null&&data.countymap.get(condition)==null&&
				data.rpcountymap.get(condition)==null){
			Toast.makeText(ChooseAreaActivity.this,"无匹配结果 ",Toast.LENGTH_SHORT).show();
		}
		else{
			Toast.makeText(ChooseAreaActivity.this,"正在查询 ",Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
			intent.putExtra("cityname", condition);
			startActivity(intent);
			finish();
		}

		//showProgressDialog();
//		String sql;
//		if(condition.length()>0||!"".equals(condition))
//		{
//			sql = "select * from County where county_name like '%"+condition+"%'";
//			Cursor cursor=coolWeatherDB.db.rawQuery(sql,null);
//			if (cursor.moveToFirst()) {
//				do {
//					County county = new County();
//					county.setId(cursor.getInt(cursor.getColumnIndex("id")));
//					county.setCountyName(cursor.getString(cursor
//							.getColumnIndex("county_name")));
//					county.setCountyCode(cursor.getString(cursor
//							.getColumnIndex("county_code")));
//					county.setCityId(cursor.getInt(cursor
//							.getColumnIndex("city_id")));
//					searchcountrylist.add(county);
//				} while (cursor.moveToNext());
//			}
//			if(searchcountrylist.size()>0){
//				dataList.clear();
//				for (County county : searchcountrylist) {
//					dataList.add(county.getCountyName());
//				}
//				adapter.notifyDataSetChanged();
//				listView.setSelection(0);
//				titleText.setText("搜索结果");
//				currentLevel = LEVEL_SEARCH;
//			}
// }
		//closeProgressDialog();

	}


	private  void initdb() throws InterruptedException {
		String address = "http://www.weather.com.cn/data/list3/city.xml";
		showProgressDialog();
		sendrequest pro = new sendrequest(address);
		pro.start();

		pro.join();

		Utility.handleProvincesResponse(coolWeatherDB,
				pro.getrlt());
		provinceList = coolWeatherDB.loadProvinces();
		//System.out.println("initdb provinceList.size(): "+provinceList.size());

		fileop provincefile=new fileop("province.txt");
		fileop cityfile=new fileop("city.txt");
		fileop countyfile=new fileop("county.txt");





		sendrequest cityreq[]=new sendrequest[provinceList.size()];
		int i=0;
		for (Province province : provinceList) {

			address="http://www.weather.com.cn/data/list3/city"+province.getProvinceCode()+".xml";
			cityreq[i] = new sendrequest(address);
			cityreq[i].start();
			cityreq[i].join();
			Utility.handleCitiesResponse(coolWeatherDB, cityreq[i].getrlt(), province.getId());
			//System.out.println("当前查询省："+province.getProvinceName());
			String str=province.getProvinceName()+" "+province.getProvinceCode()+"\n";
			//provincefile.write(str,ChooseAreaActivity.this);
			//System.out.println(str);
			//System.out.println(province.getProvinceName()+" 所辖城市数："+coolWeatherDB.loadCities(province.getId()).size());
			i++;
		}

		for (Province province : provinceList) {
			List<City> citys = coolWeatherDB.loadCities(province.getId());
			for(City city:citys){
				String str=city.getCityName()+" "+city.getCityCode()+"\n";
				address="http://www.weather.com.cn/data/list3/city"+city.getCityCode()+".xml";
				sendrequest countryreq = new sendrequest(address);
				countryreq.start();
				countryreq.join();
				Utility.handleCountiesResponse(coolWeatherDB, countryreq.getrlt(), city.getId());
			}
		}

		String sql0 = "select count(*) from Province";
		String sql1 = "select count(*) from City";
		String sql2 = "select count(*) from County";
		String sql3 = "select county_name,county_code from County";




		int countynum=0,provincenum=0,citynum=0;
		Cursor cursor=coolWeatherDB.db.rawQuery(sql0,null);
		if (cursor.moveToFirst()) {provincenum=cursor.getInt(0);}
		cursor=coolWeatherDB.db.rawQuery(sql1,null);
		if (cursor.moveToFirst()) {citynum=cursor.getInt(0);}
		cursor=coolWeatherDB.db.rawQuery(sql2,null);
		if (cursor.moveToFirst()) {countynum=cursor.getInt(0);}

		cursor=coolWeatherDB.db.rawQuery(sql3,null);
		if (cursor.moveToFirst()) {
			do {
				String str=cursor.getString(cursor.getColumnIndex("county_name"))+" "+
						cursor.getString(cursor.getColumnIndex("county_code"))+"\n";
				System.out.println(str);
				//countyfile.write(str,ChooseAreaActivity.this);
			} while (cursor.moveToNext());

		}


		String initinfo="init finished, provinceynum:"+String.valueOf(provincenum)+", citynum:"+String.valueOf(citynum)
				+", countynum:"+String.valueOf(countynum);
		System.out.println(initinfo);
		Toast.makeText(ChooseAreaActivity.this,initinfo,Toast.LENGTH_SHORT).show();
		closeProgressDialog();
	}



	/**
	 * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
	 */
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);//这个方法的作用就是将第position个item显示在listView的最上面一项
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	/**
	 * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
	 */
	private void queryCities() {
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	
	/**
	 * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
	 */
	private void queryCounties() {
		countyList = coolWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	
	/**
	 * 根据传入的代号和类型从服务器上查询省市县数据。
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(coolWeatherDB,
							response);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB,
							response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(coolWeatherDB,
							response, selectedCity.getId());
				}
				if (result) {
					// 设计到ＵＩ更改，通过runOnUiThread()方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							}
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				// 通过runOnUiThread()方法回到主线程处理逻辑
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this,
										"加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 * 关闭进度对话框
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
	/**
	 * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出。
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY||currentLevel==LEVEL_SEARCH) {
			queryProvinces();
		}else {
			if (isFromWeatherActivity) {
				Intent intent = new Intent(this, WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}

}