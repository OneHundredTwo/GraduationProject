package com.cookandroid.mom.community.club;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.mom.R;
import com.cookandroid.mom.community.replyDialog;
import com.cookandroid.mom.util.DataStringFormat;
import com.cookandroid.mom.util.User;
import com.cookandroid.mom.util.Util;

import java.util.ArrayList;

/**
 * Created by wdj46 on 2017-08-10.
 */

public class ClubTimelineFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    //private Button btnTimeLine, btnCrew;
    private String[][] brdInfo;
    private String[] brdID, brdContent,brdImg,brdDate,brdgood,brdrange,brdclubNum,btncheck,boardNum,reply;
    private Bitmap[] photo;
    private ListView listview;
    private ArrayList<CData> alist;
    private DataAdapter adapter;
    private SwipeRefreshLayout timelineRefresh;
    private AlertDialog alertDialog;
    private com.cookandroid.mom.community.replyDialog replyDialog;
    private boolean lockListView;
    private User.Club club;

    public ClubTimelineFragment(){
    }

    public static ClubTimelineFragment newInstance(){
        return new ClubTimelineFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("TimelineFragment1","one");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.community_clubtimeline_frag, container, false);


        //새로고침 레이아웃 선언

        timelineRefresh = (SwipeRefreshLayout) view.findViewById(R.id.timelineRefresh);
        // 선언한 리스트뷰에 사용할 리스뷰연결
        listview = (ListView) view.findViewById(R.id.listView);

        // 객체를 생성합니다
        alist = new ArrayList<CData>();

        // 데이터를 받기위해 데이터어댑터 객체 선언
        adapter = new DataAdapter(getActivity(), alist);

        // 리스트뷰에 어댑터 연결
        listview.setAdapter(adapter);


        timelineRefresh.setOnRefreshListener(this);
        Log.e("BRDTEST", brdDate==null?"null":""+brdDate.length );

        getData();

        lockListView = false;

        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == SCROLL_STATE_FLING || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lockListView = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }
        });


        return view;
    }
    public void getData(){
        try {
            club = ClubDetailActivity.club;
            brdInfo = Util.user.getBoard(0,club.clubNum, Util.user.getId());
            brdID = brdInfo[0];
            brdContent = brdInfo[1];
            brdDate = brdInfo[2];
            brdImg = brdInfo[3];
            brdgood = brdInfo[4];
            brdrange = brdInfo[5];
            brdclubNum = brdInfo[6];
            btncheck = brdInfo[7];
            boardNum = brdInfo[8];
            reply = brdInfo[9];


            photo = new Bitmap[brdImg.length];
            // 받아온 pRecvServerPage를 분석하는 부분


        } catch (Exception e) {
            e.getStackTrace();
        }
        for(int i=0; i<brdDate.length; i++) {
            try {
                photo[i] = Util.getIFS().execute(brdImg[i]).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            alist.add(new CData(getActivity().getApplicationContext(), brdID[i],
                    DataStringFormat.CreateDataWithCheck(brdDate[i]), brdContent[i],brdrange[i],Integer.parseInt(brdclubNum[i]),
                    Integer.parseInt(brdgood[i]),photo[i],Integer.parseInt(boardNum[i]),Boolean.valueOf(btncheck[i]).booleanValue(),Integer.parseInt(reply[i])));
        }
    }
    public void onRefresh(){
        timelineRefresh.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(ClubTimelineFragment.this).attach(ClubTimelineFragment.this).commit();
                timelineRefresh.setRefreshing(false);

            }
        }, 1000);
    }
    private class DataAdapter extends ArrayAdapter<CData> {
        // 레이아웃 XML을 읽어들이기 위한 객체
        private LayoutInflater mInflater;

        public DataAdapter(Context context, ArrayList<CData> object) {

            // 상위 클래스의 초기화 과정
            // context, 0, 자료구조
            super(context, 0, object);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public View getView(final int position, View v, ViewGroup parent) {
            View view = null;

            // 현재 리스트의 하나의 항목에 보일 컨트롤 얻기
            if (v == null) {
                // XML 레이아웃을 직접 읽어서 리스트뷰에 넣음
                v = mInflater.inflate(R.layout.community_list_timeline, null);
                ViewHolder holder = new ViewHolder();
                holder.name = (TextView) v.findViewById(R.id.name);
                holder.time  = (TextView) v.findViewById(R.id.time);
                holder.rangetxt = (TextView)v.findViewById(R.id.rangetxt);
                holder.content = (TextView) v.findViewById(R.id.content);
                holder.good =(TextView)v.findViewById(R.id.brdgood);
                holder.brdreply=(TextView)v.findViewById(R.id.brdreply);
                holder.btnLike  = (ImageView) v.findViewById(R.id.btnLike);
                holder.btnReply = (ImageView) v.findViewById(R.id.btnRe);
                holder.deltimeline = (ImageView) v.findViewById(R.id.delTimeline);
                holder.imageView = (ImageView) v.findViewById(R.id.imageView);
                v.setTag(holder);
            } else {

                view = v;
            }

            // 자료를 받는다.
            final CData data = this.getItem(position);

            if (data != null) {
                // 화면 출력
                final ViewHolder holder = (ViewHolder)v.getTag();
                User.Club club =  Util.user.getClubDetail(data.getM_clubNum());

                if(data.getM_range().equals("동호회")){
                    holder.rangetxt.setText(club.clubNm);
                }else if(data.getM_range().equals("비공개")){
                    holder.rangetxt.setText("비공개");
                }

                holder.name.setText(data.getM_name());
                holder.time.setText(data.getM_time());
                holder.content.setText(data.getM_content());
                holder.good.setText(data.getM_good()+"명이 게시글을 좋아합니다.");
                holder.brdreply.setText("댓글"+data.getM_reply()+"명");
                holder.name.setTextColor(Color.BLACK);
                holder.time.setTextColor(Color.GRAY);
                holder.content.setTextColor(Color.BLACK);
                if(data.getM_btncheck()){
                    holder.btnLike.setImageResource(R.drawable.good_selected);
                    holder.btnLike.setTag("good");
                }else{
                    holder.btnLike.setImageResource(R.drawable.good_no_selected);
                    holder.btnLike.setTag("no_good");
                }

                if(data.getM_name().equals(Util.user.getId())){
                    holder.deltimeline.setVisibility(View.VISIBLE);
                }else{
                    holder.deltimeline.setVisibility(View.GONE);
                }

                holder.imageView.setImageBitmap(data.getM_photo());
                if(data.getM_photo()==null){
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)holder.imageView.getLayoutParams();
                    params.width = 0;
                    params.height = 0;
                }
                holder.btnLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alist.clear();
                        if(((String)holder.btnLike.getTag()).equals("no_good")) {
                            holder.btnLike.setImageResource(R.drawable.good_selected);
                            int brdgood = data.getM_good()+1;
                            holder.good.setText(brdgood+"명이 게시글을 좋아합니다.");
                            String message = Util.community.InsertGood(Util.user.getId(),data.getM_name(),data.getM_content(),data.getM_boardNum());
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }else{
                            holder.btnLike.setImageResource(R.drawable.good_no_selected);
                            int brdgood = data.getM_good()-1;
                            holder.good.setText(brdgood+"명이 게시글을 좋아합니다.");
                            String message = Util.community.releaseGood(Util.user.getId(),data.getM_name(),data.getM_boardNum());
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                        getData();
                    }
                });

                holder.deltimeline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setTitle("삭제");

                        alertDialogBuilder
                                .setMessage("이 타임라인을 삭제하시겠습니까?")
                                .setCancelable(false)
                                .setPositiveButton("삭제",
                                        new DialogInterface.OnClickListener(){

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                try {
                                                    String message = Util.user.delBoard(data.getM_boardNum(),data.getM_name());
                                                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                                    if(message.equals("타임라인이 삭제 되었습니다.")){
                                                        onRefresh();
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }


                                            }
                                        })
                                .setNegativeButton("취소", new DialogInterface.OnClickListener(){

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                        alertDialog = alertDialogBuilder.create();
                        if(alertDialog != null && alertDialog.isShowing() ){
                            alertDialog.dismiss();
                        }
                        alertDialog.show();
                    }
                });
                holder.btnReply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        replyDialog = new replyDialog(getContext(),data.getM_boardNum(),data.getM_name());
                        replyDialog.show();

                        replyDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                onRefresh();
                                listview.setSelection(position);
                            }
                        });

                    }
                });




            }
            return v;

        }


    }

    // CData안에 받은 값을 직접 할당
    class CData {

        private String m_name, m_time, m_content,m_range;
        private Bitmap m_photo;
        private boolean m_btncheck;
        private int m_clubNum, m_good,m_boardNum,m_reply;
//        private int m_pic, m_imageView;



        public CData(Context context, String name, String time,
                     String content, String range, int clubNum, int good, Bitmap photo, int boardNum, boolean check, int reply) {

            this.m_name = name;
            this.m_time = time;
            this.m_content = content;
            this.m_range = range;
            this.m_clubNum = clubNum;
            this.m_good = good;
            this.m_photo = photo;
            this.m_boardNum = boardNum;
            this.m_btncheck = check;
            this.m_reply = reply;
        }

        public String getM_name() {
            return m_name;
        }

        public String getM_time() {
            return m_time;
        }

        public String getM_content() {
            return m_content;
        }

        public String getM_range(){return m_range;}

        public int getM_clubNum(){return m_clubNum;}

        public int getM_good(){return m_good;}

        public Bitmap getM_photo(){return m_photo;}

        public int getM_boardNum(){return m_boardNum;}

        public boolean getM_btncheck(){return m_btncheck;}

        public int getM_reply() {return m_reply;}
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult ", "응답 인텐트 받았다");
        //로그인 액티비티로 넘어간 후에 로그인에 성공한 경우(인텐트로 응답메세지를 받았을때)
        //수행할 ui작업 목록

        if (resultCode == Activity.RESULT_OK) {
            Fragment fg = ClubTimelineFragment.newInstance();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.child_thirdfragment_container, fg);
            /* Fragment의 변경사항을 반영시킨다. */
            transaction.commit();

        }

    }


    private class ViewHolder {
        TextView name;
        TextView time;
        TextView rangetxt;
        TextView content;
        TextView good;
        ImageView deltimeline;
        TextView brdreply;
        ImageView btnLike,btnReply;
        ImageView imageView;
    }
}