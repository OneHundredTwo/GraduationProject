package com.cookandroid.mom.home.search;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cookandroid.mom.R;
import com.cookandroid.mom.util.Exercise;
import com.cookandroid.mom.util.Util;
import com.google.android.gms.common.api.GoogleApiClient;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by RJH on 2017-10-21.
 */

public class MtSafetyActivity extends Exercise{
    public final int added = 3000;

    private LinearLayout health;

    private Context context = this;
    private MtSafetyActivity mtSafetyActivity = this;
    private RealtimeLoaction realtimeLoaction;
    private CompareLocationAndAlarmTask compareLocationAndAlarmTask;
    private boolean safetyCheck; //안전 범위를 벗어 났을 경우를 알기 위한 변수
    private int numberOfCourse; //코스 점 좌표 개수
    protected double currentLatitude, currentLongitude; // 25미터 반경과 비교하기 위한 현재 위치 좌표
    private Vibrator vibrator;

    protected MapView mapView;
    private ViewGroup mapViewContainer;

    private SlidingDrawer drawer;
    private TextView handleText;
    private MapPoint mtPoint;
    private MapPointBounds coursePointBounds;
    private MapPOIItem mtMarker;
    ///////////////////////////
    private String[] arrNum, arrCourseNum, arrLatitude, arrLongitude, arrEmdCd, arrSec_len, arrUp_min, arrDown_min, arrStart_z, arrEnd_z, arrCat_nam;
    private String[][] courseInfo;
    private String[] rsltLatitude, rsltLongitude;//코스 낱개 좌표
    private GoogleApiClient mApiClient;
    private int btnCheck;
    private int courseCheck;
    private String startLatitude, startLongitude; //코스 시작점 좌표
    private String endLatitude, endLongitude; //코스 종점 좌표
    private int courseNum;
    private MapPOIItem[] startEndMarker;
    private Notification.Builder notiBuilder;

    //우측 드로우어에 띄우기 위해 사용되는 변수////////////////////////////////////////////
    private String[][] photo;
    private String[] arrPhoto;
    private String[] arrLat;
    private String[] arrLon;
    private Bitmap[] img;

