package com.cookandroid.mom.home.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.mom.R;
import com.cookandroid.mom.util.Util;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

/**
 * Created by RJH on 2017-10-19.
 */

public class MtSearchActivity extends AppCompatActivity{
    private Context context = this;
    private MtSearchActivity mtSearchActivity = this;
    private MapView mapView;
    private  MapPoint mapPoint;
    private MapCircle circle;
    private GpsInfo gps;
    private ViewGroup mapViewContainer;
    public double realTimeLati, realTimeLongi;
    private double currentLati, currentLongi;
    ////////////////////

    ////////////////////
    private String[][] mtInfos;
    private String[] arrName;
    private String[] arrLatitude;
    private String[] arrLongitude;
    private String[] arrNum;
    private MapPOIItem[] marker;
    private MapPoint[] mapPointArr;
    private int max, zoomLevel;

    ArrayList<MtData> mtList;
    MtDataAdapter adapter;

    private SlidingDrawer drawer;
    private TextView handleText;
    private boolean rangeCheck;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_search_activity);

        try{
            mtInfos = Util.mtManager.getMtPoint();
            arrName = mtInfos[0];
            arrLatitude = mtInfos[1];
            arrLongitude = mtInfos[2];
            arrNum = mtInfos[3];

            for (int i = 0; i < arrName.length; i++)
                Log.e("확인", arrName[i] + "," + arrNum[i] + "," + arrLatitude[i] + "," + arrLongitude[i]);

        } catch (Exception e) {
            e.printStackTrace();
        }
        handleText = (TextView)findViewById(R.id.handleText);
        final ImageView handleImg = (ImageView)findViewById(R.id.handleImg);
        handleText.setText("등산할 산 선택");
        handleText.setTextColor(Color.WHITE);
        handleImg.setImageResource(R.drawable.ic_keyboard_arrow_up_white_48dp);
        drawer = (SlidingDrawer)findViewById(R.id.slidingdrawer);
        drawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                handleImg.setImageResource(R.drawable.ic_keyboard_arrow_down_white_48dp);
            }
        });
        drawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                handleImg.setImageResource(R.drawable.ic_keyboard_arrow_up_white_48dp);
            }
        });

        Intent intent = getIntent();
        max = intent.getIntExtra("km", 0);
        zoomLevel = intent.getIntExtra("zoom", 5);
        Log.e("max 값", max+"");
        gps = new GpsInfo(context, mtSearchActivity);
        if(gps.isGetLocation()){
            Log.e("Gps 유무", "잘 가져옴");
        }
        rangeCheck = false;
        final FloatingActionButton btnRange = (FloatingActionButton)findViewById(R.id.btnRange);
        btnRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rangeCheck){
                    //범위 off
                    btnRange.setImageResource(R.drawable.ic_visibility_off_white_24dp);
                    if(circle != null)
                        mapView.removeCircle(circle);
                    rangeCheck = false;
                }else{
                    //범위 on
                    btnRange.setImageResource(R.drawable.ic_visibility_white_24dp);
                    circle = new MapCircle(
                            MapPoint.mapPointWithGeoCoord(realTimeLati, realTimeLongi), // center
                            max*1000, // radius
                            Color.argb(128, 255, 0, 0), // strokeColor
                            0 // fillColor
                    );
                    circle.setTag(0);
                    mapView.addCircle(circle);
                    rangeCheck = true;
                }
            }
        });

        currentLati = gps.getLatitude();
        currentLongi = gps.getLongitude();
        Log.e("좌표", currentLati+", "+currentLongi);
        mapPoint = MapPoint.mapPointWithGeoCoord(currentLati, currentLongi);
        Log.e("mapPoint",  mapPoint.getMapPointGeoCoord().latitude+"");
        //mapView.setMapCenterPoint(mapPoint, true);
        mapView = new MapView(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
        mapView.setShowCurrentLocationMarker(true);
        //범위 선택에 따라 줌인 정도 바뀌도록
        mapView.moveCamera(CameraUpdateFactory.newMapPoint(mapPoint, zoomLevel));
        //mapView.setCurrentLocationRadius(3000); //내 위치로 부터 3km의 범위 표시

        mapView.setPOIItemEventListener(poiItemEventListener);
        mapViewContainer = (ViewGroup)findViewById(R.id.map_view2);
        mapViewContainer.addView(mapView);
        //(테스트용) 속리산 코스중 하나 좌표 36.56216940208309, 127.74026985726972
        mountainSearch(mapView, currentLati, currentLongi);



    }

    public void mountainSearch(MapView mapView, double myLati, double myLongi) {
        int mtCount = 0;
        mtList = new ArrayList<MtData>();
        // Log.e("arr length", "" + arrName.length);

        for (int i = 0; i < arrName.length; i++) {
            //위도 : 1도에 110979.309m 경도 : 1도에 88907.949m
            double distanceLati = Double.parseDouble(arrLatitude[i]) - myLati; //(위도) 산위치 - 내위치
            double distanceLongi = Double.parseDouble(arrLongitude[i]) - myLongi; //(경도) 산위치 - 내위치
            double latiToMeter = 110979.309 * Math.abs(distanceLati);  // 1도에 110979.309m 이기에 차이값(절대값)을 통해 곱함
            double longiToMeter = 88907.949 * Math.abs(distanceLongi);  // 마찬가지
            double rsltDistance = Math.sqrt((latiToMeter * latiToMeter) + (longiToMeter * longiToMeter)); //삼각함수로 x^2 + y^2의 루트값이 대각선 거리
            Log.e("산 : ", arrName[i]);
            Log.e("거리 : ", "" + rsltDistance);
            Log.e("내 위도 : ", "" + myLati);
            Log.e("내 경도 : ", "" + myLongi);

            if (rsltDistance <= (max*1000)) { //3km 내에 있는 산의 개수를 파악하며 산의 Num을 저장

                mtList.add(mtCount, new MtData(this, arrName[i], Math.round(rsltDistance*0.001), arrLatitude[i], arrLongitude[i], arrNum[i]));
                mtCount++;
                Log.e("mtCount", mtCount+"");
            }
        }
        Log.e("mtList SIze", mtList.size()+"");
        Log.e("mtCount", mtCount+"");
        adapter = new MtDataAdapter(this, mtList);

        mapPointArr = new MapPoint[mtCount]; //카운트 값만큼의 공간 정의
        marker = new MapPOIItem[mtCount];
        Log.e("mapPointArr 길이", mapPointArr.length+"" );

        for (int j = 0; j < mtCount; j++) { //카운트 값만큼(조건에 만족하는)의 marker 생성
            mapPointArr[j] = MapPoint.mapPointWithGeoCoord(Double.parseDouble(mtList.get(j).getmLatitude()), Double.parseDouble(mtList.get(j).getmLongitude()));
            marker[j] = new MapPOIItem();
            marker[j].setItemName(mtList.get(j).getmName());
            marker[j].setTag(j);
            marker[j].setMapPoint(mapPointArr[j]);
            marker[j].setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            marker[j].setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        }
        mapView.addPOIItems(marker);


        if(mtCount==0) {
            Toast.makeText(getApplicationContext(), max+"km 내에 산이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
            //mapViewContainer.removeAllViews();
            mapView.onSurfaceDestroyed();
            //Log.e("mapView확인",mapView.toString());
            finish();
        }else {
            //조건에 만족하는 산의 이름을 가져와 ListView에 출력
            ListView listView = (ListView) findViewById(R.id.listView);

            listView.setAdapter(adapter);
            //adapter.addAll(mtList);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //Toast.makeText(MtSearchActivity.this, "등산 시작할 액티비티 열기", Toast.LENGTH_SHORT).show();
                    //mapViewContainer.removeAllViews();
                    Intent intent = new Intent(MtSearchActivity.this, MtSafetyActivity.class);
                    mapViewContainer.removeAllViews();
                    drawer.close();
                    intent.putExtra("mName", mtList.get(i).getmName());
                    intent.putExtra("mNum", mtList.get(i).getmNum());
                    intent.putExtra("mtLatitude", mtList.get(i).getmLatitude());
                    intent.putExtra("mtLongitude", mtList.get(i).getmLongitude());
                    //Toast.makeText(MtSearchActivity.this, mtList.get(i).getmName()+","+mtList.get(i).getmNum(), Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            });
        }
    }

    private class MtDataAdapter extends ArrayAdapter<MtData> {
        // 레이아웃 XML을 읽어들이기 위한 객체
        private LayoutInflater mInflater;

        public MtDataAdapter(Context context, ArrayList<MtData> object) {

            // 상위 클래스의 초기화 과정
            // context, 0, 자료구조
            super(context, 0, object);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            View view = null;

            // 현재 리스트의 하나의 항목에 보일 컨트롤 얻기

            if (v == null) {

                // XML 레이아웃을 직접 읽어서 리스트뷰에 넣음
                view = mInflater.inflate(R.layout.home_list_search, null);
            } else {

                view = v;
            }

            // 자료를 받는다.
            final MtData data = this.getItem(position);

            if (data != null) {
                // 화면 출력
                TextView searchMt = (TextView) view.findViewById(R.id.searchMt);
                TextView searchDis = (TextView) view.findViewById(R.id.searchDis);
                searchMt.setText(data.getmName());
                if((int)data.getmDistance()==0){
                    searchDis.setText("약 1km 이내");
                }else{
                    searchDis.setText("약 "+(int)data.getmDistance()+"km 이내");
                }
                searchMt.setTextColor(Color.WHITE);
                searchDis.setTextColor(Color.WHITE);



            }
            return view;
        }
    }

    // CData안에 받은 값을 직접 할당
    private class MtData {
        private Context context;
        private String mName, mNum;
        private double mDistance;
        private String mLatitude, mLongitude;
//        private int m_pic, m_imageView;

        public MtData(Context context, String mName, double mDistance, String mLatitude, String mLongitude, String mNum) {
            this.context = context;
            this.mName = mName;
            this.mDistance = mDistance;
            this.mLatitude = mLatitude;
            this.mLongitude = mLongitude;
            this.mNum = mNum;

        }
        public String getmName() {
            return mName;
        }

        public double getmDistance() {
            return mDistance;
        }

        public String getmLatitude() {
            return mLatitude;
        }

        public String getmLongitude() {
            return mLongitude;
        }

        public String getmNum() {
            return mNum;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mapView.setShowCurrentLocationMarker(false);
        //mapView.onSurfaceDestroyed();
        //mapViewContainer.removeAllViews();
    }

    MapView.POIItemEventListener poiItemEventListener = new MapView.POIItemEventListener() {
        @Override
        public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
            //여기에 인텐트로 MtSafetyActivity로 인텐트 하는 소스 만듬
            String mName = mapPOIItem.getItemName();
            String mNum = getMtNum(mName);
            Intent intent = new Intent(MtSearchActivity.this, MtSafetyActivity.class);
            mapViewContainer.removeAllViews();
            intent.putExtra("mName", mName);
            intent.putExtra("mNum", mNum);
            intent.putExtra("mtLatitude", mtList.get(mapPOIItem.getTag()).getmLatitude());
            intent.putExtra("mtLongitude", mtList.get(mapPOIItem.getTag()).getmLongitude());
            //Toast.makeText(MtSearchActivity.this, mName+","+mNum, Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        private String getMtNum(String mName){
            String mtNum = "";
            for(int i=0; i<arrName.length; i++){
                if(mName.equals(arrName[i]))
                    mtNum = arrNum[i];
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

    @Override
    protected void onRestart() {
        super.onRestart();
        mapView = new MapView(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
        mapView.setShowCurrentLocationMarker(true);
        //범위 선택에 따라 줌인 정도 바뀌도록
        mapView.moveCamera(CameraUpdateFactory.newMapPoint(mapPoint, zoomLevel));
        mapViewContainer.addView(mapView);
        mapView.setPOIItemEventListener(poiItemEventListener);
        mountainSearch(mapView, currentLati, currentLongi);
    }
}
