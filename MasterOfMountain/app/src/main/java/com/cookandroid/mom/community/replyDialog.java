package com.cookandroid.mom.community;

/**
 * Created by wdj46 on 2017-10-24.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.mom.R;
import com.cookandroid.mom.util.DataStringFormat;
import com.cookandroid.mom.util.Util;

import java.util.ArrayList;


public class replyDialog extends Dialog{

    private ListView listView;
    private ImageButton replySubmit;
    private EditText editreply;
    private ArrayList<CData> alist;
    private DataAdapter adapter;
    private String[][] repInfo;
    private String[] re_name,re_time,re_content;
    private int boardNum;
    private String writer;
    private AlertDialog alertDialog;

    public replyDialog(Context context, int bn, String writerID){
        super(context);
        this.boardNum = bn;
        this.writer = writerID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.reply_dialog);

        editreply = (EditText)findViewById(R.id.editreply);
        replySubmit = (ImageButton)findViewById(R.id.replySubmit);
        listView = (ListView)findViewById(R.id.listView);

        alist = new ArrayList<CData>();
        adapter = new DataAdapter(getContext(), alist);
        listView.setAdapter(adapter);
        Log.e("boardNum",""+boardNum);
        getData();
        replySubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Util.user.getId() != null) {
                    if (editreply.getText().toString().equals("")) {
                        Toast.makeText(getContext(), "댓글을 입력해주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = Util.community.InsertReply(boardNum, Util.user.getId(), editreply.getText().toString(), writer);
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        editreply.setText(null);
                        alist.clear();
                        getData();
                        dismiss();
                        show();
                    }


                } else {
                    Toast.makeText(getContext(), "비회원은 접근할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                v= mInflater.inflate(R.layout.community_reply_list, null);
                ViewHolder holder = new ViewHolder();
                holder.name = (TextView) v.findViewById(R.id.name);
                holder.time  = (TextView) v.findViewById(R.id.time);
                holder.content = (TextView) v.findViewById(R.id.replytxt);
                holder.delreply = (ImageView) v.findViewById(R.id.delreply);
                holder.editreply = (ImageView)v.findViewById(R.id.editreply);
                holder.edittxt = (EditText)v.findViewById(R.id.edittxt);
                holder.editSubmit = (Button)v.findViewById(R.id.btnedit);
                v.setTag(holder);
            } else {

                view = v;
            }

            // 자료를 받는다.
            final CData data = this.getItem(position);
            if (data != null) {
                // 화면 출력
                final ViewHolder holder = (ViewHolder)v.getTag();
                holder.name.setText(data.getRe_name());
                holder.time.setText(DataStringFormat.CreateDataWithCheck(data.getRe_time()));
                holder.content.setText(data.getRe_content());
                holder.edittxt.setText(data.getRe_content());

                holder.name.setTextColor(Color.BLACK);
                holder.content.setTextColor(Color.BLACK);

                if(data.re_name.equals(Util.user.getId())){
                    holder.delreply.setVisibility(View.VISIBLE);
                    holder.editreply.setVisibility(View.VISIBLE);
                }else{
                    holder.delreply.setVisibility(View.GONE);
                    holder.editreply.setVisibility(View.GONE);
                }
                holder.editreply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.edittxt.setVisibility(View.VISIBLE);
                        holder.editSubmit.setVisibility(View.VISIBLE);
                        holder.content.setVisibility(View.INVISIBLE);
                        //EditText 초점맞추기
                        holder.edittxt.requestFocus();
                        //커서 마지막으로 옮기기
                        holder.edittxt.setSelection(holder.edittxt.length());
                        //키보드 보이게하는부분
                        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                    }
                });
                holder.editSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("time",data.getRe_time());
                        if (holder.edittxt.getText().toString().equals("")) {
                            Toast.makeText(getContext(), "수정할 댓글을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = Util.user.editReply(holder.edittxt.getText().toString(), boardNum,data.getRe_time());
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            InputMethodManager immhide = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            if (message.equals("글이 수정되었습니다.")) {
                                holder.edittxt.setVisibility(View.INVISIBLE);
                                holder.content.setVisibility(View.VISIBLE);
                                holder.editSubmit.setVisibility(View.INVISIBLE);
                                editreply.requestFocus();
                                alist.clear();
                                getData();
                                dismiss();
                                show();
                            }
                        }
                    }
                });

                holder.delreply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                        alertDialogBuilder.setTitle("삭제");

                        alertDialogBuilder
                                .setMessage("이 댓글을 삭제하시겠습니까?")
                                .setCancelable(false)
                                .setPositiveButton("삭제",
                                        new OnClickListener(){

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                    String message = Util.user.delReply(boardNum,writer,data.getRe_time());
                                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                                        alist.clear();
                                                        getData();
                                                        replyDialog.this.dismiss();
                                                        replyDialog.this.show();
                                            }
                                        })
                                .setNegativeButton("취소", new OnClickListener(){

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


            }
            return v;

        }
    }

    public void getData() {

        repInfo = Util.user.getReply(boardNum);
        re_name = repInfo[0];
        re_content = repInfo[1];
        re_time = repInfo[2];


        for (int i=0; i<re_time.length; i++){
            alist.add(new CData(getContext(), re_name[i],re_content[i],
                    re_time[i]));
        }
    }

    class CData {


        private String re_name,re_time, re_content;
//        private int m_pic, m_imageView;

        public CData(Context context, String name,
                     String content, String date) {

          this.re_name = name;
          this.re_time = date;
          this.re_content = content;

        }
        public String getRe_name() {return re_name;}

        public String getRe_time() {return re_time;}

        public String getRe_content() {return re_content;}

    }

    private class ViewHolder {
        TextView name;
        TextView time;
        TextView content;
        ImageView delreply;
        ImageView editreply;
        EditText edittxt;
        Button editSubmit;
    }



}
