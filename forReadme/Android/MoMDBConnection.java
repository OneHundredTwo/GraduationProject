package com.cookandroid.mom.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Shining on 2017-08-16.
 * 사진제목과 좌표정보를 매핑하여 앱데이터베이스에 저장하기 위해 DB와 연결을 관리할 클래스
 */
/*
execSQL : UPDATE, INSERT , DELETE
rawQuery : SELECT
 */

public class MoMDBConnection extends SQLiteOpenHelper {
    private static final String DB_NAME = "MoMDB";
    final String tbPicture = "picture";
    private Context context;

    public MoMDBConnection(Context context){

        super(context, DB_NAME, null, 1);
        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //사진정보 테이블
        db.execSQL("create table "+tbPicture+"(file_path text primary key, latitude text, longitude text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+tbPicture+";");
        onCreate(db);
    }

    public void insertPicture(String pic_path, String lat, String lon){
            SQLiteDatabase write = this.getWritableDatabase();
            String query = "Insert into " + tbPicture + "(file_path,latitude,longitude) Values(?,?,?);";
            write.execSQL(query, new String[]{pic_path, lat, lon});
    }
    public void sqlIdentify(){
        SQLiteDatabase read = this.getReadableDatabase();
        String query = "select * from "+tbPicture+";";
        Cursor rs = read.rawQuery(query, null);
        while(rs.moveToNext()){
            Log.e("테이블 확인", rs.getString(0)+","+rs.getString(1)+","+rs.getString(2));
        }
    }

    public String getPicInfo(String pic){
        String lat = "0";
        String lon = "0";
        SQLiteDatabase read = this.getReadableDatabase();
        String query = "select latitude, longitude from "+tbPicture+" where file_path = ?;";
        Cursor rs = read.rawQuery(query, new String[]{pic});

        while(rs.moveToNext()){
            lat = rs.getString(0);
            lon = rs.getString(1);
        }
        return lat+","+lon;
    }



}