    private GridView gridView;
    private ImageAdapter imageAdapter;
    private ArrayList<ImageItem> arrBitimg;
    private ViewHolder holder;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_search_safety_acti);
        final FloatingActionButton btnStart = (FloatingActionButton)findViewById(R.id.btnStart);
        final FloatingActionButton btnStop = (FloatingActionButton)findViewById(R.id.btnStop);
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        btnCheck = 0;
        btnStart.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        btnStop.setImageResource(R.drawable.ic_stop_white_24dp);
        btnStop.setVisibility(View.GONE);

        //health ui setting
        health = (LinearLayout)findViewById(R.id.healthlayout);

        timer = (TextView)findViewById(R.id.timer);
        step = (TextView)findViewById(R.id.step);

        super.timer = timer;
        super.step = step;

        //google 피트니스 api를 사용하기 위한 client 설정
        setmApiClient(mApiClient);
        init();

        Intent intent = getIntent();
        String mNum = intent.getStringExtra("mNum");
        final String mName = intent.getStringExtra("mName");
        String mtLatitude = intent.getStringExtra("mtLatitude");
        String mtLongitude = intent.getStringExtra("mtLongitude");
        mtPoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(mtLatitude), Double.parseDouble(mtLongitude));
        try{
            courseInfo = Util.mtManager.getMtCourses(mNum);
            arrNum = courseInfo[0];
            arrCourseNum = courseInfo[1];
            arrLatitude = courseInfo[2];
            arrLongitude = courseInfo[3];
            arrSec_len = courseInfo[5];
            arrUp_min = courseInfo[6];
            arrDown_min = courseInfo[7];
            arrCat_nam = courseInfo[10];

            for (int i = 0; i < arrNum.length; i++)
                Log.e("확인", arrCourseNum[i] + "," + arrNum[i] + "," + arrLatitude[i] + "," + arrLongitude[i]);

        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView = new MapView(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
        mapView.moveCamera(CameraUpdateFactory.newMapPoint(mtPoint, 5));
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view2);
        mapViewContainer.addView(mapView);

        mtMarker = new MapPOIItem();
        mtMarker.setItemName(mName);
        mtMarker.setTag(0);
        mtMarker.setMapPoint(mtPoint);
        mtMarker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        mtMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        mapView.addPOIItem(mtMarker);
        //=====================================좋아요 누른 사진 우측 slidingdrawer로 가져오기===================================//
        arrBitimg = new ArrayList<ImageItem>();
        gridView = (GridView)findViewById(R.id.gridView);
        imageAdapter = new ImageAdapter(this, R.layout.home_safety_mtpic_list, arrBitimg);
        gridView.setAdapter(imageAdapter);
        try{
            photo = Util.mtManager.getPhoto(mName);
            arrPhoto = photo[0];//사진 이름
            arrLat = photo[1];//사진 위도
            arrLon = photo[2];//사진 경도
            img = new Bitmap[arrPhoto.length];

            for (int i = 0; i < arrPhoto.length; i++) {
                Log.e("확인", arrPhoto[i] + "," + arrLat[i] + "," + arrLon[i]);
            }
            SlidingDrawer likeDrawer = (SlidingDrawer)findViewById(R.id.slide);
            final ImageView fab_point = (ImageView)findViewById(R.id.fab_point);
            likeDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
                @Override
                public void onDrawerOpened() {
                    fab_point.setImageResource(R.drawable.ic_keyboard_arrow_right_white_48dp);
                }
            });
            likeDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
                @Override
                public void onDrawerClosed() {
                    fab_point.setImageResource(R.drawable.ic_keyboard_arrow_left_white_48dp);
                }
            });

            for(int i=0; i<arrPhoto.length; i++){
                img[i] = Util.getPIFS().execute(arrPhoto[i]).get();
                ImageItem imageItem = new ImageItem();
                imageItem.setM_img(img[i]);
                imageItem.setM_Lat(Double.parseDouble(arrLat[i]));
                imageItem.setM_Lon(Double.parseDouble(arrLon[i]));

                MapPoint likeMapPoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(arrLat[i]), Double.parseDouble(arrLon[i]));
                MapPOIItem likeMarker = new MapPOIItem();
                likeMarker.setItemName("" + i);
                likeMarker.setTag(i);
                likeMarker.setMapPoint(likeMapPoint);
                likeMarker.setMarkerType(MapPOIItem.MarkerType.BluePin);
                likeMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

                imageItem.setLikeMarker(likeMarker);
                imageItem.setM_selection(false);
                arrBitimg.add(imageItem);
            }
            for(int i=0; i<arrBitimg.size(); i++){
                Log.e("arrBitimg 확인",arrBitimg.get(i).getM_Lat()+", "+arrBitimg.get(i).getM_Lon());
            }
           /* for(int i=0; i<arrPhoto.length; i++){ //이미지를 받는다.
                try {
                    img[i] = Util.getPIFS().execute(arrPhoto[i]).get();
                }catch (Exception e){
                    e.printStackTrace();
                }
                byte[] pic = bitmapToByte(img[i]);
                Log.e("이미지 잘 가져오는지 확인",img[i].toString());
                imageAdapter.add(new ImageItem(getApplicationContext(), img[i], Double.parseDouble(arrLat[i]), Double.parseDouble(arrLon[i]), false, pic));

            }*/
        }catch (Exception e){
            e.getStackTrace();
        }


        //======================================================================================================================//

        handleText = (TextView)findViewById(R.id.handleText);
        final ImageView handleImg = (ImageView)findViewById(R.id.handleImg);
        handleText.setText(mName + " 코스 선택");
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

        ListView listView = (ListView)findViewById(R.id.listView);
        final ArrayList<CourseData> courseList = new ArrayList<>();
        CourseDataAdapter courseDataAdapter = new CourseDataAdapter(getApplicationContext(), courseList);
        listView.setAdapter(courseDataAdapter);
        for(int i=0; i<arrNum.length; i++){
            courseDataAdapter.add(new CourseData(getApplicationContext(), arrNum[i], arrCourseNum[i],
                    arrLatitude[i], arrLongitude[i] ,arrSec_len[i], arrUp_min[i], arrDown_min[i]
                    , arrCat_nam[i]));
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if((btnCheck == 1 || btnCheck ==2)&&courseCheck != position) {
                    btnStop.setVisibility(View.GONE);
                    btnStart.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                    btnCheck = 0;
                    mapView.removePOIItems(startEndMarker);
                }
                if(realtimeLoaction != null) {
                    realtimeLoaction.stopUsingGPS();
                    realtimeLoaction = null;
                    //Toast.makeText(context, "백버튼으로 잘 종료됨", Toast.LENGTH_SHORT).show();
                }
                if(compareLocationAndAlarmTask != null) {
                    compareLocationAndAlarmTask.cancel(true);
                    compareLocationAndAlarmTask = null;
                }
                if((btnCheck == 1 || btnCheck ==2)&&courseCheck == position) {
                    Toast.makeText(context, "해당 코스입니다. 다른 코스를 선택해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    handleText.setText(mName + " 제 " + (position + 1) + "코스");
                    drawer.close();
                    rsltLatitude = tokenizingCoordi(arrLatitude[position]);
                    numberOfCourse = rsltLatitude.length;
                    rsltLongitude = tokenizingCoordi(arrLongitude[position]);
                    courseNum = Integer.parseInt(arrCourseNum[position]);
                    startLatitude = rsltLatitude[0];
                    startLongitude = rsltLongitude[0];
                    endLatitude = rsltLatitude[rsltLatitude.length - 1];
                    endLongitude = rsltLongitude[rsltLongitude.length - 1];
                    for (int i = 0; i < rsltLatitude.length; i++) {
                        Log.e("rlstLatitude", rsltLatitude[i] + ",");
                        Log.e("rlstLongitude", rsltLongitude[i] + ",");
                    }
                    MapPolyline courseItem = new MapPolyline();
                    mapView.removeAllPolylines();
                    //mapView.removeAllPOIItems();
                    courseItem.setLineColor(Color.BLACK);
                    for (int i = 0; i < rsltLatitude.length; i++)
                        courseItem.addPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(rsltLatitude[i]), Double.parseDouble(rsltLongitude[i])));
                    mapView.addPolyline(courseItem);
                    coursePointBounds = new MapPointBounds(courseItem.getMapPoints());
                    mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(coursePointBounds, 100));
                    courseCheck = position;
                }
            }


        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (btnCheck){
                    case 0:
                        //btnCheck=0 상태에서 눌렀을 때 시작 되면서 버튼 바뀜 btnCheck=0 -> 1

                        //safety 모드를 시작하겠냐고 물어본 후 시작
                        // (선택 된 코스가 없을 경우 코스 선택하라고 Toast 띄울 것)

                        if(startLatitude == null){
                            Toast.makeText(MtSafetyActivity.this, "코스를 선택해주세요.", Toast.LENGTH_SHORT).show();
                        }else{
                            realtimeLoaction = new RealtimeLoaction(context,mtSafetyActivity);
                            //*****현재 있는 위치와 시작점과의 거리차이가 많이 날 때
                            //*****안전 모드 사용 못하게 하기 위한 코드
                            //*****(수리산 or 학교 근처 좌표 DB에 넣었을 때 사용)
                            //종점좌표 - 시작점좌표(현재 거리 기준)(종점부터 시작점 사이 거리)
                            double standardDis1 = getRealDistance(Double.parseDouble(endLatitude), Double.parseDouble(endLongitude), Double.parseDouble(startLatitude), Double.parseDouble(startLongitude));
                            //현재좌표 - 시작점좌표(내 위치에서 시작점 사이 거리)
                            double standardDis2 = getRealDistance(currentLatitude, currentLongitude, Double.parseDouble(startLatitude), Double.parseDouble(startLongitude));
                            //현재좌표 - 끝점좌표(내 위치 에서 끝점 사이 거리)
                            double startardDis3 = getRealDistance(currentLatitude, currentLongitude, Double.parseDouble(endLatitude), Double.parseDouble(endLongitude));
                            //시작 점과 너무 차이가 나는 경우(standardDis1보다 멀 경우)
                            if(standardDis1 < standardDis2 && standardDis1 < startardDis3){
                                if (realtimeLoaction != null) {
                                    realtimeLoaction.stopUsingGPS();
                                    realtimeLoaction = null;
                                    //Toast.makeText(context, "백버튼으로 잘 종료됨", Toast.LENGTH_SHORT).show();
                                }
                                Toast.makeText(MtSafetyActivity.this, "근처에 코스가 존재하지 않아 안전모드 사용이 불가능합니다. 코스 근처로 이동해주세요.", Toast.LENGTH_SHORT).show();
                            }else {
                            //Toast.makeText(context, "시작 좌표: "+ realtimeLoaction.getLatitude()+", "+realtimeLoaction.getLongitude(), Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MtSafetyActivity.this);
                            alert_confirm.setMessage("안전모드를 시작하시겠습니까?").setCancelable(false).setPositiveButton("예",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            health.setVisibility(View.VISIBLE);
                                            // 'YES'
                                            //health start
                                            healthStart();
                                            mapView.setShowCurrentLocationMarker(true);
                                            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
                                            mapView.removePOIItem(mtMarker);
                                            btnStop.setVisibility(View.VISIBLE);
                                            btnStart.setImageResource(R.drawable.ic_pause_white_24dp);
                                            topBarSetting();//알림
                                            MapPoint[] startEndPoint = new MapPoint[2];
                                            startEndMarker = new MapPOIItem[2];
                                            startEndPoint[0] = MapPoint.mapPointWithGeoCoord(Double.parseDouble(startLatitude), Double.parseDouble(startLongitude));
                                            startEndPoint[1] = MapPoint.mapPointWithGeoCoord(Double.parseDouble(endLatitude), Double.parseDouble(endLongitude));
                                            startEndMarker[0] = new MapPOIItem();
                                            startEndMarker[1] = new MapPOIItem();

                                            startEndMarker[0].setItemName("코스 번호" + courseNum + " 시작점");
                                            startEndMarker[0].setTag(courseNum);
                                            startEndMarker[0].setMapPoint(startEndPoint[0]);
                                            startEndMarker[0].setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                                            startEndMarker[0].setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

                                            startEndMarker[1].setItemName("종점");
                                            startEndMarker[1].setTag(courseNum + 1);
                                            startEndMarker[1].setMapPoint(startEndPoint[1]);
                                            startEndMarker[1].setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                                            startEndMarker[1].setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

                                            mapView.addPOIItems(startEndMarker);
                                            mapView.moveCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(realtimeLoaction.getLatitude(), realtimeLoaction.getLongitude())));
                                            WaitNotify waitNotify = new WaitNotify();
                                            compareLocationAndAlarmTask = new CompareLocationAndAlarmTask(waitNotify);
                                            compareLocationAndAlarmTask.execute();
                                            Toast.makeText(context, "안전모드를 시작합니다.", Toast.LENGTH_SHORT).show();
                                            btnCheck = 1;
                                        }

                                    }).setNegativeButton("아니오",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // 'No'
                                            if (realtimeLoaction != null) {
                                                realtimeLoaction.stopUsingGPS();
                                                realtimeLoaction = null;
                                                //Toast.makeText(context, "백버튼으로 잘 종료됨", Toast.LENGTH_SHORT).show();
                                            }
                                            return;
                                        }
                                    });
                            AlertDialog alert = alert_confirm.create();
                            alert.show();
                            }
                        }
                        break;
                    case 1:
                        //health pause
                        healthPause();

                        //btnCheck=1 상태에서 눌렀을 때 일시정지 하면서    버튼 바뀜 btnCheck=1 -> 2
                        btnStart.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                        btnCheck = 2;
                        //safety 모드를 일시정지합니다. Toast
                        Toast.makeText(context, "안전모드를 일시정지 합니다.", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        //health restart
                        healthStart();

                        //btnCheck=2 상태에서 눌렀을 때 다시 시작하면서 버튼 바뀜 btnCheck=2 -> 1
                        btnStart.setImageResource(R.drawable.ic_pause_white_24dp);
                        //safety 모드를 다시시작합니다. Toast
                        compareLocationAndAlarmTask.mWaitNotify.mNotify();
                        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
                        Toast.makeText(context, "안전모드를 다시 시작합니다.", Toast.LENGTH_SHORT).show();
                        btnCheck = 1;
                        break;
                    default:
                        break;

                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //safety 기능 멈춤 btnCheck =1or2 -> 0
                //safety 기능을 멈추겠냐고 물어본 후 종료.
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MtSafetyActivity.this);
                alert_confirm.setMessage("안전모드를 종료하시겠습니까?").setCancelable(false).setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'YES'
                                //health stop
                                healthStop(btnCheck);

                                btnStop.setVisibility(View.GONE);
                                btnStart.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                                btnCheck = 0;

                                mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(coursePointBounds, 100));
                                mapView.removePOIItems(startEndMarker);
                                mapView.setShowCurrentLocationMarker(false);
                                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                                if(realtimeLoaction != null) {
                                    realtimeLoaction.stopUsingGPS();
                                    realtimeLoaction = null;
                                    //Toast.makeText(context, "스탑 버튼으로 잘 종료됨", Toast.LENGTH_SHORT).show();
                                }

                                if(compareLocationAndAlarmTask != null) {
                                    compareLocationAndAlarmTask.cancel(true);
                                    compareLocationAndAlarmTask = null;
                                }
                                topBarsettingRelease();
                                Toast.makeText(getApplicationContext(), "안전모드를 종료합니다.", Toast.LENGTH_SHORT).show();
                                health.setVisibility(View.INVISIBLE);
                            }
                        }).setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();


            }
        });
    }//End onCreate() Method.

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mapView.setShowCurrentLocationMarker(false);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mapViewContainer.removeAllViews();
        if(realtimeLoaction != null) {
            realtimeLoaction.stopUsingGPS();
            realtimeLoaction = null;
            //Toast.makeText(context, "백버튼으로 잘 종료됨", Toast.LENGTH_SHORT).show();
        }
        if(compareLocationAndAlarmTask != null) {
            compareLocationAndAlarmTask.cancel(true);
            compareLocationAndAlarmTask = null;
        }
        topBarsettingRelease();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    protected void onPause() {
        super.onPause();

    }

    public boolean safetyFunction(double myLati, double myLongi, int numberOfCourse){

        int count = 0;
        boolean check = false;
        //위도 : 1도에 110979.309m 경도 : 1도에 88907.949m
        for (int i = 0; i < numberOfCourse; i++) {
            //마지막에 있는 메소드로 바꿔도 됨 매개변수 바꿔서
            double rsltDistance = getRealDistance( Double.parseDouble(rsltLatitude[i]) , Double.parseDouble(rsltLongitude[i]), myLati, myLongi);

            if (rsltDistance > 30) { //내 거리가 25m 코스 좌표에서 25m 이상일때 count를 1씩 증가
                count++;
            }
        }
        if (count == numberOfCourse) {
            check = true;
            //count == lay.length 이라는 것은 모든 코스에서 25m 이상의 거리를 가지고 있다는
            //것이기 때문에 warningMS을 호출하여 메세지 전송
            //Log.e("체크 : ", check + "");
            return check;
        }else {
            //Log.e("체크 : ", check+"");
            return check;
        }
    }//End safetyFunction() Method.

    private class CompareLocationAndAlarmTask extends AsyncTask<Void, Void, Void>{
        private WaitNotify mWaitNotify = null;
        public CompareLocationAndAlarmTask(WaitNotify waitNotify){
            mWaitNotify = waitNotify;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(context, "안전모드를 시작합니다.", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            //Toast.makeText(getApplicationContext(), "안전모드를 종료합니다.", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... a) {
            //AsyncTask<o,x,x>쓰레드 작업
            //중간 중간 진행 상태를 UI에 업데이트 하도록 하려면 publishPorgress()메소드 호출
            Long startTime = System.currentTimeMillis();//현재 시간 받아옴
            Long nextTime = startTime+added; // 다음 시간(5초 뒤) 45초 뒤는 45000으로 바꿈

            while(true){
                if(btnCheck==2) {
                    mWaitNotify.mWait();
                }
                if(isCancelled()){
                    break;
                }
                //safetyCheck = safetyFunction(currentLatitude, currentLongitude, numberOfCourse);
                safetyCheck = safetyFunction(currentLatitude, currentLongitude, numberOfCourse);
                //Double.parseDouble(rsltLatitude[0]), Double.parseDouble(rsltLongitude[0])
                //Log.e("현재 좌표 체크",currentLatitude+", "+currentLongitude);
                Long updateTime = System.currentTimeMillis();//계속해서 현재 시간 받아옴
                //전 좌표와 현재 좌표가 다를 경우 지도 중앙 옮김(마커 위치에 따라 움직이기 위함)
                /*if(realtimeLoaction.beforeLati != currentLatitude || realtimeLoaction.beforeLongi != currentLongitude){
                    publishProgress(0);
                }*/
                //Log.e("업데이트 시간 체크", updateTime+"");
                if(safetyCheck&&(nextTime<=updateTime)) {//계속 받는 시간이 nextTime을 지날 때 & true일때
                    publishProgress();
                    nextTime = updateTime+added; //다음 알림 시간을 x초뒤로 바꿈
                }

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... a) {
            super.onProgressUpdate();
            //AsyncTask<x,o,x>작업 과정 중 경과 표시(publishProgress()메소드가 호출 될 때 마다 자동 호출 됨)
            //진동 코드 여기에 작성

            //vibrator.vibrate(1000);
            Toast.makeText(getApplicationContext(), "코스 이탈", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Void a) {
            //AsyncTask<x,x,o>doInBackground에서 나온 결과값 사용 메소드
        }
    }
    //안전모드 일시 정지, 다시 시작(쓰레드 멈춤)에 필요함. 코드 깨끗하게 보이기 위해 이너 클래스 만듬
    private class WaitNotify{
        synchronized public void mWait(){
            try{ wait(); }
            catch(Exception e){ Log.i("Debug", e.getMessage()); }
        }
        synchronized public void mNotify(){
            try { notify(); }
            catch(Exception e){ Log.i("Debug", e.getMessage()); }
        }
    }

    private class CourseDataAdapter extends ArrayAdapter<CourseData> {
        // 레이아웃 XML을 읽어들이기 위한 객체
        private LayoutInflater mInflater;

        public CourseDataAdapter(Context context, ArrayList<CourseData> object) {

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
                view = mInflater.inflate(R.layout.home_safety_course_list, null);
            } else {

                view = v;
            }

            // 자료를 받는다.
            final CourseData data = this.getItem(position);

            if (data != null) {
                // 화면 출력
                TextView courseNum = (TextView) view.findViewById(R.id.courseNum);
                TextView sec_len = (TextView) view.findViewById(R.id.sec_len);
                TextView up_min = (TextView) view.findViewById(R.id.up_min);
                TextView down_min = (TextView) view.findViewById(R.id.down_min);
                TextView cat_nam = (TextView) view.findViewById(R.id.cat_nam);
                courseNum.setText(data.getmCourseNum());
                if (Double.parseDouble(data.getmSec_len()) >= 1000){
                    double value = Double.parseDouble(data.getmSec_len());
                    if(value >= 10000){
                        long rsltLen = Math.round(Double.parseDouble(data.getmSec_len()) * 0.001);

                        sec_len.setText("약 " + rsltLen + "km");
                    }else {
                        long rsltLen = Math.round(Double.parseDouble(data.getmSec_len()) * 0.1);
                        BigDecimal bd = new BigDecimal(String.valueOf(rsltLen));
                        BigDecimal bd2 = new BigDecimal(String.valueOf(0.01));
                        sec_len.setText("약 " + bd.multiply(bd2) + "km");
                    }
                }else{
                    sec_len.setText(data.getmSec_len()+"m");
                }
                up_min.setText(data.getmUp_min()+"분");
                down_min.setText(data.getmDown_min()+"분");
                cat_nam.setText(data.getmCat_nam());
                courseNum.setTextColor(Color.WHITE);
                sec_len.setTextColor(Color.WHITE);
                up_min.setTextColor(Color.WHITE);
                down_min.setTextColor(Color.WHITE);
                cat_nam.setTextColor(Color.WHITE);


            }
            return view;
        }
    }

    // CData안에 받은 값을 직접 할당
    private class CourseData {
        private Context context;
        private String mNum, mCourseNum;
        private String mSec_len, mUp_min, mDown_min, mCat_nam;
        private String mLatitude, mLongitude;

        public CourseData(Context context,  String mNum, String mCourseNum, String mLatitude, String mLongitude,
                          String mSec_len, String mUp_min, String mDown_min, String mCat_nam) {

            this.context = context;
            this.mNum = mNum;
            this.mCourseNum = mCourseNum;
            this.mLatitude = mLatitude;
            this.mLongitude = mLongitude;
            this.mSec_len = mSec_len;
            this.mUp_min = mUp_min;
            this.mDown_min = mDown_min;
            this.mCat_nam = mCat_nam;


        }

        public String getmNum() {
            return mNum;
        }

        public String getmCourseNum() {
            return mCourseNum;
        }

        public String getmSec_len() {
            return mSec_len;
        }

        public String getmUp_min() {
            return mUp_min;
        }

        public String getmDown_min() {
            return mDown_min;
        }

        public String getmCat_nam() {
            return mCat_nam;
        }

        public String getmLatitude() {
            return mLatitude;
        }

        public String getmLongitude() {
            return mLongitude;
        }
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

    //실제 거리 계산하는 메소드
    private double getRealDistance(double checkLati, double checkLongi, double checkLati2, double checkLongi2){
        double distanceLati = checkLati - checkLati2;
        double distanceLongi = checkLongi - checkLongi2;
        double latiToMeter = 110979.309 * Math.abs(distanceLati); //1도당 110979.309m, 1도 x 차의절대값
        double longiToMeter = 88907.949 * Math.abs(distanceLongi); //위와 같음
        double rsltDistance = Math.sqrt((latiToMeter * latiToMeter) + (longiToMeter * longiToMeter)); // 피타고라스

        return rsltDistance;
    }


    //상단바에 고정
    private synchronized  void topBarSetting(){

        Intent intentMain = new Intent(Intent.ACTION_MAIN);
        intentMain.addCategory(Intent.CATEGORY_LAUNCHER);
        intentMain.setComponent((new ComponentName(context, MtSafetyActivity.class)));
        PendingIntent content = PendingIntent.getActivity(context, 0, intentMain, PendingIntent.FLAG_UPDATE_CURRENT);

        //Intent intent = new Intent(this, MtSafetyActivity.class);
        // intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        //PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notiBuilder = new Notification.Builder(this);
        // 작은 아이콘 이미지.
        notiBuilder.setSmallIcon(R.drawable.mom_notify_icon);
        notiBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.mom_icon));
        // 알림 제목.
        notiBuilder.setContentTitle("안전모드 서비스 동작 중");
        // 알림 내용.
        notiBuilder.setContentText("안전모드 화면으로 이동하려면 누르세요.");
        // 알림 터치시 반응.
        notiBuilder.setContentIntent(content);
        // 알림 터치시 반응 후 알림 삭제 여부.
        notiBuilder.setAutoCancel(false);
        // 우선순위.
        notiBuilder.setPriority(Notification.PRIORITY_MAX);
        Notification notification = notiBuilder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        // 고유ID로 알림을 생성.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

    }

    //상단바에 고정 해제
    public synchronized void topBarsettingRelease(){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager != null){
            notificationManager.cancel(0);
            notificationManager = null;
        }
    }

    //우측 드로우어에 띄우기 위해 사용되는 이너클래스////////////////////////////////////////////
    private class ImageAdapter extends BaseAdapter {
        private Context context;
        private int cellLayout;
        private LayoutInflater mInflater;
        private ArrayList<ImageItem> mImageItemList;

        public ImageAdapter(Context c, int cellLayout, ArrayList<ImageItem> imageItemList) {
            context = c;
            this.cellLayout = cellLayout;
            mImageItemList = imageItemList;
            mInflater = LayoutInflater.from(c);

        }

        @Override
        public int getCount() {
            return mImageItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (view == null) {
                view = mInflater.inflate(cellLayout, parent, false);
            } else {
                view = convertView;
            }

            //final ImageItem imageItem = this.getItem(position);

            holder = new ViewHolder();
            holder.checkbox = (CheckBox) view.findViewById(R.id.itemCheckBox);
            holder.imageview = (ImageView) view.findViewById(R.id.photoView);
            final int num = position;

            holder.checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(arrBitimg.get(num).getM_Selection()){
                        mapView.removePOIItem(arrBitimg.get(num).getLikeMarker());
                        arrBitimg.get(num).setM_selection(false);
                    }else{
                        mapView.addPOIItem(arrBitimg.get(num).getLikeMarker());
                        arrBitimg.get(num).setM_selection(true);
                    }
                }
            });
            if(arrBitimg.get(num).getM_Selection())
                holder.checkbox.setChecked(true);
            else
                holder.checkbox.setChecked(false);
            view.setTag(holder);

            //final ImageItem item = this.getItem(position);

            //holder = (ViewHolder) v.getTag();
            holder.imageview.setImageBitmap(arrBitimg.get(position).getM_img());
            //Glide.with(context).load(arrBitimg.get(position).getPic()).into(holder.imageview);
            holder.checkbox.setTag(position);
            //holder.checkbox.setChecked(false);

            return view;
        }
    }

    protected class ViewHolder {
        protected ImageView imageview;
        protected CheckBox checkbox;
        protected RelativeLayout btnGridView;
    }
    protected class ImageItem {
        private boolean m_selection;
        private Bitmap m_img;
        private double m_Lat;
        private double m_Lon;
        private MapPOIItem likeMarker;
        private byte[] pic;
        /*public ImageItem(Context context, Bitmap img, double lat, double lon, boolean selection, byte[] pic){
            this.m_img = img;
            this.m_Lat = lat;
            this.m_Lon = lon;
            this.m_selection = selection;
            this.pic = pic;
        }*/
        //public boolean getM_selection(){return m_selection;}

        public void setM_selection(boolean m_selection) {
            this.m_selection = m_selection;
        }

        public void setM_img(Bitmap m_img) {
            this.m_img = m_img;
        }

        public void setLikeMarker(MapPOIItem likeMarker) {
            this.likeMarker = likeMarker;
        }

        public MapPOIItem getLikeMarker() {
            return likeMarker;
        }

        public void setM_Lat(double m_Lat) {
            this.m_Lat = m_Lat;
        }

        public void setM_Lon(double m_Lon) {
            this.m_Lon = m_Lon;
        }

        public void setPic(byte[] pic) {
            this.pic = pic;
        }

        public Bitmap getM_img(){return m_img;}
        public double getM_Lat(){return m_Lat;}
        public double getM_Lon(){return m_Lon;}
        public boolean getM_Selection(){return m_selection;}
        public byte[] getPic() {
            return pic;
        }
    }

    private byte[] bitmapToByte(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}
