package com.cookandroid.mom.mt.map;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cookandroid.mom.R;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import java.util.StringTokenizer;

/**
 * Created by RJH on 2017-10-18.
 */

public class CourseItemMapActivity extends AppCompatActivity {
    private MapView mapView;
    private ImageView btnGPSFixed, btnPlus, btnMinus;
    private ViewGroup mapViewContainer;
    private final int MY_PERMISSION_LOCATION = 1;
    private int gpsBtnCheck;
    private String[] rsltLatitude;
    private String[] rsltLongitude;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mt_map_courseitem);

        gpsBtnCheck = 0;
        Intent intent = getIntent();
        String mLatitude = intent.getStringExtra("mLatitude");
        String mLongitude = intent.getStringExtra("mLongitude");
        rsltLatitude = tokenizingCoordi(mLatitude);
        rsltLongitude = tokenizingCoordi(mLongitude);
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

        mapView.setMapViewEventListener(mapViewEventListener);

        MapPolyline courseItem = new MapPolyline();
        courseItem.setLineColor(Color.BLACK);
        for(int i=0; i<rsltLatitude.length; i++)
            courseItem.addPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(rsltLatitude[i]), Double.parseDouble(rsltLongitude[i])));
        mapView.addPolyline(courseItem);

        MapPointBounds mapPointBounds = new MapPointBounds(courseItem.getMapPoints());
        int padding = 1000; // px
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding,1,1));
    }

    //좌표 낱개로 뽑기기
    private String[] tokenizingCoordi(String mCoordinates){
        String check = ",";
        StringTokenizer tokenizingCoordi = new StringTokenizer(mCoordinates, check);
        String[] rsltCoordi = new String[tokenizingCoordi.countTokens()];
        for(int i=0; tokenizingCoordi.hasMoreElements(); i++) {
            rsltCoordi[i] = tokenizingCoordi.nextToken();
        }
        return rsltCoordi;
    }//End tokenizingCoordi() Method

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
    protected void onDestroy() {
        super.onDestroy();
    }

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
