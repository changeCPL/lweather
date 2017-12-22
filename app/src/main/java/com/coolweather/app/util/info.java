package com.coolweather.app.util;
import java.util.ArrayList;

public class info {
    String city;
    String curtemp;
    String ganmao;
    ArrayList <dayinfo> last5days = new ArrayList<dayinfo>();
    public String getCity() {
        return city;
    }



    public String getCurtemp() {
        return curtemp;
    }

    public String getGanmao() {
        return ganmao;
    }

    public void setCity(String city) {
        this.city = city;
    }



    public void setCurtemp(String curtemp) {
        this.curtemp = curtemp;
    }

    public void setGanmao(String ganmao) {
        this.ganmao = ganmao;
    }

    @Override
    public String toString() {
        return "info{" +
                "city='" + city + '\'' +
                ", curtemp='" + curtemp + '\'' +
                ", ganmao='" + ganmao + '\'' +
                ", last5days=" + last5days +
                '}';
    }
}

class dayinfo{
    String date;
    String high;
    String low;
    String type;
    String winddirection;
    String windstrength;


    dayinfo(String date,
            String high,
            String low,
            String type,
            String winddirection,
            String windstrength){
        setDate(date);
        setHigh(high);
        setLow(low);
        setType(type);
        setWinddirection(winddirection);
        setWindstrength(windstrength);
    }
    public void setDate(String date) {
        this.date = date;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setWinddirection(String winddirection) {
        this.winddirection = winddirection;
    }

    public void setWindstrength(String windstrength) {
        this.windstrength = windstrength;
    }

    public String getDate() {
        return date;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getType() {
        return type;
    }

    public String getWinddirection() {
        return winddirection;
    }

    public String getWindstrength() {
        return windstrength;
    }

    @Override
    public String toString() {
        return "dayinfo{" +
                "date='" + date + '\'' +
                ", high='" + high + '\'' +
                ", low='" + low + '\'' +
                ", type='" + type + '\'' +
                ", winddirection='" + winddirection + '\'' +
                ", windstrength='" + windstrength + '\'' +
                '}';
    }
}