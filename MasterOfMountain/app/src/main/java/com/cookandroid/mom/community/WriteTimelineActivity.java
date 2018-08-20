package com.cookandroid.mom.community;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cookandroid.mom.R;
import com.cookandroid.mom.community.timeline.PhotoActivity;
import com.cookandroid.mom.util.User;
import com.cookandroid.mom.util.Util;

import java.util.ArrayList;

/**
 * 타임라인 글쓰기 액티비티
 */
public class WriteTimelineActivity extends ActionBarActivity {
    /**
     * 퍼미션 체크
     */
    protected static boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }
    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.CAMERA",
                "android.permission.INTERNET"
        };
  /*  int requestCode = 200;
    requestPermissions(permissions, requestCode);*/
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this, permissions, 1);

        }
    }//Permission Check 메소드들 끝.

    private Toolbar toolbar;
    private ActionBar actionBar;
    private EditText timeLineContent;
    private TextView name, range;
    private RelativeLayout rl2, rl3;
    private ImageView rsltPic, btnClear;
    private String uri, fileName;
    private String currentLatiFromCamera; //카메라프래그먼트로부터 온 좌표가 있을 경우 저장 됨
    private String currentLongiFromCamera;
    private String currentLatiFromPhoto; //포토액티비티(픽쳐프래그먼트)로부터 온 좌표가 있을 경우 저장 됨
    private String currentLongiFromPhoto;
    private String currentLatitude; //서버에 추가시키기 위해 쓰레드로 보내주는 변수
    private String currentLongitude;
    private String nearMt; //서버에 저장 시키기위해 보내주는 가까운 산이름 변수
    private Spinner rangeSpinner, clubSpinner;
    private int clubNum=0;
    private String photo = null;

    private String[][] mtInfos; //산정보
    private String[] arrName;
    private String[] arrLatitude;
    private String[] arrLongitude;
    //액티비티 관리 위한 ArrayList
    public static ArrayList<Activity> actList = new ArrayList<Activity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_write_timeline_acti);
        //Permission 체크
        if (shouldAskPermissions()) {
            askPermissions();
        }
        String[] range = {"모두","동호회","비공개"};
        Log.e("ArrayList.actList(사이즈)", WriteTimelineActivity.actList.size()+"");

        name = (TextView)findViewById(R.id.name);
        rangeSpinner = (Spinner)findViewById(R.id.rangeSpinner);
        clubSpinner = (Spinner)findViewById(R.id.clubSpinner);


        rl2 = (RelativeLayout) findViewById(R.id.rl2);
        rl3 = (RelativeLayout) findViewById(R.id.rl3);
        rl3.setVisibility(View.GONE);
        rsltPic = (ImageView)findViewById((R.id.rsltPic));
        btnClear = (ImageView)findViewById(R.id.btnClear);
        //Intent intent=new Intent(this.getIntent());
        //이름 받아올 코드 작성

        name.setText("아이디 : "+ Util.user.getId());
        /*Intent checkToThis = getIntent();
        uri = checkToThis.getExtras().getString("uri");
        Log.e("uri확인", uri);*/

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.spin, range);
        adapter.setDropDownViewResource(R.layout.spin_dropdown);

        rangeSpinner.setAdapter(adapter);

        rangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Log.e("공개범위",""+rangeSpinner.getSelectedItemPosition());
                switch (position){
                    case 0:
                        String club[] = {"모두"};
                        ArrayAdapter adapter1 = new ArrayAdapter(getApplicationContext(), R.layout.spin, club);
                        adapter1.setDropDownViewResource(R.layout.spin_dropdown);

                        clubSpinner.setAdapter(adapter1);
                        break;
                    case 1:
                        try {
                            String[][] clubName = Util.user.getClub(0, Util.user.getId());
                            String[] club_Name = clubName[0];

                            ArrayAdapter adapter2= new ArrayAdapter(getApplicationContext(), R.layout.spin, club_Name);
                            adapter2.setDropDownViewResource(R.layout.spin_dropdown);

                            clubSpinner.setAdapter(adapter2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        String[] range = {"비공개"};
                        ArrayAdapter adapter2 = new ArrayAdapter(getApplicationContext(), R.layout.spin, range);
                        adapter2.setDropDownViewResource(R.layout.spin_dropdown);

                        clubSpinner.setAdapter(adapter2);
                        break;


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //사진 추가 버튼
        rl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "사진 올리기 버튼", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, 0);
            }
        });

        // X버튼 누르면 사진 추가 버튼 다시 나타내기
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uri = null;
                //Toast.makeText(getApplicationContext(), "uri 삭제 확인: "+uri, Toast.LENGTH_SHORT).show();
                Log.e("uri 삭제 확인", uri+"");
                rl3.setVisibility(View.GONE);
                rl2.setVisibility(View.VISIBLE);
            }
        });
        //작은 사진 클릭 시 크게 보기
        rsltPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dlg = new Dialog(WriteTimelineActivity.this);
                dlg.setContentView(R.layout.photo_popup_detail);
