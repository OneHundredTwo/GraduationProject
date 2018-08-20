package com.cookandroid.mom.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.mom.R;
import com.cookandroid.mom.community.club.InsertClubActivity;
import com.cookandroid.mom.util.MountainManager.TodayMountain;
import com.cookandroid.mom.util.MountainManager.TodayMtList;
import com.cookandroid.mom.util.UsingCameraFragment;
import com.cookandroid.mom.util.Util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Shining on 2017-10-26.
 */

public class ManageTodayFragment1 extends UsingCameraFragment {
    ListView todayMtList;
    TextView status_list_today;
    FloatingActionButton btnAdd;

    ArrayList<TodayMountain> dataList;
    TodayMtList dataAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_manage_today, container,false);


        todayMtList = (ListView)view.findViewById(R.id.list_today);
        status_list_today = (TextView)view.findViewById(R.id.status_list_today);
        btnAdd = (FloatingActionButton)view.findViewById(R.id.btnAdd);

        dataList = Util.mtManager.getTodayHistory();

        dataAdapter = new TodayMtList(getContext(), dataList);

        todayMtList.setAdapter(dataAdapter);

        if(dataAdapter.isEmpty()){
            status_list_today.setVisibility(View.VISIBLE);
        }else{
            status_list_today.setVisibility(View.GONE);
        }

        btnAdd.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    TodayMountain.showTodayMtUpdateDialog(ManageTodayFragment1.this, getContext(), null);
                }
        });

        return view;
    }

    public void refresh(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(ManageTodayFragment1.this).attach(ManageTodayFragment1.this).commit();
            }
        }, 1000);
    }


}
