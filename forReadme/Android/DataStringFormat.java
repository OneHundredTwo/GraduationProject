package com.cookandroid.mom.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by wdj46 on 2017-08-01.
 */
//페이스북 타임라인 형식으로 만드는 클래스
public class DataStringFormat {
   public final static int _SEC = 60;
   public final static int _MIN = 60;
   public final static int _HOUR = 24;
   public final static int _DAY = 7;
   public final static int _MONTH = 12;



    public final static SimpleDateFormat y_mon_d = new SimpleDateFormat("yyyy-MM-dd");
    public final static SimpleDateFormat y_mon_d_h_m = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public final static SimpleDateFormat y_mon_d_h_m_s =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public final static SimpleDateFormat timestamp_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String CreateDataWithCheck(String dateString){

        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
        java.util.Date date = null;
        try{
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long curTime = System.currentTimeMillis();//현시간 가져오기
        long regTime = date.getTime();//등록되어있는 시간 가져오기
        long diffTime = (curTime - regTime) /1000; //차이

        String msg =null;
        if(diffTime <  _SEC){
            //몇초전
            msg = "방금 전";
        }else if((diffTime /= _SEC) < _MIN) {
            //몇분 전
            msg = diffTime + "분 전";
        }else if((diffTime /= _MIN) < _HOUR){
            //몇시간 전
            msg = (diffTime) + "시간 전";
        }else if((diffTime /= _HOUR) < _DAY){
            //몇일 전
            msg = (diffTime) + "일 전";
        }else{
            SimpleDateFormat aformat = new SimpleDateFormat("yyyy-MM-dd");
            msg = aformat.format(date);
        }
        return msg;
    }
}