//                dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ImageView img = (ImageView)dlg.findViewById(R.id.img);
                Bitmap bm = BitmapFactory.decodeFile(uri);
                img.setImageBitmap(bm);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dlg.cancel();
                    }
                });
                dlg.show();
            }
        });
        timeLineContent = (EditText)findViewById(R.id.timelineContent);
        toolbar = (Toolbar) findViewById(R.id.timeline_toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("타임라인 글쓰기");
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       /*
        btnTimelineOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("WriteTimeline","request2");
                if (!timeLineContent.getText().toString().trim().equals("")) {
                    try {
                        String message = Util.community.insertTimeline(Util.user.getId(), timeLineContent.getText().toString());
                        Log.e("WriteTimeline","login request3");
                        Toast.makeText(WriteTimelineActivity.this, message, Toast.LENGTH_SHORT).show();
                        if (message.equals("글이 등록되었습니다.")) {
                            Intent intent = new Intent(WriteTimelineActivity.this, TimelineFragment1.class);
                            setResult(Activity.RESULT_OK,intent);
                            finish();
                        }
                        return;
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }else{
                    Toast.makeText(WriteTimelineActivity.this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                }

            }
        });*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.write_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                // 해당 버튼을 눌렀을 때 적절한 액션을 넣는다.
                finish();
                break;
            case R.id.btnOK:
                //타임라인 글쓰기 게시 버튼 클릭시(이 때 서버에 사진 저장 함)
                if(uri == null) {
                    //사진 없을 때
                    //Toast.makeText(this, "사진 없는 상태로 글만 올림", Toast.LENGTH_SHORT).show();
                    if (!timeLineContent.getText().toString().trim().equals("")) {
                        try {
                            if(rangeSpinner.getSelectedItem().toString().equals("동호회")){
                                User.Club club = Util.user.getClubDetail(clubSpinner.getSelectedItem().toString());
                                clubNum = club.clubNum;
                            }
                            String message = Util.community.insertTimeline(Util.user.getId(), timeLineContent.getText().toString(),rangeSpinner.getSelectedItem().toString(),photo,clubNum,null,null,null);
                            Log.e("WriteTimeline","login request3");
                            Toast.makeText(WriteTimelineActivity.this, message, Toast.LENGTH_SHORT).show();
                            if (message.equals("글이 등록되었습니다.")) {
                                Intent intent = new Intent(WriteTimelineActivity.this, TimelineFragment1.class);
                                setResult(Activity.RESULT_OK,intent);
                                finish();
                            }
                            break;
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                    }else{
                        Toast.makeText(WriteTimelineActivity.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    //사진 있을 때
                    //사진 서버로 전송하기 위해 변수 설정
                    if(currentLatiFromCamera!=null && currentLongiFromCamera!=null) {
                        //카메라프래그먼트로부터 넘어온 데이터
                        currentLatitude = currentLatiFromCamera;
                        currentLongitude = currentLongiFromCamera;
                        nearMt = getNearMtName(currentLatitude, currentLongitude);
                        Toast.makeText(this, "From카메라", Toast.LENGTH_SHORT).show();
                    }
                    else if(currentLatiFromPhoto!=null && currentLongiFromPhoto!=null) {
                        //포토 선택해서 넘어온 데이터
                        currentLatitude = currentLatiFromPhoto;
                        currentLongitude = currentLongiFromPhoto;
                        nearMt = getNearMtName(currentLatitude, currentLongitude);
                        Toast.makeText(this, "From포토", Toast.LENGTH_SHORT).show();
                    }else {
                        //사진에 좌표가 없을 경우
                        currentLatitude = null;
                        currentLongitude = null;
                        nearMt = null;
                        Toast.makeText(this, "좌표없음", Toast.LENGTH_SHORT).show();
                    }
                    /*GPS 위치 받아오고, 테이블에 사진과 매핑 후, ITS에 매개변수로 추가.*/
                    if (!timeLineContent.getText().toString().trim().equals("")) {
                        if(rangeSpinner.getSelectedItem().toString().equals("동호회")){
                            User.Club club = Util.user.getClubDetail(clubSpinner.getSelectedItem().toString());
                            clubNum = club.clubNum;
                        }

                        Util.getITS().execute(uri);
                        photo = Util.getITS().getImagePathToUri(uri);
                        Toast.makeText(this, "등록중..." , Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try {
                                    String message = Util.community.insertTimeline(Util.user.getId(), timeLineContent.getText().toString(),rangeSpinner.getSelectedItem().toString(),photo,clubNum,currentLatitude,currentLongitude, nearMt);
                                    Log.e("WriteTimeline","login request3");
                                    Toast.makeText(WriteTimelineActivity.this, message, Toast.LENGTH_SHORT).show();

                                    if (message.equals("글이 등록되었습니다.")) {
                                        Intent intent = new Intent(WriteTimelineActivity.this, TimelineFragment1.class);
                                        setResult(Activity.RESULT_OK,intent);
                                        finish();
                                    }
                                } catch (Exception e) {
                                    e.getStackTrace();
                                }
                            }
                        }, 3000);
                        break;

                    }else{
                        Toast.makeText(WriteTimelineActivity.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;

        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    uri = data.getStringExtra("uri");
                    fileName = data.getStringExtra("fileName");

                    //카메라프래그먼트로부터 넘어온 데이터
                    //if(data.getStringExtra("currentLatiFromCamera")!=null && data.getStringExtra("currentLongiFromCamera")!=null) {
                    currentLatiFromCamera = data.getStringExtra("currentLatiFromCamera");
                    currentLongiFromCamera = data.getStringExtra("currentLongiFromCamera");
                    //}
                    //포토 선택해서 넘어온 데이터
                    // if(data.getStringExtra("currentLatiFromPhoto")!=null && data.getStringExtra("currentLongiFromPhoto")!=null) {
                    currentLatiFromPhoto = data.getStringExtra("currentLatiFromPhoto");
                    currentLongiFromPhoto = data.getStringExtra("currentLongiFromPhoto");
                    //}
                    Log.e("--WriteTimelineActi--", "확인");
                    Log.e("좌표 확인 FromCamera", currentLatiFromCamera+","+currentLongiFromCamera);
                    Log.e("좌표 확인 FromPhoto", currentLatiFromPhoto+","+currentLongiFromPhoto);
                    Log.e("fileName확인", fileName);
                    Log.e("uri확인", uri);
                    //Toast.makeText(this, "uri확인"+uri, Toast.LENGTH_SHORT).show();
                    rl2.setVisibility(View.GONE);
                    Glide.with(this).load(uri).into(rsltPic);
                    // rsltPic.setImageURI(Uri.parse(uri));
                    rsltPic.setScaleType(ImageView.ScaleType.FIT_XY);
                    rl3.setVisibility(View.VISIBLE);
                    /* BitmapFactory.Options op = new BitmapFactory.Options();
                     op.inJustDecodeBounds = true;
                     BitmapFactory.decodeFile(uri,op);
                     Toast.makeText(getApplicationContext(),"사진 사이즈/ 가로:"+op.outWidth+"세로:"+op.outHeight, Toast.LENGTH_SHORT).show();
*/
                }
                break;
        }
    }

    private String getNearMtName(String currentLatitude, String currentLongitude){
        double nearDistance = 1000000000;
        int nearMtIndex=0;

        try{ //User와 산의 거리 비교를 위해 받아옴
            mtInfos = Util.mtManager.getMtPoint();
            arrName = mtInfos[0];
            arrLatitude = mtInfos[1];
            arrLongitude = mtInfos[2];

            for (int i = 0; i < arrName.length; i++) {
                //위도 : 1도에 110979.309m 경도 : 1도에 88907.949m
                double distanceLati = Double.parseDouble(arrLatitude[i]) - Double.parseDouble(currentLatitude); //(위도) 산위치 - 내위치
                double distanceLongi = Double.parseDouble(arrLongitude[i]) - Double.parseDouble(currentLongitude); //(경도) 산위치 - 내위치
                double latiToMeter = 110979.309 * Math.abs(distanceLati);  // 1도에 110979.309m 이기에 차이값(절대값)을 통해 곱함
                double longiToMeter = 88907.949 * Math.abs(distanceLongi);  // 마찬가지
                double rsltDistance = Math.sqrt((latiToMeter * latiToMeter) + (longiToMeter * longiToMeter)); //삼각함수로 x^2 + y^2의 루트값이 대각선 거리

                if(nearDistance > rsltDistance) {
                    nearDistance = rsltDistance;
                    nearMtIndex = i;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrName[nearMtIndex];
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e("ArrayList.actList(사이즈)", WriteTimelineActivity.actList.size()+"");
    }
}