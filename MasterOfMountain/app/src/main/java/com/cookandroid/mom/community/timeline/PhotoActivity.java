package com.cookandroid.mom.community.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.mom.R;
import com.cookandroid.mom.community.WriteTimelineActivity;
import com.cookandroid.mom.util.MoMDBConnection;

import java.util.StringTokenizer;

public class PhotoActivity extends AppCompatActivity {
    private ViewPager vp;
    private PagerAdapter pagerAdapter;
    private TextView tab_first, tab_second;
    private View ll;

    private PictureFragment pictureFragment = new PictureFragment();
    private CameraFragment cameraFragment = CameraFragment.newInstance(this);
    private Toolbar toolbar;

    private class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return pictureFragment;
                case 1:
                    return cameraFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return 2;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_activity);

        WriteTimelineActivity.actList.add(this);
        Log.e("ArrayList.actList(사이즈)", WriteTimelineActivity.actList.size()+"");
        try {

            vp = (ViewPager) findViewById(R.id.vp);
            ll = findViewById(R.id.ll);

            tab_first = (TextView) findViewById(R.id.btnTimeline);
            tab_second = (TextView) findViewById(R.id.btnCrew);


            pagerAdapter = new PagerAdapter(getSupportFragmentManager());
            //ViewPager - Adapter 연결
            vp.setAdapter(pagerAdapter);

            //앱 실행됐을 때 첫번째 페이지로 초기화
            vp.setCurrentItem(0);

            tab_first.setOnClickListener(movePageListener);
            tab_first.setTag(0);
            tab_second.setOnClickListener(movePageListener);
            tab_second.setTag(1);

            tab_first.setSelected(true);

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("갤러리");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            //탭 색깔 바꾸기
            vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    int i = 0;
                    while (i < 2) {
                        if (position == i) {
                            ll.findViewWithTag(i).setSelected(true);
                            if (position == 0) {
                                toolbar.setTitle("갤러리");
                                toolbar.getMenu().getItem(0).setVisible(true);
                            } else {
                                toolbar.setTitle("카메라");
                                toolbar.getMenu().getItem(0).setVisible(false);
                            }
                            Log.e("현재 페이지", vp.getCurrentItem() + "");
                        } else {
                            ll.findViewWithTag(i).setSelected(false);
                        }
                        i++;
                    }

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }

    }


    //탭 색깔 바꾸기
    View.OnClickListener movePageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag();
            int i = 0;
            while (i < 2) {
                if (tag == i)
                    ll.findViewWithTag(i).setSelected(true);
                else
                    ll.findViewWithTag(i).setSelected(false);
                i++;
            }

            vp.setCurrentItem(tag);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("ArrayList.actList(사이즈)", WriteTimelineActivity.actList.size()+"");
        pictureFragment.strUri = null;
        pagerAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                //뒤로가기 버튼 누르면 ArrayList에서 액티비티 삭제
                WriteTimelineActivity.actList.remove(0);
                finish();
                return true;
            case R.id.btnNext:
                if(pictureFragment.strUri==null) {
                    Toast.makeText(getApplicationContext(), "사진을 선택하세요.", Toast.LENGTH_SHORT).show();
                }else{
                    //사진 목록에서 SQLite에 사진이 존재할 경우 그 사진을 좌표와 함께 CheckActivity로 넘김
                    MoMDBConnection momDB = new MoMDBConnection(getApplicationContext());
                    String fileName = pictureFragment.strUri.substring(pictureFragment.strUri.lastIndexOf("/")+1);
                    String latAndLon = momDB.getPicInfo(fileName);
                    Intent intent = new Intent(this, CheckActivity.class);
                    intent.putExtra("uri", pictureFragment.strUri);
                    if(!latAndLon.equals("0,0")){
                        String[] currentLocation = tokenizingCoordi(latAndLon);
                        Log.e("--PhotoActivity--","");
                        Log.e("SQLite 확인", latAndLon);
                        Log.e("fileName확인", fileName);
                        Log.e("좌표 확인 FromPhoto", currentLocation[0]+","+currentLocation[1]);
                        intent.putExtra("currentLatiFromPhoto", currentLocation[0]);
                        intent.putExtra("currentLongiFromPhoto", currentLocation[1]);
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    startActivity(intent);
                    //finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
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
    public void onBackPressed() {
        //하드웨어 백버튼 누르면 ArrayList에서 액티비티 삭제
        WriteTimelineActivity.actList.remove(0);
        finish();
    }
}
