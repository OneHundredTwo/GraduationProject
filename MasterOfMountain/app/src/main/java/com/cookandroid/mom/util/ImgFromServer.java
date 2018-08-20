package com.cookandroid.mom.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wdj46 on 2017-07-11.
 */
// 서버에서 이미지파일 불러오게끔 하는 클래스
public class ImgFromServer extends AsyncTask<String, Integer, Bitmap>{
    //서버 아이피
    private String imgSERVER_IP = Util.imgServerIP ;
    //서버 포트번호
    private String imgPORT = Util.imgServerPORT;
    private Bitmap bmImg;
    void setIP(String ip){
        imgSERVER_IP = ip;
    }
    String getIP(){
        return imgSERVER_IP;
    }

    void setPORT(String port){
        imgPORT = port;
    }
    String getPORT(){
        return imgPORT;
    }

    public ImgFromServer(){super();}

    ImgFromServer(String ip, String port){
        super();
        setIP(ip);
        setPORT(port);
    }


    @Override
    protected Bitmap doInBackground(String... params) {
        try{
            URL myFileUrl = new URL("http://" + imgSERVER_IP + imgPORT + "/" + "img/"+params[0]);
            HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            //url에서 불러와서 파싱하므로 decodeStream를 사용

            //사이즈 조절 decoding시 이미지를 줄여서 decoding할지 결정하는 옵션
            //ex) inSampleSize = 4 라면 비트맵 크기에 1/4크기로 만들라는 이야기
            bmImg = BitmapFactory.decodeStream(is);
        }catch (Exception e){
            e.getStackTrace();
        }
        return bmImg;

    }
}
