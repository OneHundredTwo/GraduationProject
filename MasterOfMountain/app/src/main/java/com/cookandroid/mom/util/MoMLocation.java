package com.cookandroid.mom.util;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Shining on 2017-08-16.
 * GPS로 좌표정보를 가져와서 제공하는 클래스.
 */

public class MoMLocation extends Service implements LocationListener{
    private final Context context;
    public MoMLocation(Context context){
        this.context = context;
        getLocation();
    }

    //현재 GPS 사용유무
    boolean isGPSEnabled = false;

    //네트워크 사용유무
    boolean isNetworkEnabled = false;

    //GPS 상태값
    boolean isGetLocation = false;

    Location location;
    double lat;//위도
    double lon;//경도

    //최소 GPS 정보 업데이트 거리(m)
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;

    //최소 GPS 정보 업데이트 시간(ms)
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 5;

    //안드로이드 시스템에서 디바이스의 좌표정보를 제공하는 시스템자원을 앱에서 이용하기위한 클래스.
    protected LocationManager locationManager;

    private Location getLocation(){
        try {
            locationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);
            //GPS 허가정보 가져오기
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); //사용자가 안드로이드 시스템의 '위치'서비스가 켰는지 체크하는 코드.
            //현재 네트워크 상태 값 알아오기
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled) {
                //GPS와 네트워크 사용이 가능하지 않을 때 소스구현
           }else {
                isGetLocation = true;

                //조건문의 메소드를 실행 후 허용(false)여야 location을 수행함 . (사용자의 허가를 받는)해당코드가 꼭 있어야 작동함.
                //사용자가 앱권한에서 Manifest에 추가한 안드로이드 시스템기능을 사용할 수 있도록 허용하는지 확인하는 코드.
                //해당 권한의 허용을 사용자에게 구체적으로(설명을 부가해서)물어보려면 액티비티에서만 가능하므로, 액티비티에서 해당 서비스의 기능을 이용하는 이벤트부분에 권한을 체크하고
                //권한을 설정하도록 한다.
                if (
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        ) {
                } else {
                    //네트워크 정보로 부터 위치값 가져오기
                    if (isNetworkEnabled) {
                        //시스템 자원인 LocationManager에 좌표정보 업데이트를 요청.
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                //위경도 저장
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }
                    }

                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                    this
                            );
                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    lat = location.getLatitude();
                                    lon = location.getLongitude();
                                }
                            }
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return location;
    }

    //GPS종료
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(MoMLocation.this);
        }
    }

    //위도값을 가져옵니다.
    public double getLatitude(){
        if(location != null){
            lat = location.getLatitude();
        }
        return lat;
    }

    //경도값을 가져옵니다.
    public double getLongitude(){
        if(location != null){
            lon = location.getLongitude();
        }
        return lon;
    }

    //GPS나 wifi정보가 켜져있는지 확인합니다.
    public boolean isGetLocation(){
        return this.isGetLocation;
    }

    //GPS정보를 가져오지 못했을때 설정창으로 이동할지 물어보는 alert창
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("GPS 사용유무 셋팅")
                .setMessage("GPS 셋팅이 되지 않았을 수도 있습니다. \n설정창으로 가시겠습니끼ㅏ?")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whitch){
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whitch){
                        dialog.cancel();
                    }
                }).show();

    }

    public void locationMapping(String file_path, String lat, String lon){
        /*
        1.사진파일과 같은 이름의 텍스트파일생성
        2.텍스트파일에 좌표저장.
        혹은
        1.picture_location파일을 연다.
        2.파일이름과 좌표를 형식에 맞춰서 입력한다.(지울땐?)
        혹은
        데이터베이스를 쓴다.
        * */

       /* getLocation();
        String lat = String.valueOf(this.lat);//latitude
        String lon = String.valueOf(this.lon);//longitude*/

        String filename = file_path.substring(file_path.lastIndexOf("/") + 1);
        //String str_loc = lat+" "+lon;
        Log.e("MoMLocation class", "locationMapping() method");
        Log.e("MoMLocation-fileName", filename);
        Log.e("MoMLocation-lat", lat);
        Log.e("MoMLocation-lon", lon);
        MoMDBConnection momDB = new MoMDBConnection(context);
        momDB.insertPicture(filename, lat, lon);
        momDB.close();

    }

    @Override
    public void onLocationChanged(Location location){

    }
    @Override
    public void onProviderDisabled(String provider){

    }
    @Override
    public void onProviderEnabled(String provider){

    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){

    }

    public IBinder onBind(Intent intent){
        return null;
    }

}
