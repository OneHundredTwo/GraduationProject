package com.cookandroid.mom.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cookandroid.mom.R;
import com.cookandroid.mom.util.Util;

/**
 * Created by Shining on 2017-10-26.
 */

public class ManageActivity extends FragmentActivity {

    ViewPager vp;
    TextView tagNotice, tagToday;
    LinearLayout manageLl;

    pagerAdapter pagerAdapter;

    public ManageTodayFragment1 fragment1 = new ManageTodayFragment1();
    public ManageNoticeFragment2 fragment2 = new ManageNoticeFragment2();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_management_activity);
        Util.manage = this;

        vp = (ViewPager)findViewById(R.id.manageVp);
        manageLl = (LinearLayout)findViewById(R.id.manageLl);
        tagNotice = (TextView)findViewById(R.id.tag_mngNotice);
        tagToday = (TextView)findViewById(R.id.tag_mngToday);

        pagerAdapter = new ManageActivity.pagerAdapter(getSupportFragmentManager());
        vp.setAdapter(pagerAdapter);
        vp.setCurrentItem(0);

        tagToday.setOnClickListener(movePageListener);
        tagToday.setTag(0);
        tagNotice.setOnClickListener(movePageListener);
        tagNotice.setTag(1);

        tagToday.setSelected(true);

        //ViewPager 페이지 체인지 리스너 추가
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int i =  0;
                while(i<2){
                    if(position==i)
                        manageLl.findViewWithTag(i).setSelected(true);
                    else
                        manageLl.findViewWithTag(i).setSelected(false);
                    i++;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

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
                    return fragment1;
                case 1:
                    return fragment2;
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
            return 2;
        }


    }



    //탭 색깔 바꾸기
    View.OnClickListener movePageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag();

            int i = 0;
            while(i<2){
                if(tag==i)
                    manageLl.findViewWithTag(i).setSelected(true);
                else
                    manageLl.findViewWithTag(i).setSelected(false);
                i++;
            }

            vp.setCurrentItem(tag);
        }
    };

}
