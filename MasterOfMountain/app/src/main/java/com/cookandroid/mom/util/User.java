package com.cookandroid.mom.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.mom.MainActivity;
import com.cookandroid.mom.R;
import com.cookandroid.mom.community.club.CrewDetailActivity;
import com.cookandroid.mom.my.MyFragment;
import com.cookandroid.mom.my.MySchduleFragment3;
import com.google.firebase.iid.FirebaseInstanceId;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by S401 on 2017-06-07.
 */

public class User {
    private String tag = "User";

    private String id;
    private String password;
    private String name;
    private String phone;
    private String birth;
    private String address;
    private String question_find_password;
    private boolean isAdmin;

    //아직몰라
    private String GPS;
    private String sensors;
    private String excercise;

    private boolean logout_status = false;

    public SimpleDateFormat schFormat = DataStringFormat.y_mon_d_h_m;


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getBirth() {
        return birth;
    }
    public void setBirth(String birth) {
        this.birth = birth;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getQuestion_find_password() {
        return question_find_password;
    }
    public void setQuestion_find_password(String question_find_password) {
        this.question_find_password = question_find_password;
    }
    public String getGPS() {
        return GPS;
    }
    public void setGPS(String gPS) {
        GPS = gPS;
    }
    public String getSensors() {
        return sensors;
    }
    public void setSensors(String sensors) {
        this.sensors = sensors;
    }
    public String getExcercise() {
        return excercise;
    }
    public void setExcercise(String excercise) {
        this.excercise = excercise;
    }

    private User(){
    };

    static User getInstance(){
        return new User();
    }

    //<회원가입 데이터 입력>registry.xml - Registry.java - done.onClickListener()
    public String registry(String id, String pwd, String pwd2, String address, String email, String phone, String sex, String hint) throws Exception{

        String uri = "Registry.jsp";
        String message = Util.getAnt().execute(uri,"id:"+id, "password:"+pwd, "password2:"+pwd2,"address:"+address,"sex:"+sex,"phone:"+phone,"email:"+email,"hint:"+hint).get();

        return message;
    }
    //------------------------------------크루 관련 클래스 및 메소드----------------------------------------------
    public static class Crew{
        public int planNum, prejoin, joinNum, coursenum;
        public String planID, mtNm, planDate, clubNum, plancontent, planregion;

        public Crew(int pn, String pi, int pj, int jn, String mn, String pd, String cn, String pc, String pr, int cs){
            this.planNum = pn;
            this.planID = pi;
            this.prejoin = pj;
            this.joinNum = jn;
            this.mtNm = mn;
            this.planDate = pd;
            this.clubNum = cn;
            this.plancontent = pc;
            this.planregion = pr;
            this.coursenum = cs;
        }
    }

    public String[][] getCrew(int check, String id) throws Exception{
        String[][] crewInfo = null;
        String uri = "Crew.jsp";
        JSONObject requestJSON = null;
        requestJSON = new JSONObject(Util.getAnt().execute(uri,"check:"+check,"id:"+id).get());

        JSONArray jarr = requestJSON.getJSONArray("crew");
        crewInfo = new String[7][jarr.length()];
        Log.e("json getCrew",""+jarr.length());

        for(int i=0; i<jarr.length(); i++){
            JSONObject json = jarr.getJSONObject(i);
            crewInfo[0][i] = json.getString("planNum");
            crewInfo[1][i] = json.getString("planID");
            crewInfo[2][i] = json.getString("prejoin");
            crewInfo[3][i] = json.getString("joinNum");
            crewInfo[4][i] = json.getString("mNum");
            crewInfo[5][i] = json.getString("planDate");
            crewInfo[6][i] = json.getString("clubNum");
        }
        return crewInfo;

    }
    public String joinCrew(int planNum, String id, String token)throws Exception{
        String uri = "joinCrew.jsp";
        Log.e("Log",id+":"+planNum);
        String message = Util.getAnt().execute(uri,"planNum:"+planNum,"id:"+id,"token:"+token).get();

        return message;
    }
    public String dismissCrew(int planNum, String id)throws Exception{
        String uri = "DismissCrew.jsp";
        Log.e("DismissCrew",id+":"+planNum);
        String message = Util.getAnt().execute(uri,"planNum:"+planNum,"id:"+id).get();

        return message;
    }
    public String secCrew(int planNum, String id, String token)throws Exception{
        String uri = "secessionCrew.jsp";
        Log.e("DismissCrew",id+":"+planNum);
        String message = Util.getAnt().execute(uri,"planNum:"+planNum,"id:"+id,"token:"+token).get();

        return message;
    }
    public Crew getCrewDetail(int crew){
        JSONObject requestJSON = null;
        try {
            String uri = "CrewDetail.jsp";
            requestJSON = new JSONObject(Util.getAnt().execute(uri, "crew:" + crew).get());

            int planNum = requestJSON.getInt("planNum");
            String planID = requestJSON.getString("planID");
            int prejoin = requestJSON.getInt("prejoin");
            int joinNum = requestJSON.getInt("joinNum");
            String mtNm = requestJSON.getString("mtNm");
            String planDate = requestJSON.getString("planDate");
            String clubNum = requestJSON.getString("clubNum");
            String planCotent = requestJSON.getString("plancontent");
            String planregion = requestJSON.getString("planregion");
            int coursenum = requestJSON.getInt("coursenum");

            Crew result = new Crew(planNum, planID, prejoin, joinNum, mtNm, planDate, clubNum,planCotent,planregion,coursenum);

            return result;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //------------------------------------동호회 관련 클래스 및 메소드-----------------------------------------------

    public static class Club{
        public String clubNm;
        public String clubID;
        public String clubContent;
        public String clubDate;
        public int clubNum, clubJoin;
        public String clubPhoto;

        public Club(int clubNum, String n, String i, String c, String d, int j, String p){
            this.clubNum = clubNum;
            this.clubNm = n;
            this.clubID = i;
            this.clubContent = c;
            this.clubDate = d;
            this.clubJoin = j;
            this.clubPhoto = p;
        }
    }

    public String[][] getClub(int check, String id) throws Exception{
        String[][] clubInfo = null;
        String uri = "Club.jsp";
        JSONObject requestJSON = null;
        // check : 전체 클럽 리스트를 불러오는지 자신이 가입한 클럽 리스트를 불러오는지에 대한 검사
        requestJSON = new JSONObject(Util.getAnt().execute(uri,"check:"+check, "id:"+id).get());


        JSONArray jarr = requestJSON.getJSONArray("club");
        clubInfo = new String[8][jarr.length()];
        Log.e("json getClub", ""+jarr.length());

        for(int i=0; i < jarr.length(); i++){
            JSONObject json = jarr.getJSONObject(i);
            clubInfo[0][i] = json.getString("clubname");
            clubInfo[1][i] = json.getString("clubID");
            clubInfo[2][i] = json.getString("clubContent");
            clubInfo[3][i] = json.getString("clubDate");
            clubInfo[4][i] = json.getString("clubjoin");
            clubInfo[5][i] = json.getString("clubIcon");
            clubInfo[6][i] = json.getString("clubNum");
            clubInfo[7][i] = json.getString("crew");
            Log.e("json clubname",""+clubInfo[0][i]);
            Log.e("json clubID",""+clubInfo[1][i]);
            Log.e("json clubContent",""+clubInfo[2][i]);
            Log.e("json clubDate",""+clubInfo[3][i]);
            Log.e("json clubjoin",""+clubInfo[4][i]);
            Log.e("json clubIcon",""+clubInfo[5][i]);
        }

        return clubInfo;
    }
    public Club getClubDetail(String club){
        JSONObject requestJSON = null;
        try {
            String uri = "ClubDetail.jsp";
            requestJSON = new JSONObject(Util.getAnt().execute(uri, "club:" + club).get());

            int clubNum = requestJSON.getInt("clubNum");
            String clubNm = requestJSON.getString("clubNm");
            String clubID = requestJSON.getString("clubID");
            String clubCotent = requestJSON.getString("clubContent");
            String clubDate = requestJSON.getString("clubDate");
            int clubJoin = requestJSON.getInt("clubJoin");
            String clubIcon =requestJSON.getString("clubIcon");
            Club result = new Club(clubNum, clubNm, clubID, clubCotent, clubDate, clubJoin, clubIcon);

            return result;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public Club getClubDetail(int Num){
        JSONObject requestJSON = null;
        try {
            String uri = "ClubDetail.jsp";
            requestJSON = new JSONObject(Util.getAnt().execute(uri, "clubNum:" + Num).get());

            int clubNum = requestJSON.getInt("clubNum");
            String clubNm = requestJSON.getString("clubNm");
            String clubID = requestJSON.getString("clubID");
            String clubCotent = requestJSON.getString("clubContent");
            String clubDate = requestJSON.getString("clubDate");
            int clubJoin = requestJSON.getInt("clubJoin");
            String clubIcon =requestJSON.getString("clubIcon");
            Club result = new Club(clubNum, clubNm, clubID, clubCotent, clubDate, clubJoin, clubIcon);

            return result;
        }catch(Exception e){
            Log.i("getClubDetail", e.getMessage());;
            return null;
        }
    }
    public String regClub(int clubNum, String id)throws Exception{
        String uri = "RegistryClub.jsp";
        Log.e("Log",id+":"+clubNum);
        String message = Util.getAnt().execute(uri,"clubNum:"+clubNum,"id:"+id).get();

        return message;
    }
    public String secClub(int clubNum, String id)throws Exception{
        String uri = "secessionClub.jsp";
        Log.e("Log", id+":"+clubNum);
        String message = Util.getAnt().execute(uri, "clubNum:"+clubNum,"id:"+id).get();

        return message;
    }
    public String dismissClub(int clubNum, String id, String icon)throws Exception{
        String uri = "DismissClub.jsp";
        Log.e("Dismiss",id+":"+clubNum);
        String message = Util.getAnt().execute(uri,"clubNum:"+clubNum,"id:"+id,"Icon:"+icon).get();

        return message;
    }

    //-------------------------------------------타임라인/공지사항 관련 메소드------------------------------
    public String[][] getBoard(int check, String id) throws Exception{
        String[][] brdInfo = null;
        String uri = "Board.jsp";
        JSONObject requestJSON =null;
        requestJSON = new JSONObject(Util.getAnt().execute(uri,"check:"+check,"id:"+id).get());
        JSONArray jarr = requestJSON.getJSONArray("board");
        brdInfo = new String[10][jarr.length()];
        Log.e("json getBoard", ""+brdInfo.length);

        for(int i=0; i<jarr.length(); i++){
            JSONObject json = jarr.getJSONObject(i);
            brdInfo[0][i] = json.getString("id");
            brdInfo[1][i] = json.getString("content");
            brdInfo[2][i] = json.getString("date");
            brdInfo[3][i] = json.getString("photo");
            brdInfo[4][i] = String.valueOf(json.getInt("good"));
            brdInfo[5][i] = json.getString("boardrange");
            brdInfo[6][i] = String.valueOf(json.getInt("clubNum"));
            brdInfo[7][i] = String.valueOf(json.getBoolean("checking"));
            brdInfo[8][i] = String.valueOf(json.getInt("boardNum"));
            brdInfo[9][i] = String.valueOf(json.getInt("reply"));
        }

        return brdInfo;
    }
    public String[][] getBoard(int check, int clubNum, String id) throws Exception{
        String[][] brdInfo = null;
        String uri = "Board.jsp";
        JSONObject requestJSON =null;
        requestJSON = new JSONObject(Util.getAnt().execute(uri,"check:"+check,"clubNum:"+clubNum,"id:"+id).get());
        JSONArray jarr = requestJSON.getJSONArray("board");
        brdInfo = new String[10][jarr.length()];
        Log.e("json getBoard", ""+brdInfo.length);

        for(int i=0; i<jarr.length(); i++){
            JSONObject json = jarr.getJSONObject(i);
            brdInfo[0][i] = json.getString("id");
            brdInfo[1][i] = json.getString("content");
            brdInfo[2][i] = json.getString("date");
            brdInfo[3][i] = json.getString("photo");
            brdInfo[4][i] = String.valueOf(json.getInt("good"));
            brdInfo[5][i] = json.getString("boardrange");
            brdInfo[6][i] = String.valueOf(json.getInt("clubNum"));
            brdInfo[7][i] = String.valueOf(json.getBoolean("checking"));
            brdInfo[8][i] = String.valueOf(json.getInt("boardNum"));
            brdInfo[9][i] = String.valueOf(json.getInt("reply"));
        }

        return brdInfo;
    }
    public String[][] getNotice(String ... area) {
        String[][] brdInfo = null;
        String uri = "Board.jsp";
        JSONObject requestJSON =null;
        try {
            requestJSON = new JSONObject(Util.getAnt().execute(uri, "check:0","id:admin").get());
            JSONArray jarr = requestJSON.getJSONArray("board");
            brdInfo = new String[3][jarr.length()];
            Log.e("json getBoard", "" + jarr.length());

            for (int i = 0; i < jarr.length(); i++) {
                JSONObject json = jarr.getJSONObject(i);
                brdInfo[0][i] = json.getString("id");
                brdInfo[1][i] = json.getString("content");
                brdInfo[2][i] = json.getString("date");
            }

            return brdInfo;
        }catch(Exception e){
            return null;
        }
    }
    public String[][] getmyBoard(String ... area) {
        String[][] brdInfo = null;
        String uri = "Board.jsp";
        JSONObject requestJSON =null;

        try{
            if(id==null){throw new Exception();}
            requestJSON = new JSONObject(Util.getAnt().execute(uri,"check:0", "id:" + id).get());
            JSONArray jarr = requestJSON.getJSONArray("board");
            brdInfo = new String[3][jarr.length()];
            Log.e("json getMyBoard", "" + jarr.length());

            for (int i = 0; i < jarr.length(); i++) {
                JSONObject json = jarr.getJSONObject(i);
                brdInfo[0][i] = json.getString("id");
                brdInfo[1][i] = json.getString("content");
                brdInfo[2][i] = json.getString("date");
            }

            return brdInfo;
        }catch(Exception e){
            return null;
        }

    }
    public String delBoard(int boardNum, String id){
        String uri = "delBoard.jsp";
        String message = null;
        try {
            message = Util.getAnt().execute(uri,"boardNum:"+boardNum,"id:"+id).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return message;

    }
    //------------------------------------------리플 관련 클래스 및 메소드------------------------------------
    public String[][] getReply(int boardNum) {
        String[][] repInfo = null;
        String uri = "reply.jsp";
        JSONObject requestJSON =null;

        try{
            requestJSON = new JSONObject(Util.getAnt().execute(uri,"boardNum:"+boardNum).get());
            JSONArray jarr = requestJSON.getJSONArray("reply");
            repInfo = new String[3][jarr.length()];
            Log.e("reply", "" + jarr.length());

            for (int i = 0; i < jarr.length(); i++) {
                JSONObject json = jarr.getJSONObject(i);
                repInfo[0][i] = json.getString("id");
                repInfo[1][i] = json.getString("content");
                repInfo[2][i] = json.getString("date");
            }

            return repInfo;
        }catch(Exception e){
            return null;
        }

    }
    public String editReply(String content, int boardNum,String time){
        String uri = "editReply.jsp";
        String message = null;

        try {
            message = Util.getAnt().execute(uri,"content:"+content,"boardNum:"+boardNum,"time:"+time).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return message;
    }
    public String delReply(int boardNum, String writer,String time){
        String uri = "delReply.jsp";
        String message = null;

        try {
            message = Util.getAnt().execute(uri,"boardNum:"+boardNum,"boardID:"+writer,"time:"+time).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return message;
    }

    //--------------------------------------------내정보 관련 메소드-----------------------------------------
    public String[] myInfo(String ... area) throws Exception{
        String[] myInfo = null;
        String uri = "MyInfo.jsp";
        JSONObject requestJSON =null;
        requestJSON = new JSONObject(Util.getAnt().execute(uri,"id:"+getId()).get());
        JSONObject json = requestJSON.getJSONObject("myinfo");
        myInfo = new String[json.length()];
        Log.e("json", ""+json.length());

        myInfo[0] = json.getString("id");
        myInfo[1] = json.getString("address");
        myInfo[2] = json.getString("sex");
        myInfo[3] = json.getString("phone");
        myInfo[4] = json.getString("email");
        ///////////////////////////////////////////////
        myInfo[5] = json.getString("hint");
//////////////////////////////////////////////////////



        return myInfo;
    }

    public String Edit(String id, String pwd, String pwd2, String address, String email, String phone, String hint){
        String uri = "Edit.jsp";
        String message = null;

        try {
            message = Util.getAnt().execute(uri,"id:"+id,"password:"+pwd,"password2:"+pwd2,"address:"+address,"email:"+email,"phone:"+phone,"hint:"+hint).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return message;
    }

    public String Session(String id){
        String uri = "Session.jsp";
        String message = null;

        try {
            message = Util.getAnt().execute(uri,"id:"+id).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return message;
    }
    //---------------------------------------------로그인 관련 메소드-----------------------------------------

    public static class LoginStatus{
        boolean status;
        boolean islogin;
        String message;

        public LoginStatus(boolean status, boolean islogin, String message) {
            this.status = status;
            this.islogin = islogin;
            this.message = message;
        }

        public boolean isStatus() {
            return status;
        }

        public boolean islogin() {
            return islogin;
        }

        public String getMessage() {
            return message;
        }
    }

    public LoginStatus loginCheck(String id, String password){
        LoginStatus ls=null;

        String url = "LoginCheck.jsp";
        try {
            String message = Util.getAnt().execute(url, "id:" + id, "password:" + password).get();
            JSONObject response = new JSONObject(message);
            ls = new LoginStatus(response.getBoolean("status"), response.getBoolean("islogin"),response.getString("message"));
        }catch(Exception e){

        }
        return ls;
    }


    public String login(String id, boolean islogin) throws Exception{
        String uri = "Login.jsp";
        Log.e("Log",id+":"+password);
        String message = Util.getAnt().execute(uri,"id:"+id,"islogin:"+islogin, "token:"+FirebaseInstanceId.getInstance().getToken()).get();
        JSONObject response = new JSONObject(message);
        if(response.getInt("status")==1){
            message = "로그인 성공";
            JSONObject user_info = response.getJSONObject("user_info");
            Util.user.setId(user_info.getString("id"));
            Util.user.setAdmin(user_info.getBoolean("isAdmin"));
        }else{
            message = "로그인 과정에서 문제가 발생하였습니다. 다시 시도해주세요.";
        }

        return message;
    }

    public String autoLogin(String token)throws Exception{
        String uri = "autoLogin.jsp";
        String message = Util.getAnt().execute(uri,"token:"+token).get();

        return message;
    }

    public boolean logout(){

        Log.e(tag, "logout start");


        try {

            //문제. main Thread를 wait하면 FirebaseInstanceId.getInstanceId().deleteInstanceId()가 작동하지 않는다.(Timeout Exception 발생)
            //FirebaseInstanceId.getInstanceId().deleteInstanceId();
            //아니면 이걸,.... Service에 넣어야하나? Service는 어떻게 호출해서 쓰지??

            JSONObject res;
            String uri = "Logout.jsp";
            res = new JSONObject(Util.getAnt().execute(uri, "token:" + FirebaseInstanceId.getInstance().getToken()).get());
            if(res.getInt("status")==1){
                logout_status=true;
                Log.e(tag, "logout sucess");
                Util.user.setId(null);
                Util.user.setAdmin(false);
            }else{
                logout_status=false;
                Log.e(tag, "logout fail");
            };

        }catch(JSONException | InterruptedException | ExecutionException e){
            e.printStackTrace();
            logout_status=false;
            Log.e(tag, "logout fail");
        }



        if(logout_status) {
            setId(null);
        }
        Log.e(tag, "logout end");
        return logout_status;
    }

    //-----------------------------------------스케쥴 관련 클래스 및 메소드-------------------------------------
    //User에서 받아오는 ArrayList<CalendarDay>를 저장한 ArrayList에 저장순서에 따른 색깔상수
    public final static int IMPORTANCE_LOW = 0;
    public final static int IMPORTANCE_MIDDLE = 1;
    public final static int IMPORTANCE_HIGH = 2;
    public final static int IMPORTANCE_CREW = 3;

    public final static int IMPORTANCE_COLOR_CREW = Color.rgb(224,64,251);

    public final static int IMPORTANCE_COLOR_LOW = Color.rgb(204, 255, 144);
    public final static int IMPORTANCE_COLOR_MIDDLE = Color.rgb(178, 255, 89);
    public final static int IMPORTANCE_COLOR_HIGH = Color.rgb(118, 255, 3);

    public interface OnSchChangeListener{
        public void onDelete(long s[]);
        public void onUpdate(Schedule sch);
    }

    public static class Schedule{
        String schId;
        String time;
        String content;
        String mname;
        int importance;
        String areaCat;
        ScheduleList.Viewholder holder;

        public Schedule(){
        }

        public Schedule(String schId, String time, String content, String mname, int importance, String areaCat){
            this.schId = schId;
            this.time = time;
            this.content = content;
            this.mname = mname;
            this.importance = importance;
            this.areaCat = areaCat;
        }

        public void setHolder(ScheduleList.Viewholder holder) {
            this.holder = holder;
        }

        public String getSchId() {
            return schId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getMname() {
            return mname;
        }

        public void setMname(String mname) {
            this.mname = mname;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getImportance() {
            return importance;
        }

        public void setImportance(int importance) {
            this.importance = importance;
        }

        public String getAreaCat() {
            return areaCat;
        }

        public String toString() {
            String returnstr = schId+" "
                    + time +" "
                    + content+" "
                    + mname+" "
                    + importance+" "
                    + areaCat;
            return returnstr;
        }
    }
    public static class ScheduleList extends ArrayAdapter<Schedule> {

        LayoutInflater popupinflater;

        OnSchChangeListener change;
        Context context;
        public ScheduleList(Context context, ArrayList<Schedule> list, OnSchChangeListener change){
            super(context, 0, list);
            this.change = change;
            popupinflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.context = context;
        }

        class Viewholder{
            TextView txtDate;
            TextView txtTitle;
            TextView txtContent;
            ImageButton btnSchDelete;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = null;
            if(convertView !=null){
                view = convertView;
            }else{
                view = popupinflater.inflate(R.layout.my_list_schdule_item, null);
            }
            // 자료를 받는다.
            final Schedule data = this.getItem(position);

            //아래 버튼이벤트/다이얼로그 이벤트에서 해당 View의 데이터를 식별하기위해 쓸 수 있는 방법
            //1. ArrayAdapter.getItem(position) 외에는 현재 생각나는 방법이 없다.
            //2. 그래서 View의 setTag메소드를 이용해서 view를 클릭했을때 넘겨지는 view객체를 통해 Schedule객체나 Schedule.schId 속성을 태그로 지정해 호출할 수 있도록한다.
            //데이터호출의 편의를 위해 Schedule 객체를 Tag로 지정.
            view.setTag(data);
            final Viewholder holder = new Viewholder();
            data.setHolder(holder);


            holder.txtDate = (TextView)view.findViewById(R.id.schDatetime);
            holder.txtTitle = (TextView)view.findViewById(R.id.schTitle);
            holder.txtContent = (TextView)view.findViewById(R.id.schContent);

            holder.btnSchDelete = (ImageButton)view.findViewById(R.id.btnSchDelete);

            holder.txtDate.setText(data.getTime());
            holder.txtTitle.setText(data.getMname());
            holder.txtContent.setText(data.getContent());


            //중요도에 따른 배경색깔 설정
            switch(data.getImportance()){
                case 1:
                case 2:
                    view.setBackgroundColor(IMPORTANCE_COLOR_LOW);
                    break;
                case 3:
                    view.setBackgroundColor(IMPORTANCE_COLOR_MIDDLE);
                    break;
                case 4:
                    view.setBackgroundColor(IMPORTANCE_COLOR_HIGH);
                    break;
                case 6:
                    //크루 일정, 삭제버튼을 없앤다.
                    view.setBackgroundColor(IMPORTANCE_COLOR_CREW);
                    break;
            }

            //스케쥴삭제 이벤트 할당
            holder.btnSchDelete.setOnClickListener(new View.OnClickListener(){
                Schedule sch;
                public void onClick(View v){
                    sch = (Schedule)((View)v.getParent().getParent()).getTag();

                    Log.e("TEST DELETE SCH", sch.getSchId());
                    if(sch.getImportance() != 6) {
                        long[] s = Util.user.deleteSch(sch);

                        if ((s != null) && s[0] == 1) {
                            ScheduleList.this.remove(sch);
                            change.onDelete(s);
                        } else {
                            Toast.makeText(getContext(), "스케쥴삭제에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }else{

                    }

                }
            });

            //현재날짜를 얻어 서버에 날짜데이터를 넘겨주자.

            view.setOnClickListener(new View.OnClickListener(){
                Schedule sch;

                public void onClick(View v) {
                    sch = (Schedule) v.getTag();
                    Viewholder holder = sch.holder;
                    Log.e(TAG, "onClick: "+sch.getImportance() );
                    if (sch.getImportance() != 6) {

                        //dialog띄울 layout View
                        View popupview = (View) View.inflate(getContext(), R.layout.my_popup_schedule, null);
                        TextView contentView = (TextView) popupview.findViewById(R.id.viewContent);
                        TextView popDatetime = (TextView) popupview.findViewById(R.id.popDatetime);
                        contentView.setText(sch.getContent());
                        popDatetime.setText(sch.getTime());

                        AlertDialog.Builder schDialog = new AlertDialog.Builder(getContext());


                        schDialog.setTitle(holder.txtTitle.getText())
                                //.setIcon()
                                .setView(popupview)
                                .setPositiveButton("수정", new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        change.onUpdate(sch);
                                    }
                                })
                                .setNegativeButton("취소", null)
                                .show();
                    }else{
                        Intent intent = new Intent(context, CrewDetailActivity.class);
                        intent.putExtra("planNum", Integer.parseInt(sch.getSchId()))
                              .putExtra("submit", "관리");
                        context.startActivity(intent);
                    }
                }
            });

            return view;
        }
    }


    public ArrayList<ArrayList> getCalendarInitData(Calendar day){
        ArrayList<ArrayList> initValues = new ArrayList();
        String uri = "InitCalendar.jsp";
        /*
        *1년단위의 캘린더의 기념일을 표시해놓는다
            =>priv,next를 누를때 년도가 변하는지 체크해서 년도가 변하면 서버에 요청한다.
        한달단위의 기념일만 표시한다
            =>priv,next를 누를때마다 해당날짜를 서버에 넘겨서 받아온다.
        전후 6개월단위만 표시한다
            =>priv,next를 누를때마다 -3미만인지,
        별로중요한거아냐

         */
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        ArrayList<ArrayList> schOfYear = new ArrayList<ArrayList>();
        ArrayList<Schedule> daySchedule = new ArrayList<Schedule>();

        try {
            JSONObject response = new JSONObject(Util.getAnt().execute(uri, "date:" + day.getTimeInMillis(), "id:" + Util.user.getId()).get());

            JSONArray schDays = response.getJSONArray("schOfYear");

            ArrayList<CalendarDay> importanceLow = new ArrayList(); // 중요도 1,2
            ArrayList<CalendarDay> importanceMiddle = new ArrayList(); // 중요도 3
            ArrayList<CalendarDay> importanceHigh = new ArrayList(); //중요도 4
            ArrayList<CalendarDay> importanceCrew = new ArrayList(); //중요도 crew

            for(int i = 0; i<schDays.length(); i++){
                JSONObject jsonday = schDays.getJSONObject(i);
                Date date = format.parse(jsonday.getString("date"));
                CalendarDay schDay = CalendarDay.from(date);
                Integer importance = jsonday.getInt("importance");
                switch(importance){
                    case 1:
                    case 2:
                        importanceLow.add(schDay);
                        break;
                    case 3:
                        importanceMiddle.add(schDay);
                        break;
                    case 4 :
                        importanceHigh.add(schDay);
                        break;
                    case 6:
                        importanceCrew.add(schDay);
                        break;
                }

            }

            schOfYear.add(importanceLow);
            schOfYear.add(importanceMiddle);
            schOfYear.add(importanceHigh);
            schOfYear.add(importanceCrew);

            JSONArray daySch = response.getJSONArray("daySchedule");

            for(int j = 0; j<daySch.length(); j++){
                JSONObject sch = (JSONObject)daySch.get(j);

                String time = sch.getString("time");
                String content = sch.getString("content");
                String mname = sch.getString("mname");
                int colorcode = sch.getInt("importance");
                String schId = sch.getString("schId");
                String areaCat = sch.getString("areaCat");
                daySchedule.add(j, new Schedule(schId,time, content, mname, colorcode,areaCat));
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        initValues.add(0,schOfYear);
        initValues.add(1,daySchedule);

        return initValues;
    }

    public ArrayList<Schedule> getDaySchedule(long datetime){
        ArrayList<Schedule> daySchedule = new ArrayList();
        String uri = "DaySchedule.jsp";
        try {
            JSONObject response = new JSONObject(Util.getAnt().execute(uri, "date:" + datetime, "id:"+Util.user.getId()).get());

            JSONArray daySch = response.getJSONArray("daySchedule");

            for(int j = 0; j<daySch.length(); j++){
                JSONObject sch = (JSONObject)daySch.get(j);
                String time = sch.getString("time");
                String content = sch.getString("content");
                String mname = sch.getString("mname");
                int colorcode = sch.getInt("importance");
                String schId = sch.getString("schId");
                String areaCat = sch.getString("areaCat");
                daySchedule.add(j, new Schedule(schId,time, content, mname, colorcode,areaCat));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return daySchedule;
    }

    public long[] updateSch(Schedule sch){
        String uri = "UpdateSchedule.jsp";
        try {

            StringBuilder params = new StringBuilder();
            params.append(sch.getSchId()!=null ? "schId:"+sch.getSchId() : "");
            params.append("mName:"+sch.getMname());
            params.append("importance:"+sch.getImportance());
            params.append("time:"+sch.getTime());
            params.append("content:"+sch.getContent());

            String resStr ;
                    if(sch.getSchId()==null){
                        resStr = Util.getAnt().execute(uri, "id:"+Util.user.getId(),"mtName:"+sch.getMname(),"importance:"+sch.getImportance(),"time:"+sch.getTime(),"content:"+sch.getContent()).get();
                    }else{
                        resStr = Util.getAnt().execute(uri, "schId:"+sch.getSchId(),"mtName:"+sch.getMname(),"importance:"+sch.getImportance(),"time:"+sch.getTime(),"content:"+sch.getContent()).get();
                    }


            JSONObject response = new JSONObject(resStr);
            long[] s = new long[2];
            s[0]=response.getInt("status");
            s[1]=response.getLong("date");
            return s;

        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public long[] deleteSch(Schedule sch){
        String uri = "DeleteSchedule.jsp";
        try {

            JSONObject response = new JSONObject(Util.getAnt().execute(uri, "schId:"+sch.getSchId(), "time:"+sch.getTime()).get());
            long[] s = new long[2];
            s[0]=response.getInt("status");
            s[1]=response.getLong("date");
            return s;

        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    // -----------------------------운동 기록하기, 리스트 불러오기 관련 메소드---------------------------------
    static final int[][] timezone =
            {{1,6},{6,10},{10,17},{17,21},{21,25}};
    static final int DAWN=0, MORN=1, DAYT=2, EVEN=3, NIGT=4;
    static final String[] times = {"새벽","아침","낮","저녁","밤"};

    //운동기록 아이템 객체
    public static class ExerciseItem{
        String id;

        String ex_pic;
        String ex_day;
        long ex_start;
        long ex_end; //나중에 timezone 구분할때 쓰는 필드.
        String ex_location;

        int step, calories, distance;

        String ex_time;

        String time;

        public ExerciseItem(String ex_id,String ex_pic, String ex_day, long ex_start, long ex_end, String ex_location, int step, int calories, int distance, String ex_time) {
            this.id = ex_id;
            this.ex_pic = ex_pic;
            this.ex_day = ex_day;
            this.ex_start = ex_start;
            this.ex_end = ex_end;
            this.ex_location = ex_location;
            this.step = step;
            this.calories = calories;
            this.distance = distance;
            this.ex_time = ex_time;
            setTimezone();
        }

        void setTimezone(){
            //1. 시작시간과 끝시간의 해당 타임존 찾기
            Calendar c_st = Calendar.getInstance();
            Calendar c_ed = Calendar.getInstance();

            c_st.setTimeInMillis(ex_start);
            c_ed.setTimeInMillis(ex_end);

            //현재는 시간으로만, 나중에 시간이되면 분으로 더 정확하게.
            int st_hour = c_st.get(Calendar.HOUR_OF_DAY);
            int ed_hour = c_ed.get(Calendar.HOUR_OF_DAY);

            if(st_hour == 0 ){
                st_hour = 24;
            }
            if(ed_hour == 0){
                ed_hour = 24;
            }

            int st_idx = 0;
            int ed_idx = 0;

            for(int i=DAWN; i<=NIGT; i++){
                if(st_hour>=timezone[i][0] && st_hour<timezone[i][1]){
                    st_idx = i;
                }
                if(ed_hour>=timezone[i][0] && ed_hour<timezone[i][1]){
                    ed_idx = i;
                }
            }

            //시간대가 같으면, 그 시간대로 설정
            if(st_idx == ed_idx){
                time = times[st_idx];
                return;
            }else{
                //시간대가 다르면, 각 시간대에서 얼만큼 차지했는지 확인하고, 더많은 시간을 보낸 시간대를 설정.
                int st_rate = timezone[st_idx][1]-st_hour;
                int ed_rate = timezone[ed_idx][1]-ed_hour;

                if(st_rate >= ed_rate){
                    time = times[st_idx];
                }else{
                    time = times[ed_idx];
                }
            }

        }

        public String getEx_Id(){return id;}

        public String getEx_pic() {
            return ex_pic;
        }

        public String getEx_day() {
            return ex_day;
        }

        public long getEx_start() {
            return ex_start;
        }

        public long getEx_end() {
            return ex_end;
        }

        public String getEx_location() {
            return ex_location;
        }

        public int getStep() {
            return step;
        }

        public int getCalories() {
            return calories;
        }

        public int getDistance() {
            return distance;
        }

        public String getTime() {
            return time;
        }

        public String getEx_time(){return ex_time;}
    }

    //기록하기 관련
    public String insertExData(ExerciseItem result){
        String uri = "SaveHealth.jsp";
        String message = null;
        try {
            Log.e("dfdfdf", "userid :"+Util.user.getId() );
            message = new AndroidToServer()
                    .execute(uri,
                            "id:"+Util.user.getId(), // 합칠때 Util.user.getId()로 수정
                            "exer_time:"+result.getEx_time(),
                            "startTime:"+result.getEx_start(),
                            "endTime:"+result.getEx_end(),
                            "step:"+result.getStep(),
                            "calories:"+result.getCalories(),
                            "distance:"+result.getDistance(),
                            "location:"+result.getEx_location(),
                            "picpath:"+result.getEx_pic()
                    )
                    .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return message;
    }


    //리스트 불러오기 관련
    public ArrayList<ExerciseItem> getExerciseItems(long stTime, long endTime){
        ArrayList<ExerciseItem> itemlist = new ArrayList();

        String uri = "SearchExerciseItems.jsp";
        try {
            String message = Util.getAnt()
                    .execute(uri,
                            "id:" + Util.user.getId(),
                            "stTime:" + stTime,
                            "endTime:" + endTime)
                    .get();
            JSONObject response = new JSONObject(message);
            JSONArray ja_items = response.getJSONArray("items");

            for(int i=0; i<ja_items.length(); i++){
                JSONObject item = (JSONObject)ja_items.get(i);
                ExerciseItem ex_item = new ExerciseItem(
                        item.getString("ex_id"),
                        item.getString("ex_pic"),
                        item.getString("ex_day"),
                        item.getLong("ex_start"),
                        item.getLong("ex_end"),
                        item.getString("ex_location"),
                        item.getInt("step"),
                        item.getInt("calories"),
                        item.getInt("distance"),
                        item.getString("ex_time")
                );
                itemlist.add(ex_item);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return itemlist;
    }

    public ArrayList<ExerciseItem> getExerciseItems(int rownum) {
        ArrayList<ExerciseItem> itemlist = new ArrayList();

        String uri = "ExerciseHistory.jsp";
        try {
            String message = Util.getAnt()
                    .execute(uri,
                            "id:" + Util.user.getId(),
                            "row:"+ rownum)
                    .get();
            JSONObject response = new JSONObject(message);
            JSONArray ja_items = response.getJSONArray("items");

            for(int i=0; i<ja_items.length(); i++){
                JSONObject item = (JSONObject)ja_items.get(i);
                ExerciseItem ex_item = new ExerciseItem(
                        item.getString("ex_id"),
                        item.getString("ex_pic"),
                        item.getString("ex_day"),
                        item.getLong("ex_start"),
                        item.getLong("ex_end"),
                        item.getString("ex_location"),
                        item.getInt("step"),
                        item.getInt("calories"),
                        item.getInt("distance"),
                        item.getString("ex_time")
                );
                itemlist.add(ex_item);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return itemlist;

    }

    //삭제하기 관련
    public String deleteExData(ExerciseItem item){
        String uri = "DeleteExerciseData.jsp";
        String message = null;
        try {
            message = Util.getAnt().execute(
                    uri,
                    "ex_id:"+item.getEx_Id()
            ).get();
            /*
            * 상태 받고 상태에따라 사용자에게 알리기위해 데이터 분석하는코드.
            * */
        }catch(Exception e){

        }
        return message;
    }




}
