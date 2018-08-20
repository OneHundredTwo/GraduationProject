package com.cookandroid.mom.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wdj46 on 2017-07-11.
 */
// 서버에서 이미지파일 불러오게끔 하는 클래스
public class PhotoImgFromServer extends AsyncTask<String, Integer, Bitmap> {
    //서버 아이피
    private String imgSERVER_IP = Util.imgServerIP ;
    //서버 포트번호
    private String imgPORT = Util.imgServerPORT;

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

    public PhotoImgFromServer(){super();}

    PhotoImgFromServer(String ip, String port){
        super();
        setIP(ip);
        setPORT(port);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bmImg=null;
        try{
            for (int i=0; i<params.length; i++) {
                Log.e("params["+i+"]값 : ", params[i]);
            }
            URL myFileUrl = new URL("http://" + imgSERVER_IP + imgPORT + "/img/" +params[0]);
            Log.e("URL 값 : ", ""+myFileUrl);
            HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();

            //url에서 불러와서 파싱하므로 decodeStream를 사용
            //사이즈 조절 decoding시 이미지를 줄여서 decoding할지 결정하는 옵션
            //ex) inSampleSize = 4 라면 비트맵 크기에 1/4크기로 만들라는 이야기
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize=16;
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;

            options.inJustDecodeBounds = false;
            bmImg = BitmapFactory.decodeStream(is, null, options);
            Log.e("크기 확인", imageHeight+", "+imageWidth);
        }catch (Exception e){
            e.getStackTrace();
        }
        return bmImg;
    }
}
