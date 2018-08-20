package com.cookandroid.mom.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.mom.R;
import com.cookandroid.mom.community.club.InsertClubActivity;
import com.cookandroid.mom.home.ManageTodayFragment1;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by S401 on 2017-06-09.
 */

public class MountainManager {
    String TAG = "MountainManager";

    private MountainManager() {
        setAreaList();
    }

    static MountainManager getInstance() {
        return new MountainManager();
    }

    //-------------------------오늘의 산 관련 클래스 및 메소드 -------------------------

    public static class TodayMountain {
        int todayNum;
        String mtNum;
        String content;
        String event;
        String picture;
        String title;
        String date;
        long date_long;
        boolean isshow;
        String mtArea;
        TodayMtList.Viewholder viewholder;

        public boolean isshow() {
            return isshow;
        }

        public TodayMountain(int todayNum, String mtNum, String content, String event, String picture, String title, long date, boolean isshow, String mtArea) {
            this.todayNum = todayNum;
            this.mtNum = mtNum;
            this.content = content;
            this.event = event;
            this.picture = picture;

            this.title = title;
            this.date_long = date;
            this.date = DataStringFormat.y_mon_d.format(date);
            this.isshow = isshow;

            this.mtArea = mtArea;
        }

        public TodayMountain() {
        }

        ;

        public String getContent() {
            return content;
        }

        public String getDate() {
            return date;
        }

        public String getTitle() {
            return title;
        }

        public int getTodayNum() {
            return todayNum;
        }

        public String getMtNum() {
            return mtNum;
        }

        public String getEvent() {
            return event;
        }

        public String getPicture() {
            return picture;
        }

        public long getDate_long() {
            return date_long;
        }

        public String getMtArea() {
            return mtArea;
        }

