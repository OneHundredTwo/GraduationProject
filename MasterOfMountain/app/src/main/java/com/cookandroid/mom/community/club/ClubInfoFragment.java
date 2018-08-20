package com.cookandroid.mom.community.club;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cookandroid.mom.R;
import com.cookandroid.mom.util.User;

/**
 * Created by wdj46 on 2017-08-10.
 */

public class ClubInfoFragment extends Fragment {
    private Fragment fg;
    private TextView clubInfoname, clubInfoID, clubInfoContent,clubInfoJoin,clubInfoArea;
    private User.Club club;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.community_clubinfo_frag, container, false);

        club = ClubDetailActivity.club;
        clubInfoname = (TextView) view.findViewById(R.id.clubInfoName);
        clubInfoID = (TextView)view.findViewById(R.id.clubInfoID);
        clubInfoContent = (TextView)view.findViewById(R.id.clubInfoContent);
        clubInfoJoin = (TextView)view.findViewById(R.id.clubInfoJoin);
        clubInfoArea = (TextView)view.findViewById(R.id.clubInfoArea);

        clubInfoname.setText("동호회 이름 : "+club.clubNm);
        clubInfoID.setText("개설자 : "+club.clubID);
        clubInfoContent.setText("상세 내용 : "+club.clubContent);
        clubInfoJoin.setText("참여인원 : "+club.clubJoin);
        return view;
    }


}
