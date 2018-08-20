package com.cookandroid.mom.community.club;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cookandroid.mom.R;
import com.cookandroid.mom.util.Util;

import java.util.ArrayList;

/**
 * Created by wdj46 on 2017-08-17.
 */

public class ClubManagementActivity extends ActionBarActivity {
    final Context context = this;
    private Bitmap[] clubIcon;
    private ListView listview;
    private ArrayList<CData> alist;
    private DataAdapter adapter;
    private String[][] clubInfo;
    private String[] clubNm;
    private String[] clubID;
    private String[] clubContent;
    private String[] clubDate;
    private String[] clubjoin;
    private String[] clubPhoto;
    private String[] clubNum;
    private String[] crew;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_club);
        Toolbar toolbar = (Toolbar) findViewById(R.id.club_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("동호회 관리");
        listview = (ListView) findViewById(R.id.listView);
        alist = new ArrayList<CData>();
        adapter = new DataAdapter(this, alist);
        listview.setAdapter(adapter);

        try {
            getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notice_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                //해당 버튼을 클릭시 액션 삽입
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private class DataAdapter extends ArrayAdapter<CData> {

        private LayoutInflater mInflater;

        public DataAdapter(Context context, ArrayList<CData> object) {

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
                view = mInflater.inflate(R.layout.community_list_club, null);
            } else {

                view = v;
            }

            // 자료를 받는다.
            final CData data = this.getItem(position);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //<여기다 상세정보데이터 얻어오는 메소드>
                    // ArrayAdapter객체(여기선 adapter)의 getItem메소드로 선택된 리스트아이템의 position을 얻어서 해당 아이템(여기선 제너릭으로 CData)형식으로 형변환하여 가져옴.

                    String clubNm_f = ((TextView)v.findViewById(R.id.clubName)).getText().toString();


                    Intent intent = new Intent(getContext(), ClubDetailActivity.class);
                    intent.putExtra("clubNm",spliteClubNm(clubNm_f))
                            .putExtra("summit","관리");
                    startActivity(intent);
                    finish();

                }

                public String spliteClubNm(String clubNm_f){
                    String clubName =clubNm_f.substring(clubNm_f.indexOf(":")+1,clubNm_f.length());
                    clubName = clubName.trim();
                    return clubName;
                }
            });
            if (data != null) {
                // 화면 출력
                TextView clubname = (TextView) view.findViewById(R.id.clubName);
                TextView clubID = (TextView) view.findViewById(R.id.clubID);
                TextView clubcontent = (TextView) view.findViewById(R.id.clubContent);
                TextView clubDate = (TextView) view.findViewById(R.id.clubdate);
                TextView clubJoin = (TextView) view.findViewById(R.id.memcnt);
                TextView crewJoin = (TextView) view.findViewById(R.id.crewcnt);


                clubname.setText(data.getClub_name());
                clubcontent.setText(data.getClub_content());
                clubID.setText(data.getClub_ID());
                clubDate.setText(data.getClub_date());
                clubJoin.setText(data.getClub_join());
                crewJoin.setText(data.getClub_crew());
                clubname.setTextColor(Color.BLACK);
                clubcontent.setTextColor(Color.BLACK);
                clubID.setTextColor(Color.BLACK);
                clubDate.setTextColor(Color.BLACK);
                clubJoin.setTextColor(Color.GRAY);
                crewJoin.setTextColor(Color.GRAY);

                ImageView pic = (ImageView) view.findViewById(R.id.clubPic);
                pic.setImageBitmap(data.getClub_Pic());
                Log.e("ID",""+data.getClub_ID());
                Log.e("userID",""+ Util.user.getId());
                //비교하기 위하여 텍스트 뽑아내기
                String club_ID = data.getClub_ID().substring(data.getClub_ID().indexOf(":")+1,data.getClub_ID().length()).trim();
                Log.e("club_ID",""+club_ID);

            }
            return view;

        }
    }

    public void getData() throws Exception {

        clubInfo = Util.user.getClub(0, Util.user.getId());
        clubNm = clubInfo[0];
        clubID = clubInfo[1];
        clubContent = clubInfo[2];
        clubDate = clubInfo[3];
        clubjoin = clubInfo[4];
        clubPhoto = clubInfo[5];
        clubIcon = new Bitmap[clubInfo.length];
        clubNum = clubInfo[6];
        crew = clubInfo[7];
        Log.e("clubNm", "" + clubNm.length);
        for (int i = 0; i < clubNm.length; i++) {
            clubIcon[i] = Util.getIFS().execute(clubPhoto[i]).get();
            adapter.add(new CData(this, "동호회 이름 : " + clubNm[i], "개설자 : " + clubID[i], "내용 : " + clubContent[i],
                    "개설날짜 : " + clubDate[i], "회원수 : " + clubjoin[i], "크루 수 : "+crew[i], clubIcon[i],clubNum[i],clubPhoto[i]));
        }

    }

    class CData {
        private String club_num,club_name, club_ID, club_content, club_date, club_join, club_crew, club_photo;
        private Bitmap club_Pic;
//        private int m_pic, m_imageView;

        public CData(Context context, String name, String ID,
                     String content, String date, String join, String crew, Bitmap pic, String num, String photo) {

            this.club_name = name;
            this.club_ID = ID;
            this.club_content = content;
            this.club_date = date;
            this.club_join = join;
            this.club_crew = crew;
            this.club_Pic = pic;
            this.club_num = num;
            this.club_photo = photo;

        }
        public String getClub_num(){
            return club_num;
        }
        public String getClub_name() {
            return club_name;
        }

        public String getClub_ID() {
            return club_ID;
        }

        public String getClub_content() {
            return club_content;
        }

        public String getClub_date() {
            return club_date;
        }

        public String getClub_join() {
            return club_join;
        }

        public String getClub_crew() {
            return club_crew;
        }

        public Bitmap getClub_Pic() {
            return club_Pic;
        }

        public String getClub_photo(){return club_photo;}

    }
}