        public static void showTodayMtUpdateDialog(final UsingCameraFragment fragment, final Context context, View view) {
            int situation = Util.INSERT;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialog_view = inflater.inflate(R.layout.home_manage_today_insert_update, null);
            View v = dialog_view;

            final int toMtNum;
            final TextView date = (TextView) v.findViewById(R.id.today_in_date);
            final Switch isshow = (Switch) v.findViewById(R.id.today_in_isshow);
            final Spinner spinArea = (Spinner) v.findViewById(R.id.today_in_spinArea);
            final Spinner spinMts = (Spinner) v.findViewById(R.id.today_in_spinMts);
            final ImageView imgToday = (ImageView) v.findViewById(R.id.today_in_imgToday);
            final TextView filename = (TextView) v.findViewById(R.id.today_in_filename);
            final Button btnPicture = (Button) v.findViewById(R.id.today_in_btnPicture);
            final EditText today_title, event, today_content;
            today_title = (EditText) v.findViewById(R.id.today_in_title);
            event = (EditText) v.findViewById(R.id.today_in_event);
            today_content = (EditText) v.findViewById(R.id.today_in_content);

            //수정상황/삽입상황에 상관없이 초기화해야하는 View 작업.
            spinArea.setAdapter(Util.mtManager.getAreaAdapter(context));
            spinMts.setAdapter(Util.mtManager.getMtAdapter(context, Util.mtManager.category[0]));

            spinArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    String area = Util.mtManager.category[position];
                    spinMts.setAdapter(Util.mtManager.getMtAdapter(context, area));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }

            });
            date.setText(DataStringFormat.y_mon_d.format(Calendar.getInstance().getTimeInMillis()));

            fragment.displayImage = imgToday;
            fragment.fileName = filename;

            btnPicture.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    fragment.getAlbum();
                }
            });

            String btnPositive = "등록";

            int toMtNum_temp = -1;


            //리스트에 있는 아이템을 터치했을때 : 수정상황 => 기존 데이터 할당
            if (view != null) {
                Toast.makeText(context, "수정", Toast.LENGTH_SHORT).show();
                btnPositive = "수정";
                situation = Util.UPDATE;
                TodayMountain data = (TodayMountain) view.getTag();
                TodayMtList.Viewholder holder = data.viewholder;
                try {
                    //리스트에 있는 이미지 할당(서버통신을 다시 안하는데 의미를 둠.)
                    imgToday.setImageBitmap(holder.picture.getDrawingCache());
                    //나머지는 뭐... 다 Data에 있는거니까.
                    date.setText(data.getDate());
                    isshow.setChecked(data.isshow);
                    toMtNum_temp = data.getTodayNum();
                    spinArea.setSelection(
                            Util.mtManager.getCatindex(data.getMtArea()));
                    spinMts.setAdapter(Util.mtManager.getMtAdapter(context, (String)spinArea.getSelectedItem()));
                    spinMts.setSelection(Util.mtManager.getMtPosition(data.getMtArea(),data.getMtNum()));
                    Toast.makeText(context,data.getPicture(), Toast.LENGTH_SHORT).show();
                    filename.setText(data.getPicture());
                    today_title.setText(data.getTitle());
                    today_content.setText(data.getContent());
                    event.setText(data.getEvent());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

                Toast.makeText(context, "삽입", Toast.LENGTH_SHORT).show();
                // 프래그먼트의 +버튼을 눌렀을때 : 삽입상황 => 데이터없음.
                toMtNum_temp = -1;
            }


            //------다이얼로그 띄우기-------
            toMtNum = toMtNum_temp;
            final int s = situation;
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("오늘의 산")
                    .setView(dialog_view)
                    .setPositiveButton(btnPositive, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String st[] = filename.getText().toString().split("/");
                            String filenm  = st[st.length-1];
                            TodayMountain toMt = new TodayMountain(
                                    toMtNum,
                                    Util.mtManager.getMtNum((String)spinArea.getSelectedItem(), spinMts.getSelectedItemPosition()),
                                    today_content.getText().toString(),
                                    event.getText().toString(),
                                    filenm,
                                    today_title.getText().toString(),
                                    Calendar.getInstance().getTimeInMillis(),
                                    isshow.isChecked(),
                                    null
                            );
                            switch (s) {
                                case Util.INSERT: {
                                    if (fragment.isSelected) {
                                        Util.getITS().execute(filename.getText().toString());
                                    } else {
                                        Toast.makeText(context, "이미지를 선택해주세요", Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                    Util.mtManager.insertTodayMt(toMt);
                                    break;
                                }
                                case Util.UPDATE: {
                                    Util.mtManager.updateTodayMt(toMt, fragment.isSelected);


                                    if (fragment.isSelected) {
                                        Util.getITS().execute(filename.getText().toString());
                                    }

                                    break;
                                }
                            }
                            Util.manage.fragment1.refresh();
                        }
                    })
                    .setNegativeButton("취소", null)
                    .show();

        }


    }

    public static class TodayMtList extends ArrayAdapter<TodayMountain> {
        LayoutInflater inflater;
        Context context;

        User.OnSchChangeListener change;

        public TodayMtList(Context context, ArrayList<TodayMountain> list) {
            super(context, 0, list);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = null;
            if (convertView != null) {
                view = convertView;
            } else {
                view = inflater.inflate(R.layout.home_manage_today_item, null);
            }
            // 자료를 받는다.
            final TodayMountain data = this.getItem(position);
            final Viewholder holder = new Viewholder();
            data.viewholder = holder;
            view.setTag(data);

            // View 객체 할당
            holder.title = (TextView) view.findViewById(R.id.today_item_title);
            holder.date = (TextView) view.findViewById(R.id.today_item_date);
            holder.content = (TextView) view.findViewById(R.id.today_item_content);
            holder.picture = (ImageView) view.findViewById(R.id.today_item_picture);
            holder.isshow = (CheckBox) view.findViewById(R.id.today_item_isshow);

            // View 객체의 값 할당
            holder.title.setText(data.getTitle());
            holder.date.setText(data.getDate());
            String content_txt = data.getContent();
            if (content_txt.length() > 50) {
                content_txt = content_txt.substring(0, 50) + "...";
            }
            holder.content.setText(content_txt);

            Bitmap image = null;
            try {
                image = Util.getIFS().execute(data.getPicture()).get();
            } catch (Exception e) {
                image = BitmapFactory.decodeResource(context.getResources(), R.drawable.d);
                e.printStackTrace();
            }
            holder.picture.setImageBitmap(image);
            holder.isshow.setChecked(data.isshow());


            //isshow 상태변화 이벤트 할당
            holder.isshow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                         @Override
                                                         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                             Util.mtManager.isShowUpdate(data.getTodayNum(), isChecked);
                                                         }
                                                     }
            );


            //리스트 아이템에 클릭이벤트 할당
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    TodayMountain.showTodayMtUpdateDialog(Util.manage.fragment1, context, view);
                }
            });

            return view;
        }

        static class Viewholder {
            TextView title;
            TextView date;
            TextView content;
            ImageView picture;
            CheckBox isshow;
        }
    }


    public ArrayList<TodayMountain> getTodayHistory() {
        ArrayList<TodayMountain> dataList = new ArrayList();
        String uri = "TodayMountainHistory.jsp";
        try {
            String message = Util.getAnt().execute(uri).get();
            JSONObject response = new JSONObject(message);
            JSONArray jarr = response.getJSONArray("history");
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject item = jarr.getJSONObject(i);
                TodayMountain toMt = new TodayMountain(
                        item.getInt("todayNum"),
                        item.getString("mtNum"),
                        item.getString("content"),
                        item.getString("event"),
                        item.getString("picture"),
                        item.getString("title"),
                        item.getLong("date"),
                        item.getBoolean("isshow"),
                        item.getString("mtArea")
                );
                dataList.add(toMt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return dataList;
    }

    public void insertTodayMt(TodayMountain data) {
        String url = "InsertTodayMt.jsp";
        JSONObject response = null;
        try {
            String message = Util.getAnt().execute(
                    url,
                    "mtNum:" + data.getMtNum(),
                    "content:" + data.getContent(),
                    "event:" + data.getEvent(),
                    "picture:" + data.getPicture(),
                    "title:" + data.getTitle(),
                    "date:" + data.getDate_long(),
                    "isshow:" + data.isshow()
            ).get();

            response = new JSONObject(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTodayMt(TodayMountain data, boolean isChgPic) {
        String url = "UpdateTodayMt.jsp";
        JSONObject response = null;
        try {
            String message = Util.getAnt().execute(
                    url,
                    "todayNum:" + data.getTodayNum(),
                    "mtNum:" + data.getMtNum(),
                    "content:" + data.getContent(),
                    "event:" + data.getEvent(),
                    "picture:" + data.getPicture(),
                    "title:" + data.getTitle(),
                    "date:" + data.getDate_long(),
                    "isshow:" + data.isshow(),
                    "isChgPic:" + isChgPic
            ).get();

            response = new JSONObject(message);
        } catch (Exception e) {
                e.printStackTrace();
        }
    }

    public void isShowUpdate(int todayNum, boolean isshow) {
        String url = "TodayMtIsShowUpdate.jsp";
        JSONObject response = null;
        try {
            String message = Util.getAnt().execute(
                    url,
                    "todayNum:" + todayNum,
                    "isshow:" + isshow
            ).get();

            response = new JSONObject(message);
        } catch (Exception e) {

        }
    }

    public ArrayList<TodayMountain> getTodayMt() {
        ArrayList<TodayMountain> list = new ArrayList();
        TodayMountain toMt;
        String uri = "TodayMountain.jsp";

        JSONObject requestJSON = null;

        try {
            requestJSON = new JSONObject(Util.getAnt().execute(uri).get());
            JSONArray jarr = requestJSON.getJSONArray("toMts");

            for(int i=0; i<jarr.length(); i++){
                JSONObject mtobj = jarr.getJSONObject(i);
                TodayMountain mt = new TodayMountain(
                        mtobj.getInt("todayNum"),
                        mtobj.getString("mtNum"),
                        mtobj.getString("content"),
                        mtobj.getString("event"),
                        mtobj.getString("picture"),
                        mtobj.getString("title"),
                        mtobj.getLong("date"),
                        mtobj.getBoolean("isshow"),
                        mtobj.getString("mtArea")
                );
                list.add(mt);

            }

        } catch (Exception e) {
            Log.e("getTodayMt", e.getMessage());
        }

        return list;
    }

        //------------------------지역 리스트 관련 메소드 ----------------------------------

    public String[] category;
    public HashMap<String, String> cat_areasStr;
    public HashMap<String, ArrayList> cat_areaList;
    public HashMap<String, ArrayList<MtNameNum>> cat_mtList;

    public ArrayList<ArrayList> all_area;


    class MtNameNum{
        String name;
        String num;
        int cat_index;

    }

    void setAreaList() {
        String url = "AreaCategory.jsp";
        int position;
        cat_areasStr = new HashMap();
        cat_areaList = new HashMap();
        cat_mtList = new HashMap();

        all_area = new ArrayList<ArrayList>();

        try {
            JSONObject response = new JSONObject(Util.getAnt().execute(url).get());
            JSONArray arr = response.getJSONArray("cats");
            JSONArray mts = response.getJSONArray("cat_Mts");

            category = new String[arr.length()];

            for (int i = 0; i < arr.length(); i++) {
                JSONObject cat_areas = arr.getJSONObject(i);
                String cat = cat_areas.getString("category");
                category[i] = cat;
                Log.e("area output",  i+":"+cat);
                cat_areasStr.put(cat, cat_areas.getString("areas"));

                all_area.add(i, new ArrayList<MtNameNum>());
            }


            for(int j=0; j<mts.length(); j++){
                MtNameNum mt = new MtNameNum();
                JSONObject cat_Mt = mts.getJSONObject(j);
                mt.cat_index = cat_Mt.getInt("index");
                mt.name = cat_Mt.getString("mName");
                mt.num = cat_Mt.getString("mNum");

                all_area.get(mt.cat_index).add(mt);
            }

            for (int i = 0; i < category.length; i++) {
                String areasStr = cat_areasStr.get(category[i]);
                String[] tmp_arr = areasStr.split(",");
                ArrayList<String> tmp_al = new ArrayList();
                for (int j = 0; j < tmp_arr.length; j++) {
                    tmp_al.add(j, tmp_arr[j]);
                }
                cat_areaList.put(category[i], tmp_al);

                cat_mtList.put(category[i], all_area.get(i));
                Log.e(TAG, "setAreaList: "+category[i]+" "+all_area.get(i).toString());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    ArrayAdapter<String> getAreaAdapter(Context context){
        return new ArrayAdapter<String>(context, R.layout.spinner_item, Util.mtManager.category);
    }
    ArrayAdapter<String> getMtAdapter(Context context, String cat){
        ArrayList<MtNameNum> a = Util.mtManager.cat_mtList.get(cat);
        String[] mts = new String[a.size()];
        int i=0;
        for(MtNameNum m : a){
            mts[i++] = m.name;
        }
        return new ArrayAdapter<String>(context, R.layout.spinner_item, mts);
    }
    int getCatindex(String cat){
        int i=0;
        for(int j=0; j<category.length; j++){
            if(cat.equals(category[j])){
                i=j;
                break;
            }
        }
        return i;
    }
    int getMtPosition(String cat, String mtNum){
        int i=0;
        Log.e("cat_mtList check : ", cat_mtList.get(cat)==null?"null":"not null");
        for(MtNameNum m : cat_mtList.get(cat)){
            if(m.num.equals(mtNum)){
                break;
            }
            i++;
        }
        return i;
    }
    String getMtNum(String cat, int position){
        return ((MtNameNum)cat_mtList.get(cat).get(position)).num;
    }

    SpinnerAdapter mtSpinner =  new SpinnerAdapter() {
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return null;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    };

    //-----------------------산정보 관련 클래스 및 메소드 -----------------------------

    public static class Mountain {
        public String mtCode;
        public String mtName;
        public String mtAddress;
        public int mtHeight;
        public String mtPhoto;
        public String mtContent;
        public String mtLatitude;
        public String mtHardness;

        public Mountain(String m, String n, String a, int h, String p, String c, String l, String har) {
            mtCode = m;
            mtName = n;
            mtAddress = a;
            mtHeight = h;
            mtPhoto = p;
            mtContent = c;
            mtLatitude = l;
            mtHardness = har;
        }
    }

    public class Facility {
        public String mtName;
        public String facilName;
        public String content;

        public Facility(String n, String f, String c) {
            mtName = n;
            facilName = f;
            content = c;
        }
    }


    public String[][] getAreaMts(int cnt, String... params) throws Exception {
        String[][] mtInfos = null;
        String uri = "MountainInformationList.jsp";
        JSONObject requestJSON = null;
        if (params.length == 0) {
            requestJSON = new JSONObject(Util.getAnt().execute(uri, "cnt:" + cnt).get());
        } else {
            requestJSON = new JSONObject(Util.getAnt().execute(uri, "cnt:" + cnt, "area:" + params[0]).get());
        }
        JSONArray jarr = requestJSON.getJSONArray("item");
        mtInfos = new String[5][jarr.length()];

        for (int i = 0; i < jarr.length(); i++) {
            JSONObject json = jarr.getJSONObject(i);
            mtInfos[0][i] = json.getString("area");
            mtInfos[1][i] = json.getString("mntnm");
            mtInfos[2][i] = json.getString("mntheight");
            mtInfos[3][i] = json.getString("mPhoto");
            mtInfos[4][i] = json.getString("mntncd");
        }

        return mtInfos;
    }

    public String[][] getMtCourses(String mNum) {
        String[][] courseInfo = null;
        String uri = "Courses.jsp";
        JSONObject requestJSON = null;
        try {
            requestJSON = new JSONObject(Util.getAnt().execute(uri, "mNum:" + mNum).get());
            JSONArray jarr = requestJSON.getJSONArray("item");
            Log.e("JSONArray length_Course", jarr.length() + "");
            courseInfo = new String[11][jarr.length()];
            Log.e("jarr", jarr.toString());
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject json = jarr.getJSONObject(i);
                courseInfo[0][i] = json.getString("mNum");
                courseInfo[1][i] = json.getString("mCourseNum");
                courseInfo[2][i] = json.getString("mLatitude");
                courseInfo[3][i] = json.getString("mLongitude");
                courseInfo[4][i] = json.getString("emdCd");
                courseInfo[5][i] = json.getString("sec_len");
                courseInfo[6][i] = json.getString("up_min");
                courseInfo[7][i] = json.getString("down_min");
                courseInfo[8][i] = json.getString("start_z");
                courseInfo[9][i] = json.getString("end_z");
                courseInfo[10][i] = json.getString("cat_nam");
                //Log.e("courseInf[0]", courseInfo[0][i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courseInfo;
    }

    public ArrayList<Facility> getFacilitys(Mountain mountain) {
        ArrayList<Facility> result = new ArrayList<Facility>();
        JSONObject requestJSON = null;
        try {
            String uri = "MountainFacils.jsp";
            requestJSON = new JSONObject(Util.getAnt().execute(uri, "mNum:" + mountain.mtCode).get());
            JSONArray jarr = requestJSON.getJSONArray("facilinfos");

            for (int i = 0; i < jarr.length(); i++) {
                JSONObject facilinfo = jarr.getJSONObject(i);
                Facility facil = new Facility(facilinfo.getString("mtName"), facilinfo.getString("facilName"), facilinfo.getString("content"));

                result.add(facil);

            }
        } catch (Exception e) {

        }
        return result;


    }

    //산 좌표
    public String[][] getMtPoint() throws Exception {
        String[][] mtPoint = null;
        String uri = "MountainPoint.jsp";
        JSONObject requestJSON = null;
        requestJSON = new JSONObject(Util.getAnt().execute(uri).get());

        JSONArray jarr = requestJSON.getJSONArray("item");
        mtPoint = new String[4][jarr.length()];

        for (int i = 0; i < jarr.length(); i++) {
            JSONObject json = jarr.getJSONObject(i);
            mtPoint[0][i] = json.optString("mName", "text on no value");
            mtPoint[1][i] = json.optString("mLatitude", "text on no value");
            mtPoint[2][i] = json.optString("mLongitude", "text on no value");
            mtPoint[3][i] = json.optString("mNum", "text on no value");
        }

        return mtPoint;
    }


    public Mountain getMtDetail(String mtName) {
        JSONObject requestJSON = null;
        try {
            String uri = "MountainDetail.jsp";
            requestJSON = new JSONObject(Util.getAnt().execute(uri, "mtName:" + mtName).get());

            String mtCode = requestJSON.getString("mtCode");
            String mName = requestJSON.getString("mtName");
            String mAddress = requestJSON.getString("mtAddress");
            int mHeight = requestJSON.getInt("mtHeight");
            String mPhoto = requestJSON.getString("mtPhoto");
            String mContent = requestJSON.getString("mtContent");
            String mtLatitude = requestJSON.getString("mtLatitude");
            String mtHardness = requestJSON.getString("mtHardness");
            Mountain result = new Mountain(mtCode, mName, mAddress, mHeight, mPhoto, mContent, mtLatitude, mtHardness);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //아이디와 산 이름으로 산 사진 가져오기
    public String[][] getPhoto(String mName) throws Exception{
        Log.e("getPhoto 에서 mName :",mName);
        String[][] photo = null;
        String uri = "Photo.jsp";
        JSONObject requestJSON =null;
        requestJSON = new JSONObject(Util.getAnt().execute(uri, "UserID:" + Util.user.getId(),"mName:" + mName).get());


        JSONArray jarr = requestJSON.getJSONArray("item");
        photo = new String[3][jarr.length()];
        Log.e("jarr.length : ", ""+jarr.length() );

        for(int i=0; i<jarr.length(); i++){
            JSONObject json = jarr.getJSONObject(i);
            photo[0][i] = json.getString("photo");
            photo[1][i] = json.getString("mLatitude");
            photo[2][i] = json.getString("mLongitude");
            Log.e("photo[i] : ", photo[0][i]);
        }
        //Log.e("photo[0] : ", photo[0][0]);
        return photo;
    }
    //---------------------날씨정보 관련 클래스 및 메소드-------------------------

    public class Weather {
        public String code;
        public String temperature;
        public String time;

        public Weather(String c, String t, String time) {
            code = c;
            temperature = t;
            this.time = time;
        }

        public Weather(String c, String t) {
            code = c;
            temperature = t;
        }

        public String toString() {
            return code + ":" + temperature + ":" + time;
        }

    }

    public ArrayList<Weather> getWeatehrInfos(Mountain mountain) {
        ArrayList<Weather> weatherInfos = new ArrayList<Weather>();
        String domain = "apis.skplanetx.com";
        String version = "1";
        String lon = mountain.mtHardness;
        String lat = mountain.mtLatitude;
        /* 1.정환꺼, 2. 내꺼*/
        //String appKey = "14293396-275b-39a3-8212-293b112b2dec";
        String appKey = "2ece16b8-c7a7-364e-95f2-1eb51da0d13f";
        String url = "";
        /*
        날씨정보 API domain : http://apis.skplanetx.com
request parameter
version=1
lon=127.69019444444444
lat=35.946377777777776
appKey=14293396-275b-39a3-8212-293b112b2dec

현재시간 API : weather/current/hourly
조회할 데이터

Object
   ->weather
      ->hourly
         ->index 0
             ->sky
                ->code
             ->temperature
                ->tc
             ->timeRelease
   ->result
      ->message : 성공 -> 띄우기/ 그외 -> 날씨정보를 받아올 수 없습니다.

*/
        try {
            //현재 날씨정보 추가
            url = "weather/current/hourly";
            String jsonString = Util.getAnt(domain, "").execute(url, "version:" + version, "lon:" + lon, "lat:" + lat, "appKey:" + appKey).get();
            JSONObject response = new JSONObject(jsonString);
            int resultCode = response.getJSONObject("result").getInt("code");
            if (resultCode == 9200) {
                Log.e("Weather create", "1");
                JSONObject hourly = response.getJSONObject("weather").getJSONArray("hourly").getJSONObject(0);
                JSONObject sky = hourly.getJSONObject("sky");
                String code = sky.getString("name");
                JSONObject temp = hourly.getJSONObject("temperature");
                String tc = temp.getString("tc");
                String releaseTime = hourly.getString("timeRelease");
                Weather weather = new Weather(code, tc, releaseTime);
                Log.e("Weather create", "2");
                weatherInfos.add(weather);
            } else {
                return null;
            }

            /*
기상예보 API : weather/forecast/3days

조회할 데이터
object
   ->weather
      ->forecast3days
         ->index 1 : fcst3hour
            ->sky(JOBJ)
            ->temperature(JOBJ)
         ->index 2 : timeRelease(String)

        * */

            //예보 날씨 추가
            int[] times = {4, 7, 10, 13, 16, 19, 22, 25};
            Log.e("Weather create", "3");
            url = "weather/forecast/3days";

            jsonString = Util.getAnt(domain, "").execute(url, "version:" + version, "lon:" + lon, "lat:" + lat, "appKey:" + appKey).get();
            Log.e("Weather create", "4");
            JSONObject response2 = new JSONObject(jsonString);
            Log.e("Weather create", "5");
            resultCode = response2.getJSONObject("result").getInt("code");
            if (resultCode == 9200) {
                Log.e("Weather create", "6");
                JSONObject fore = response2.getJSONObject("weather").getJSONArray("forecast3days").getJSONObject(0).getJSONObject("fcst3hour");
                JSONObject sky = fore.getJSONObject("sky");
                JSONObject temp = fore.getJSONObject("temperature");
                Log.e("Weather create", "7");
                for (int t : times) {
                    Log.e("Weather create", "8");
                    String code = sky.getString("name" + t + "hour");
                    String temper = temp.getString("temp" + t + "hour");
                    Weather weather = new Weather(code, temper);
                    weatherInfos.add(weather);
                }

                /*
                timeRelease형식 : 2017-06-20 17:00:00 => YYYY-MM-DD hh:mm:ss
                내가 필요한거
                시간에 + 4,7,10..을 한 후에 "MM-DD hh" 형식의 데이터
                이렇게 바꿔주는 작업
                * */
                SimpleDateFormat before = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat after = new SimpleDateFormat("MM월dd일 HH시");
                //시작시간을 기준으로 +해야하므로 첫번째 인자는 미리 계산해서 기준으로함.
                Date base = before.parse(weatherInfos.get(0).time);
                weatherInfos.get(0).time = after.format(base);
                int i = 1; //해당형식을 할당할인덱스
                for (int t : times) {
                    Date time = new Date(base.getTime() + (t * 60 * 60 * 1000));
                    weatherInfos.get(i).time = after.format(time);
                    i++;
                }

            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return weatherInfos;
    }

}
