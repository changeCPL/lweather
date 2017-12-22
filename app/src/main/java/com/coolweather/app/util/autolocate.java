package com.coolweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.List;

/**
 * Created by cpl on 12/21/17.
 */

public class autolocate {
    private Context context;

    boolean first = true;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    public autolocate(Context context) {
        this.context = context;
        mLocationClient = new LocationClient(this.context.getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        option.setIsNeedAddress(true);
        option.setPriority(LocationClientOption.NetWorkFirst);
        option.setOpenGps(true);
        option.setScanSpan(0);

        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        mLocationClient.setLocOption(option);


    }
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            if (location == null) {
                System.out.println("定位失败！");
                return;
            }

            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息



            if(city!=null) {
                Toast.makeText(context,addr, Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putString("cur_district", district);
                editor.commit();
            }
            else{
                String error_info;
                switch (location.getLocType()){
                    case 62:error_info="请检查网络是否开启";
                        break;
                    case 63:error_info="网络异常";break;
                    case 167:error_info="请检查位置权限";break;
                    default:error_info="定位失败";
                }
                System.out.println(error_info);
                Toast.makeText(context,error_info, Toast.LENGTH_SHORT).show();
            }

            mLocationClient.stop();
            System.out.println("LOC.TYPE:"+location.getLocType());
            System.out.println("cur_city:"+city+" cur_district:"+district);
        }

    }


    public boolean getLocation() {
        // 获取位置管理服务
        LocationManager locationManager;
        locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);

        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;

        if (!(providers.contains(LocationManager.GPS_PROVIDER))) {
            Toast.makeText(context, "请检查GPS", Toast.LENGTH_SHORT).show();
            System.out.println("请检查GPS!");
            return false;
        }

        if (providers.size() <= 0) {
            System.out.println("无可用provider!");
            return false;
        }

        System.out.println("providers.size:" + providers.size());


        for (String provider : providers)
            System.out.println(provider);

        int count = 0;

        for (String provider : providers) {
            System.out.println("cur provider:" + provider);

            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                if (count < providers.size() - 1) {
                    count++;
                    continue;
                } else {
                    Toast.makeText(context, "无法定位，请检查网络或GPS", Toast.LENGTH_SHORT).show();
                    System.out.println("无法定位，请检查网络或GPS");
                    return false;
                }
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }

            System.out.println("bestProvider: " + provider);
        }


        String curlocation = bestLocation.getLatitude() + "," + bestLocation.getLongitude();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("pos", curlocation);
        editor.commit();

        System.out.println("getlocation:" + bestLocation.getLatitude() + "," + bestLocation.getLongitude());
        return true;
    }


}
