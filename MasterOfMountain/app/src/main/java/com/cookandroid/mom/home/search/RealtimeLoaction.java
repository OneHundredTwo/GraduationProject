package com.cookandroid.mom.home.search;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by RJH on 2017-10-24.
 */

public class RealtimeLoaction extends Service implements LocationListener {
    private final Context mContext;
    private final MtSafetyActivity mActivty;
    private long startTime = -1;

    // 현재 GPS 사용유무
    boolean isGPSEnabled = false;
    // 네트워크 사용유무
    boolean isNetworkEnabled = false;

    // GPS 상태값
    boolean isGetLocation = false;

    Location location;
    double currentLati; // 위도
    double currentLongi; // 경도
    double beforeLati; //바뀌기 이전 위도
    double beforeLongi; //바뀌기 이전 경도

    // GPS 정보 업데이트 시간 1/1000
    private static final long MIN_TIME_UPDATES = 0;//1000*45*1(45초)   //1000 * 60 * 1(1분)

    // GPS 정보 업데이트 거리 10미터
    private static final long MIN_DISTANCE_UPDATES = 0; //10;

    protected LocationManager locationManager;

    private Criteria criteria;

    public RealtimeLoaction(Context context, MtSafetyActivity activity) {
        this.mContext = context;
        this.mActivty = activity;
        criteria = initCriteria();

        getLocation();
        mActivty.currentLatitude = currentLati;
        mActivty.currentLongitude = currentLongi;
    }
    public Criteria initCriteria(){
        Criteria initCriteria = new Criteria();
        initCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        return initCriteria;
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            //둘다 불가능 할 경우
            if (!isGPSEnabled && !isNetworkEnabled) {
            } else {
                this.isGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_UPDATES,
                            MIN_DISTANCE_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            // 위도 경도 저장
                            currentLati = location.getLatitude();
                            currentLongi = location.getLongitude();
                            beforeLati = location.getLatitude();
                            beforeLongi = location.getLongitude();
                        }
                    }
                }

                //GPS 사용 가능 할 경우
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager
                                .requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER,
                                        MIN_TIME_UPDATES,
                                        MIN_DISTANCE_UPDATES,
                                        this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                currentLati = location.getLatitude();
                                currentLongi = location.getLongitude();
                                beforeLati = location.getLatitude();
                                beforeLongi = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    /**
     * GPS 종료
     * */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(RealtimeLoaction.this);
        }
    }

    /**
     * 위도값
     * */
    public double getLatitude() {
        if (location != null) {
            currentLati = location.getLatitude();
        }
        return currentLati;
    }

    /**
     * 경도값
     * */
    public double getLongitude() {
        if (location != null) {
            currentLongi = location.getLongitude();
        }
        return currentLongi;
    }

    public boolean isGetLocation() {
        return this.isGetLocation;
    }

    /**
     * GPS 정보를 가져오지 못했을때 설정값으로 갈지 물어보는 alert 창
     * */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                mContext);

        alertDialog.setTitle("GPS 사용유무셋팅");
        alertDialog
                .setMessage("GPS 셋팅이 되지 않았을수도 있습니다.\n 설정창으로 가시겠습니까?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onLocationChanged(Location location) {
        // TODO Auto-generated method

        if (startTime == -1) {
            startTime = location.getTime();
        }
        long delay = location.getTime() - startTime; //경과 시간
        Log.e("경과 시간: ", delay+"");

        beforeLati = currentLati;
        beforeLongi = currentLongi;

        currentLati = location.getLatitude();
        currentLongi = location.getLongitude();

        mActivty.currentLatitude = currentLati;
        mActivty.currentLongitude = currentLongi;
        if(beforeLati == currentLati && beforeLongi == currentLongi) {
            Toast.makeText(mContext, "좌표 변화없음", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(mContext, "좌표 바뀜", Toast.LENGTH_SHORT).show();
            //Toast.makeText(mContext, "현재 좌표: "+ mActivty.currentLatitude+", "+ mActivty.currentLongitude, Toast.LENGTH_SHORT).show();
        }


    }

    public void onStatusChanged(String provider, int status,
                                Bundle extras) {
        // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }
}
