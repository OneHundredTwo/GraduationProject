package com.cookandroid.mom.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.mom.R;
import com.cookandroid.mom.home.NoticeActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

/**
 * Created by S401 on 2017-06-15.
 */

public class Community {

    String content;//나중에 그림,글을 포함하는 새로운 자료구조 클래스로 변경가능.

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    static Community getInstance() {
        return new Community();
    }

    ////-----------------------------공지사항 관련 클래스 및 메소드---------------------------------

    String[] noticeCategory = new String[]{"일반","산정보","이벤트","시스템"};
    public int findCatPosition(String cat){
        int i=0;
        for(String s:noticeCategory){
            if(s.equals(cat)){
                return i;
            }
            i++;
        }
        return i;
    }

    public static class Notice implements Serializable{
        static SimpleDateFormat notice_date = new SimpleDateFormat("yyyy-MM-dd");
        Integer notice_num;
        String content;
        long date;
        String title;
        String admin_id;
        String category;

        public Notice(int notice_num, String content, long date, String title, String admin_id, String category) {
            this.notice_num = notice_num;
            this.content = content;
            this.date = date;
            this.title = title;
            this.admin_id = admin_id;
            this.category = category;
        }

        public int getNotice_num() {
            return notice_num;
        }

        public String getContent() {
            return content;
        }

        public long getDate() {
            return date;
        }

        public String getTitle() {
            return title;
        }

        public String getAdmin_id() {
            return admin_id;
        }

        public String getCategory() {
            return category;
        }

