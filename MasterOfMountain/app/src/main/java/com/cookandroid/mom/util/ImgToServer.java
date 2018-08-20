package com.cookandroid.mom.util;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wdj46 on 2017-08-30.
 */

public class ImgToServer extends AsyncTask<String, String, Void>{
    //서버 아이피
    private String imgSERVER_IP = Util.imgServerIP ;
    //서버 포트번호
    private String imageName;
    private String imgPORT = Util.imgServerPORT;
    private String URL = "UploadImageInfo.jsp";  //<<서버주소
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    public ImgToServer(){super();}

    @Override
    protected Void doInBackground(String... params) {

        try {

            FileInputStream mFileInputStream = new FileInputStream(params[0]);

            URL connectUrl = new URL("http://" + imgSERVER_IP + imgPORT+"/" +URL);
            Log.d("Test", "mFileInputStream  is " + mFileInputStream);

            // HttpURLConnection 통신
            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            // write data
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            // userName으로 아이디 가져와서 구분
            String userName = Util.user.getId();
            Log.e("ImgToServer 확인", userName);
            dos.writeBytes("Content-Disposition: form-data; name=\""+ userName +"\";filename=\"" + params[0] + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            int bytesAvailable = mFileInputStream.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            byte[] buffer = new byte[bufferSize];
            int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

            Log.d("Test", "image byte is " + bytesRead);

            // read image
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = mFileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            Log.e("Test", "File is written");
            mFileInputStream.close();
            dos.flush();
            // finish upload...

            // get response
            InputStream is = conn.getInputStream();

            StringBuffer b = new StringBuffer();
            for (int ch = 0; (ch = is.read()) != -1; ) {
                b.append((char) ch);
            }
            is.close();
            Log.e("Test", b.toString());


        } catch (Exception e) {
            Log.d("Test", "exception " + e.getMessage());
            e.printStackTrace();
            // TODO: handle exception
        }
        return null;
    }


    public String getImagePathToUri(String data) {
        //사용자가 선택한 이미지의 정보를 받아옴
        //이미지의 경로 값
        Log.d("경로test", data);

        //이미지의 이름 값
        String imgName = data.substring(data.lastIndexOf("/") + 1);
        this.imageName = imgName;

        return imgName;
    }


    public String getImagePathToUri(Uri data) {
        //사용자가 선택한 이미지의 정보를 받아옴
        //이미지의 경로 값
        String imgPath = data.getPath();
        Log.d("경로test", imgPath);

        //이미지의 이름 값
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);
        this.imageName = imgName;

        return imgName;
    }//end of getImagePathToUri()





}
