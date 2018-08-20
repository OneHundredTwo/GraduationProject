package com.cookandroid.mom.home;

import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cookandroid.mom.R;
import com.cookandroid.mom.util.Community;
import com.cookandroid.mom.util.Util;

import java.util.ArrayList;

/**
 * Created by Shining on 2017-10-26.
 */

public class ManageNoticeFragment2 extends Fragment{
    ListView noticeList;
    FloatingActionButton noticeAdd;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_manage_notice, container, false);

        ArrayList<Community.Notice> notices = Util.community.getNotices();

        noticeList = (ListView)view.findViewById(R.id.noticeList);
        noticeList.setAdapter(new Community.NoticeAdapter(getContext(), notices, new Community.NoticeClickListener() {
            @Override
            public void onClick(View view) {
                Community.Notice.showNoticeDialog(getContext(), view);
            }
        }));

        noticeAdd = (FloatingActionButton)view.findViewById(R.id.noticeAdd);
        noticeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Community.Notice.showNoticeDialog(getContext(), null);
            }
        });




        return view;
    }

    public void refresh(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(ManageNoticeFragment2.this).attach(ManageNoticeFragment2.this).commit();
            }
        }, 1000);
    }
}
