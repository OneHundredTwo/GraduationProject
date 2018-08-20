package com.cookandroid.mom.mt.map;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.cookandroid.mom.R;
import com.cookandroid.mom.mt.MtDetailInfoActivity;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MapActivity extends AppCompatActivity implements Serializable{
        private MapView mapView;
        private ImageView btnGPSFixed, btnPlus, btnMinus;
        private ViewGroup mapViewContainer;
        private final int MY_PERMISSION_LOCATION = 1;
        private String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        private MtMarker mtMarker;
        private int gpsBtnCheck;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.mt_map);
            checkPermission();
            gpsBtnCheck = 0;
            Intent intent = getIntent();
            mapView = new MapView(this);
            mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
            mapViewContainer.addView(mapView);

            btnPlus = (ImageView)findViewById(R.id.plus);
            btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapView.zoomIn(true);
                }
            });
            btnMinus = (ImageView)findViewById(R.id.minus);
            btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapView.zoomOut(true);
                }
            });
            btnGPSFixed = (ImageView)findViewById(R.id.gpsFixed);
            btnGPSFixed.setImageResource(R.drawable.gps1);
            btnGPSFixed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (gpsBtnCheck){
                        case 0:
                            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                            mapView.setZoomLevel(0,true);
                            btnGPSFixed.setBackground(null);
                            btnGPSFixed.setBackgroundResource(R.drawable.button_gps2);
                            btnGPSFixed.setImageResource(R.drawable.gps2);
                            gpsBtnCheck = 1;
                            break;
                        case 1:
                            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
                            btnGPSFixed.setBackground(null);
                            btnGPSFixed.setBackgroundResource(R.drawable.button_gps2);
                            btnGPSFixed.setImageResource(R.drawable.explore);
                            gpsBtnCheck = 2;
                            break;
                        case 2:
                            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
                            btnGPSFixed.setBackground(null);
                            btnGPSFixed.setBackgroundResource(R.drawable.button_plus_minus_false);
                            btnGPSFixed.setImageResource(R.drawable.gps1);
                            gpsBtnCheck = 0;
                            break;
                        default:
                            break;
                    }
                }
            });

            //DrawRouteMt.polyline(mapView);
            mtMarker = new MtMarker();
            mtMarker.drawMtMarker(mapView);
            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(36.46958333333333, 127.86173333333333), 11,true);
            mapView.setPOIItemEventListener(poiItemEventListener);
            mapView.setMapViewEventListener(mapViewEventListener);
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
        //mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mapView.setShowCurrentLocationMarker(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mapView = new MapView(this);
        mapViewContainer.addView(mapView);
        //DrawRouteMt.polyline(mapView);
        mtMarker.drawMtMarker(mapView);
        //drawMtCourseAll.drawMtCourseAll();
        mapView.setPOIItemEventListener(poiItemEventListener);
    }

        public void checkPermission(){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                int result;
                List<String> permissionList = new ArrayList<>();
                for(String pm : permissions){
                    result = ContextCompat.checkSelfPermission(this, pm);
                    if(result != PackageManager.PERMISSION_GRANTED){
                        permissionList.add(pm);
                    }
                }
                if(!permissionList.isEmpty()){
                    ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MY_PERMISSION_LOCATION);
                }
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) || (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))) {
                        new AlertDialog.Builder(this)
                                .setTitle("알림")
                                .setMessage("위치 권한이 거부 되어있습니다. 사용을 원하시면 설정에서 해당 권한을 허용해 주세요.")
                                .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.parse("package:" + getPackageName()));
                                        startActivity(intent);
                                    }
                                })
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .setCancelable(false)
                                .create()
                                .show();
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_LOCATION);
                    }
                }
            }
        }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            switch (requestCode) {
                case MY_PERMISSION_LOCATION: {
                    if (grantResults.length > 0) {
                        for (int i = 0; i < permissions.length; i++) {
                            if (permissions[i].equals(this.permissions[0])) {
                                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                    showNoPermissionToastAndFinish();
                                }
                            }else if(permissions[i].equals(this.permissions[1])){
                                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                    showNoPermissionToastAndFinish();
                                }
                            }
                        }
                    } else {
                        showNoPermissionToastAndFinish();
                    }
                    return;
                }
            }
        }else {
            switch (requestCode) {
                case MY_PERMISSION_LOCATION: {
                    for (int i = 0; i < grantResults.length; i++) {
                        //grantResults[] : 허용된 권한은 0, 거부한 권한은 -1
                        if (grantResults[i] < 0) {
                            Toast.makeText(MapActivity.this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    //허용했다면 이부분에서
                    break;
                }
            }
        }
    }

        private void showNoPermissionToastAndFinish(){
            Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다", Toast.LENGTH_SHORT).show();
            new AlertDialog.Builder(this)
                    .setTitle("알림")
                    .setMessage("위치 권한이 거부되었습니다. 사용을 원하신다면 설정에서 해당 권한을 직접 허용하셔야합니다.")
                    .setNeutralButton("설정", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i){
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:"+getPackageName()));
                            startActivity(intent);
                        }
                    })
                    .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i){
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }

    //Reverse Geo-Coding 결과를 비동기적으로 통보받는 리스너
    MapReverseGeoCoder.ReverseGeoCodingResultListener reverseGeoCodingResultListener = new MapReverseGeoCoder.ReverseGeoCodingResultListener() {
       @Override
       public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
           mapReverseGeoCoder.toString();
           onFinishReverseGeoCoding(s);
       }

       @Override
       public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
           onFinishReverseGeoCoding("Fail");
       }

       private void onFinishReverseGeoCoding(String result) {
            Toast.makeText(MapActivity.this, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
        }
      };

    //POI 관련 이벤트를 통보받는 리스너
    MapView.POIItemEventListener poiItemEventListener = new MapView.POIItemEventListener() {
        @Override
        public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
            Log.e("리스너", "확인");
        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
            //말풍선 터치 리스너
            Log.e("말풍선", "확인");
            String mtName = mapPOIItem.getItemName();
            Log.e("말풍선 mtName", mtName);
            String mtCode = getMtNum(mtName);
            Log.e("말풍선 mtCode", mtCode);
            Intent intent = new Intent(getApplicationContext(),MtDetailInfoActivity.class);
            intent.putExtra("mtName", mtName);
            intent.putExtra("mtCode", mtCode);
            startActivity(intent);

        }
        public String getMtNum(String sanName_f){
            String mtNum = "";
            for(int i=0; i<mtMarker.getArrName().length; i++){
                if(sanName_f.equals(mtMarker.getName(i)))
                    mtNum = mtMarker.getNum(i);
            }
            return mtNum;
        }


        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

        }

        @Override
        public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

        }
    };

    //지도 이동/확대/축소, 지도 화면 터치(Single Tap / Double Tap / Long Press) 이벤트를 통보받는 리스너
    MapView.MapViewEventListener mapViewEventListener = new MapView.MapViewEventListener() {
        @Override
        public void onMapViewInitialized(MapView mapView) {

        }

        @Override
        public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewZoomLevelChanged(MapView mapView, int i) {

        }

        @Override
        public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
            btnGPSFixed.setBackground(null);
            btnGPSFixed.setBackgroundResource(R.drawable.button_plus_minus_false);
            btnGPSFixed.setImageResource(R.drawable.gps1);
            gpsBtnCheck = 0;
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
        }

        @Override
        public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

        }
    };

}
