package com.coolweather.app.activity;

import android.app.Application;

import com.coolweather.app.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by cpl on 12/20/17.
 */

public class myapp extends Application {
    //public CoolWeatherDB coolWeatherDB;
    public HashMap provincemap = new HashMap();
    public HashMap citymap = new HashMap();
    public HashMap countymap = new HashMap();
    public HashMap rpcountymap = new HashMap();

    public void savemap(){

        InputStream province = getResources().openRawResource(R.raw.province);
        InputStream city = getResources().openRawResource(R.raw.city);
        InputStream county = getResources().openRawResource(R.raw.county);

        InputStreamReader provinceisr =new InputStreamReader(province);
        InputStreamReader cityisr =new InputStreamReader(city);
        InputStreamReader countyisr =new InputStreamReader(county);

        BufferedReader provincebfr =new BufferedReader(provinceisr);
        BufferedReader citybfr =new BufferedReader(cityisr);
        BufferedReader countybfr =new BufferedReader(countyisr);

        String provincein,cityin,countyin;
        try {
            while ((provincein=provincebfr.readLine())!=null){
                String provinceinfo[] = provincein.split(" ");
                this.provincemap.put(provinceinfo[0],provinceinfo[1]);
            }

            while((cityin=citybfr.readLine())!=null){
                //System.out.println("city:"+cityin);
                String cityinfo []= cityin.split(" ");
                this.citymap.put(cityinfo[0],cityinfo[1]);
            }
            while((countyin=countybfr.readLine())!=null){
                //System.out.println("county:"+countyin);
                String countyinfo []= countyin.split(" ");
                if(countymap.get(countyinfo[0])==null)
                    this.countymap.put(countyinfo[0],countyinfo[1]);
                else {
                    this.rpcountymap.put(countyinfo[0], countyinfo[1]);
                   // System.out.println(countyinfo[0]+" "+countyinfo[1]);
                }
            }

            //System.out.println("save map finished! "+"province.size:"+provincemap.size()+" ,citymap.size:"+citymap.size()+" ,countymap.size:"
                   // +countymap.size()+" ,rpcountymap.size:"+rpcountymap.size());
            //System.out.println("测试朝阳："+countymap.get("朝阳"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        this.savemap();
    }
}