        public static void showNoticeDialog(final Context context, View view){
            int situation = Util.INSERT;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialog_view = inflater.inflate(R.layout.home_manage_notice_dialog, null);
            View v = dialog_view;

            int nn=-1;
            String btnPositive = "등록";

            final int toMtNum;
            final TextView noticeNum, noticeDate, adminId;
            final Spinner noticeCat;
            final EditText noticeContent, noticeTitle;

            noticeNum = (TextView)v.findViewById(R.id.noticeDiaNum);
            noticeDate = (TextView)v.findViewById(R.id.noticeDiaDate);
            adminId = (TextView)v.findViewById(R.id.noticeAdminId);
            noticeCat = (Spinner)v.findViewById(R.id.noticeSpinCat);
            noticeContent = (EditText)v.findViewById(R.id.noticeDiaContent);
            noticeTitle = (EditText)v.findViewById(R.id.noticeDiaTitle);

            Log.e("noticeNum","showNoticeDialog: "+(noticeNum==null?"null":"notnull"));

            Log.e("notice spinner", "showNoticeDialog: "+(noticeCat==null?"null":"notnull"));

            //기본값 세팅
            noticeCat.setAdapter(new ArrayAdapter<String>(context, R.layout.spinner_item, Util.community.noticeCategory));

            if(view != null) {
                situation = Util.UPDATE;
                btnPositive = "수정";
                Notice data = (Notice) view.getTag();

                nn = data.getNotice_num();
                noticeNum.setText(""+data.getNotice_num());
                noticeDate.setText(DataStringFormat.y_mon_d_h_m.format(data.getDate()));
                adminId.setText(data.getAdmin_id());
                noticeCat.setSelection(Util.community.findCatPosition(data.getCategory()));
                noticeContent.setText(data.getContent());
                noticeTitle.setText(data.getTitle());
            }else{
                noticeNum.setText("new");
                noticeTitle.setText("");
                noticeDate.setText(DataStringFormat.y_mon_d_h_m.format(Calendar.getInstance().getTimeInMillis()));
                adminId.setText(Util.user.getId());
            }

            //다이얼로그 띄우기
            final int notice_num = nn;
            final int s = situation;
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("공지사항")
                    .setView(dialog_view)
                    .setPositiveButton(btnPositive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            long time = Calendar.getInstance().getTimeInMillis();
                            try{
                                time = (DataStringFormat.y_mon_d_h_m.parse(noticeDate.getText().toString()).getTime());
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            Notice notice = new Notice(
                                    notice_num,
                                    noticeContent.getText().toString(),
                                    time,
                                    noticeTitle.getText().toString(),
                                    Util.user.getId(),
                                    (String)noticeCat.getSelectedItem()
                            );
                            switch (s) {
                                case Util.INSERT: {
                                    Util.community.insertNotice(notice);
                                    break;
                                }
                                case Util.UPDATE: {
                                    Util.community.updateNotice(notice);
                                    break;
                                }
                            }
                            Util.manage.fragment2.refresh();
                        }
                    })
                    .setNegativeButton("취소", null)
                    .show();




        }
    }

    public interface NoticeClickListener{
        public void onClick(View view);
    }

    public static class NoticeAdapter extends ArrayAdapter<Notice> {
        // 레이아웃 XML을 읽어들이기 위한 객체
        Context context;
        private LayoutInflater mInflater;
        private NoticeClickListener click;

        public NoticeAdapter(Context context, ArrayList<Notice> object, NoticeClickListener click) {

            // 상위 클래스의 초기화 과정
            // context, 0, 자료구조
            super(context, 0, object);
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.click = click;
            this.context = context;

        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            View view = null;
            if (v == null) {
                view = mInflater.inflate(R.layout.home_list_main_notice, null);
            } else {
                view = v;
            }

            // 자료를 받는다.
            final Notice data = this.getItem(position);

            view.setTag(data);

            // title 보여줄 글자수 제한하는 변수
            int n=19;


            // [CAT](isNew)[title][date]
            TextView category,isNew,title,date = null;
            category = (TextView)view.findViewById(R.id.noticeCat);
            isNew = (TextView)view.findViewById(R.id.notice_isNew);
            title = (TextView)view.findViewById(R.id.noticeTitle);
            date = (TextView)view.findViewById(R.id.noticeDate);

            category.setText(data.getCategory());
            if(isDataNew(data)){
                isNew.setVisibility(View.VISIBLE);
                n = 18;
            }else{
                isNew.setVisibility(View.INVISIBLE);
            }

            String title_text = data.getTitle();
            if(data.getTitle().length() > n){
                title_text = data.getTitle().substring(0,n)+"...";
            }
            title.setText(title_text);

            date.setText(Notice.notice_date.format(data.date));

            //공지사항 클릭했을때 이벤트
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    click.onClick(v);
                }
            });

            return view;

        }

        public boolean isDataNew(Notice data){
            boolean isNew=false;

            Calendar now = Calendar.getInstance();
            Calendar noticeDate = Calendar.getInstance();
            noticeDate.setTimeInMillis(data.getDate());

            if(now.get(Calendar.DAY_OF_YEAR)-noticeDate.get(Calendar.DAY_OF_YEAR) > 7){
                isNew = false;
            }else{
                isNew = true;
            }

            return isNew;
        }
    }

    public void insertNotice(Notice notice){
        String uri = "InsertNotice.jsp";
        try{
        String message = Util.getAnt().execute(
                uri,
                "title:"+notice.getTitle(),
                "content:"+notice.getContent(),
                "category:"+notice.getCategory(),
                "admin_id:"+notice.getAdmin_id(),
                "date:"+notice.getDate()
        ).get();
        }catch(Exception e){
            e.printStackTrace();
        }
    };

    public void updateNotice(Notice notice){
        String uri = "UpdateNotice.jsp";
        try{
            String message = Util.getAnt().execute(
                    uri,
                    "title:"+notice.getTitle(),
                    "content:"+notice.getContent(),
                    "category:"+notice.getCategory(),
                    "admin_id:"+notice.getAdmin_id(),
                    "date:"+notice.getDate(),
                    "noticeNum:"+notice.getNotice_num()
            ).get();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<Notice> getNotices(){
        ArrayList<Notice> notices = new ArrayList();
        String uri = "Notices.jsp";
        try{
            String message = Util.getAnt().execute(uri).get();
            JSONObject response = new JSONObject(message);
            JSONArray jarr = response.getJSONArray("notices");

            for(int i=0; i<jarr.length(); i++){
                JSONObject obj = (JSONObject)jarr.get(i);

                Notice notice =
                        new Notice(
                                obj.getInt("noticeNum"),
                                obj.getString("content"),
                                obj.getLong("date"),
                                obj.getString("title"),
                                obj.getString("admin_id"),
                                obj.getString("category")
                        );

                notices.add(notice);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return notices;
    }


    //-----------------------------타임라인 관련 클래스 및 메소드---------------------------------
    public String insertTimeline(String id, String content,String range,String photo, int clubNum, String currentLatitude, String currentLongitude, String nearMt) throws Exception {
        String uri = "InsertBoard.jsp";

        String message = Util.getAnt().execute(uri, "id:" + id, "content:" + content, "range:" + range, "photo:" + photo, "clubNum:" + clubNum, "currentLatitude:" + currentLatitude, "currentLongitude:" + currentLongitude, "nearMt:"+nearMt).get();
        return message;


    }


    ////-----------------------------동호회 관련 클래스 및 메소드---------------------------------
    public String InsertClub(String name, String ID, String content, String Icon)throws Exception{
        String uri = "InsertClub.jsp";
        String message = Util.getAnt().execute(uri,"name:"+name,"clubID:"+ID,"content:"+content,"Icon:"+Icon).get();

        return message;
    }

    ////-----------------------------크루 관련 클래스 및 메소드---------------------------------
    public String InsertCrew(String ID, String join, String mNum, String date, int clubNum, String content,String region, String courseNum, String token){
        String uri = "InsertCrew.jsp";
        String message = null;
        try {
            message = Util.getAnt().execute(uri,"ID:"+ID,"joinNum:"+join,"mNum:"+mNum,"date:"+date,"clubNum:"+clubNum,"content:"+content,"region:"+region, "course:"+courseNum,"token:"+token).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return message;
    }

    //////----------------------------- 좋아요 관련 클래스 및 메소드---------------------------------
    public String InsertGood(String id, String boardID,String content, int boardNum){
        String uri ="InsertGood.jsp";
        String message = null;
        try{
            message = Util.getAnt().execute(uri,"id:"+id,"boardID:"+boardID,"content:"+content,"boardNum:"+boardNum).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return message;
    }
    public String releaseGood(String id, String boardID, int boardNum){
        String uri ="releaseGood.jsp";
        String message = null;
        try{
            message = Util.getAnt().execute(uri,"id:"+id,"boardID:"+boardID,"boardNum:"+boardNum).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return message;
    }

    //--------------------------------리플달기 관련 클래스 및 메소드----------------------------------
    public String InsertReply(int boardNum,String id, String content,String writer){
        String uri = "InsertReply.jsp";
        String message = null;

        try {
            message = Util.getAnt().execute(uri,"boardNum:"+boardNum,"id:"+id,"content:"+content,"writer:"+writer).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return message;

    }

}
