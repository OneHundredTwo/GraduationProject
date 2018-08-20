package com.cookandroid.mom.community;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cookandroid.mom.community.club.CrewFragment2;

import com.cookandroid.mom.R;
import com.cookandroid.mom.util.Util;


public class CommunityFragment extends Fragment implements View.OnClickListener{
    private String TAG = Util.tagHeader+"CommunityFragment";

    private Fragment fg;

    private TimelineFragment1 timeline = TimelineFragment1.newInstance();
    private CrewFragment2 crew = CrewFragment2.newInstance();

    private FragmentManager fm;

    public CommunityFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.community_fragment, container, false);

        fg = timeline;
        fm = getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        /* R.id.container(activity_main.xml)에 띄우겠다.
         첫번째로 보여지는 Fragment는 firstFragment로 설정한다. */
        transaction.replace(R.id.child_thirdfragment_container, fg);
        /* Fragment의 변경사항을 반영시킨다. */
        transaction.commit();

        Button btnTimeline = (Button)view.findViewById(R.id.btnTimeline);
        Button btnCrew = (Button)view.findViewById(R.id.btnCrew);
        btnTimeline.setOnClickListener(this);
        btnCrew.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnTimeline:
                fg = timeline;
                setChildFragment(fg);
                break;
            case R.id.btnCrew:
                fg = crew;
                setChildFragment(fg);
                break;
        }
    }

    private void setChildFragment(Fragment child) {
        FragmentTransaction childFt = fm.beginTransaction();

        if (!child.isAdded()) {
            childFt.replace(R.id.child_thirdfragment_container, child);
            childFt.addToBackStack(null);
            childFt.commit();
        }
    }
}
