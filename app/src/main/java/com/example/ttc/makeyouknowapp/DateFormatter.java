package com.example.ttc.makeyouknowapp;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ttc on 2017/3/13.
 */

public class DateFormatter {
    public String ZhihuDailyDateFormat(Date date) {
        String sDate;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        sDate = format.format(date);
        return sDate;
    }

    public Date stringToDate(String dateString){
        Date date=null;
        SimpleDateFormat sim = new SimpleDateFormat("yyyyMMdd");
        try{

          date = sim.parse(dateString);

        }catch (ParseException e){
            Log.d("String to Date Error!:",dateString);
        }
        return date;
    }

    public Date dateAddOne(Date date){
        long time = date.getTime();
        time = time+24*60*60*1000;
        Date date1 = new Date(time);
        return  date1;
    }
    public Date dateMinusOne(Date date){
        long time = date.getTime();
        time = time-24*60*60*1000;
        Date date1 = new Date(time);
        return  date1;
    }
}
