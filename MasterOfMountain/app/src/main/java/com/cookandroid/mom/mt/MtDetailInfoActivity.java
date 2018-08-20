package com.cookandroid.mom.mt;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.cookandroid.mom.R;
import com.cookandroid.mom.util.MountainManager;
import com.cookandroid.mom.util.Util;

public class MtDetailInfoActivity extends FragmentActivity {
    private ViewPager mountain_vp;
    private TextView tab_MTInfo, tab_MTCoures, tab_MTEm;
    //static String mtName;
    private View mountain_ll;
    private FrameLayout layout;
    private ImageView mountainPic;
    private TextView mountainName, mountainHeight;
    static String mtName,mNum;
    static MountainManager.Mountain mountain;
    private PagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mt_detail_activity);

        Intent intent = getIntent();
        mtName = intent.getStringExtra("mtName");
        mNum = intent.getStringExtra("mtCode");
        mountain = Util.mtManager.getMtDetail(mtName);
        layout = (FrameLayout)findViewById(R.id.mtdetailLayout);
        mountainName = (TextView) findViewById(R.id.mountain_name);
        mountainHeight = (TextView) findViewById(R.id.mountain_height);
        mountainName.setText(mountain.mtName);
        mountainName.setTextColor(Color.WHITE);
        mountainHeight.setText(mountain.mtHeight + "m");
        mountainHeight.setTextColor(Color.WHITE);

        try {
            mountainPic = (ImageView) findViewById(R.id.mtPic);
            mountainPic.setImageBitmap(Util.getIFS().execute(mountain.mtPhoto).get());
        }catch(Exception e){
            e.printStackTrace();
            /*나중에 이미지 로딩오류 메세지를 나타내는 이미지를 셋팅하자.*/
        }



        mountain_vp = (ViewPager)findViewById(R.id.mountain_vp);
        mountain_ll = findViewById(R.id.mountain_ll);


        tab_MTInfo = (TextView)findViewById(R.id.tab_MTInfo);
        tab_MTCoures = (TextView)findViewById(R.id.tab_MTCourse);
        tab_MTEm = (TextView)findViewById(R.id.tab_MTEm);
        pagerAdapter = new pagerAdapter(getSupportFragmentManager());
        mountain_vp.setAdapter(pagerAdapter);
        mountain_vp.setCurrentItem(0);

        tab_MTInfo.setOnClickListener(movePageListener);
        tab_MTInfo.setTag(0);
        tab_MTCoures.setOnClickListener(movePageListener);
        tab_MTCoures.setTag(1);
        tab_MTEm.setOnClickListener(movePageListener);
        tab_MTEm.setTag(2);

        tab_MTInfo.setSelected(true);

        //탭 색깔 바꾸기
        mountain_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int i =  0;
                while(i<3){
                    if(position==i)
                        mountain_ll.findViewWithTag(i).setSelected(true);
                    else
                        mountain_ll.findViewWithTag(i).setSelected(false);
                    i++;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    //탭 색깔 바꾸기
    View.OnClickListener movePageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag();

            int i = 0;
            while(i<3){
                if(tag==i)
                    mountain_ll.findViewWithTag(i).setSelected(true);
                else
                    mountain_ll.findViewWithTag(i).setSelected(false);
                i++;
            }

            mountain_vp.setCurrentItem(tag);
        }
    };
    private class pagerAdapter extends FragmentStatePagerAdapter
    {
        public pagerAdapter(android.support.v4.app.FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public Fragment getItem(int position)
        {
            switch(position)
            {
                case 0:
                    return new MtInfoFragment1();
                case 1:
                    return new MtCourseFragment2();
                case 2:
                    return new MtEmFragment3();
                default:
                    return null;
            }
        }

        //프래그먼트 새로고침하는 데에 필요
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount()
        {
            return 3;
        }
    }

    //프래그먼트 새로고침
    @Override
    protected void onResume() {
        super.onResume();
        pagerAdapter.notifyDataSetChanged();
    }

}
