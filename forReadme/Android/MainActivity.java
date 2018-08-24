package com.cookandroid.mom;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.mom.community.CommunityFragment;
import com.cookandroid.mom.home.HomeFragment;
import com.cookandroid.mom.home.LoginActivity;
import com.cookandroid.mom.mt.MtFragment;
import com.cookandroid.mom.my.MyFragment;
import com.cookandroid.mom.util.Community;
import com.cookandroid.mom.util.Util;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;


public class MainActivity extends FragmentActivity {
    private final String TAG = Util.tagHeader+"MainActivity";

    //ViewPager에서 다음에 보여줄거라 기대하는 한쪽방향의 페이지 수.
    private int pagelimit = 4;

    private ViewPager vp;
    private PagerAdapter pagerAdapter;
    private TextView tab_first, tab_second, tab_third, tab_fourth;
    private View ll;
    private BackPressCloseHandler backPressCloseHandler;
    private Button btnLogin; // 로그인버튼, 언제든 바뀔 수 있음.


    public  HomeFragment homeFragment = new HomeFragment();
    public  MtFragment mtFragment = new MtFragment();
    public  CommunityFragment communityFragment = new CommunityFragment();
    public  MyFragment myFragment = new MyFragment();
    public static boolean beforePage;

    private class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return homeFragment;
                case 1:
                    return mtFragment;
                case 2:
                    return communityFragment;
                case 3:
                    return myFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }



    //탭 색깔 바꾸기
    View.OnClickListener movePageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int TAG = (int) v.getTag();

            int i = 0;
            while (i < 4) {
                if (TAG == i)
                    ll.findViewWithTag(i).setSelected(true);
                else
                    ll.findViewWithTag(i).setSelected(false);
                i++;
            }

            vp.setCurrentItem(TAG, true);
            //빠르게 움직이면 발생하는 익셉션 --최대이슈--
            // java.lang.NullPointerException: Attempt to read from field 'int android.support.v4.app.Fragment.mContainerId' on a null object reference
            //웹서치중 유사한 증상에 대한 페이지 구글 검색어 :  fast move viewpager setCurrentItem crash
            //https://stackoverflow.com/questions/23616945/swipe-back-from-a-viewpager-causes-the-app-to-crash

            //==> limit을 4개로 늘리니까 해결.
        }
    };

    //뒤로가기 2번 누르면 종료시킬 때 필요한 메소드
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    //현재 선택된 Fragment를 반환하는 방법을 알아내고, 새로고침
        /*
        * 1. MainActivity 상의 로그인버튼이 눌렸을때 현재 화면에 표시된 Fragment를 찾는다.
        * -> 로그인버튼 클릭이벤트 리스너 적용.
        * ->refreshFragment() 정의
        * 2.로그인일때 -> login액티비티를 intent의 startActivityForResert()로 호출 -> onActivityResult()메소드에 refreshFragment()호출
        *  로그아웃일때 -> logoutclick 이벤트리스너에 refreshFragment()호출
        * */

    void refreshFragment() {
        // ViewPager 혹은 FragmentStatePagerAdapter에서 찾아보자

        // 아예 Adapter를 새로 만들어서 생성한다음에 setCurrentItem을 해볼까 => 된다, 초기값이 0이라 로그인하면 0번 아이템으로 이동하긴한데
        // 아래와같이 더러운방법으로 하면 될것 같다.
        int prevPosition = vp.getCurrentItem();

        //구 데이터
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        //갱신할 데이터.
        vp.setAdapter(pagerAdapter);
        if(prevPosition == 3) {
            vp.setCurrentItem(0);
            ll.findViewWithTag(0).setSelected(true);
            ll.findViewWithTag(prevPosition).setSelected(false);
        }else{
            vp.setCurrentItem(prevPosition);
        }

        Log.e(TAG, "refreshFragment done");

    }

    //로그인/로그아웃 버튼을 눌렀을때 수행될 이벤트 정의
    View.OnClickListener loginEvent = new View.OnClickListener() {
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, 0);

        }
    };

    View.OnClickListener logoutEvent = new View.OnClickListener() {
        public void onClick(View view) {
            if(Util.user.logout()){
                Log.e(TAG, "logout_state receive");
                Toast.makeText(MainActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                btnLogin.setOnClickListener(loginEvent);
                btnLogin.setText("login");
            }else{
                /* 로그아웃 안됐을때 */
                Toast.makeText(MainActivity.this, "로그아웃에 실패하였습니다. 잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
            }

            refreshFragment();

        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("onActivityResult ", "응답 인텐트 받았다");
        //로그인 액티비티로 넘어간 후에 로그인에 성공한 경우(인텐트로 응답메세지를 받았을때)
        //수행할 ui작업 목록
        /*
        1.login 버튼 텍스트 변경
        2.login 버튼 클릭 이벤트 변경(로그아웃으로)
        3.내가쓴글 리스트 출력 => fragment 새로고침으로 됐다.
        * */
        if (resultCode == Activity.RESULT_OK) {
            Log.e("onActivityResult ", "로그아웃으로 바꾼다");
            //btnLogin.setText("로그아웃"); 이미지를 바꾸던가 등등
            btnLogin.setOnClickListener(logoutEvent);
            btnLogin.setText("logout");
            refreshFragment();
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Util.main = this;

        Log.e(TAG, "onCreate");

        backPressCloseHandler = new BackPressCloseHandler(this);
        beforePage = false;

        vp = (ViewPager)findViewById(R.id.vp);
        ll = findViewById(R.id.ll);

        tab_first = (TextView)findViewById(R.id.tab_first);
        tab_second = (TextView) findViewById(R.id.tab_second);
        tab_third = (TextView) findViewById(R.id.tab_third);
        tab_fourth = (TextView) findViewById(R.id.tab_fourth);

        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        //ViewPager - Adapter 연결
        vp.setAdapter(pagerAdapter);
        vp.setOffscreenPageLimit(pagelimit);
        //앱 실행됐을 때 첫번째 페이지로 초기화
        vp.setCurrentItem(0);


        tab_first.setOnClickListener(movePageListener);
        tab_first.setTag(0);
        tab_second.setOnClickListener(movePageListener);
        tab_second.setTag(1);
        tab_third.setOnClickListener(movePageListener);
        tab_third.setTag(2);
        tab_fourth.setOnClickListener(movePageListener);
        tab_fourth.setTag(3);

        tab_first.setSelected(true);

        //탭 색깔 바꾸기
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int i =  0;
                while(i<4){
                    if(position==i) {
                        ll.findViewWithTag(i).setSelected(true);
                        if(position == 3){
                            if(Util.user.getId()==null){
                                beforePage = true;
                                Toast.makeText(getApplicationContext(), "비회원은 접근할수 없습니다. 로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivityForResult(intent, 0);
                            }
                        }
                    }else {
                        ll.findViewWithTag(i).setSelected(false);
                    }
                    i++;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //로그인버튼 할당 및 이벤트연결.
        btnLogin = (Button)findViewById(R.id.btnLogin);
        //초기이벤트 -> 로그인
        if(Util.user.getId() == null){
            btnLogin.setOnClickListener(loginEvent);
            btnLogin.setText("login");
            btnLogin.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10f);
        }else{
            btnLogin.setOnClickListener(logoutEvent);
            btnLogin.setText("logout");
            btnLogin.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10f);
        }


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(beforePage == true){
            vp.setCurrentItem(0);
            beforePage = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager != null){
            notificationManager.cancel(0);
            notificationManager = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        refreshFragment();
    }




}

