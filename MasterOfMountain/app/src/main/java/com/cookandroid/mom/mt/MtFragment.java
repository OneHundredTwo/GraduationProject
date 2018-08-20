package com.cookandroid.mom.mt;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cookandroid.mom.community.WriteTimelineActivity;
import com.cookandroid.mom.util.Util;
import com.cookandroid.mom.R;


import com.cookandroid.mom.mt.map.MapActivity;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MtFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    String TAG = "MtFragment";

    private String area = "";
    private String[][] mtInfos;
    private String[] arrArea;
    private String[] arrMntNm;
    private String[] arrHeight;
    private String[] arrPhotos;
    private String[] arrNum;
    private Bitmap[] img;
    private Button btnSK, btnKang, btnChoong, btnJeon, btnKyung, btnJe;
    private ImageView sanView;
    private View view, footer, header;
    private ListView listview;
    private ArrayList<CData> alist = new ArrayList<CData>();
    private DataAdapter adapter;
    private int itemNum, totalListNum;
    private boolean lockListView;
    private boolean buttonListLock;
    private SwipeRefreshLayout refresh;
    private int cnt=0;
    public MtFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.mt_fragment, container, false);
        try {
            mtInfos = Util.mtManager.getAreaMts(cnt);
            arrArea = mtInfos[0];
            arrMntNm = mtInfos[1];
            arrHeight = mtInfos[2];
            arrPhotos = mtInfos[3];
            arrNum = mtInfos[4];
            img = new Bitmap[arrPhotos.length];
            for(int i=0; i<arrPhotos.length; i++){
                img[i] = Util.getIFS().execute(arrPhotos[i]).get();
            }
            // 받아온 pRecvServerPage를 분석하는 부분


        } catch (Exception e) {
            e.getStackTrace();
        }

        //중요!
        alist.clear();

        // 선언한 리스트뷰에 사용할 리스뷰연결
        listview = (ListView) view.findViewById(R.id.listView);
        refresh = (SwipeRefreshLayout)view.findViewById(R.id.mtRefresh);

        refresh.setOnRefreshListener(this);
        // 객체를 생성합니다

        // 데이터를 받기위해 데이터어댑터 객체 선언
        adapter = new DataAdapter(getActivity(), alist);
        // 리스트뷰에 어댑터 연결
        // listview.setAdapter(adapter);

        btnSK = (Button)view.findViewById(R.id.btnSK);
        btnKang = (Button)view.findViewById(R.id.btnKang);
        btnChoong = (Button)view.findViewById(R.id.btnChoong);
        btnJeon = (Button)view.findViewById(R.id.btnJeon);
        btnKyung = (Button)view.findViewById(R.id.btnKyung);
        btnJe = (Button)view.findViewById(R.id.btnJe);

        btnSK.setOnClickListener(this);
        btnKang.setOnClickListener(this);
        btnChoong.setOnClickListener(this);
        btnJeon.setOnClickListener(this);
        btnKyung.setOnClickListener(this);
        btnJe.setOnClickListener(this);

        final FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.btnFloating);
        fab.show();
        fab.setBackgroundColor(Color.BLUE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
            }
        });

        lockListView = false; //처음 잠기지 않은 상태
        buttonListLock = false;
        totalListNum = arrMntNm.length;

        for (itemNum = 0; itemNum < totalListNum; itemNum++) {
            adapter.add(new CData(getActivity().getApplicationContext(), arrMntNm[itemNum],
                    arrArea[itemNum], arrHeight[itemNum] + "m",img[itemNum]));

        }



        listview.setAdapter(adapter);

        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                cnt = (alist.isEmpty())? 0 : alist.size();
                //스크롤이 맨 아래일경우
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lockListView) {
                    try{
                        getData(cnt,area);
                    }catch (Exception e){
                        e.getStackTrace();
                    }
                }
                if(scrollState == SCROLL_STATE_FLING || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    fab.hide();
                }else{
                    fab.show();
                }
            }
            @Override
            //스크롤 시
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                lockListView = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);



            }
        });

        sanView = (ImageView)view.findViewById(R.id.sanView);
        sanView.setImageResource(R.drawable.all);
        sanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dlg = new Dialog(getActivity());
                dlg.setContentView(R.layout.photo_popup_detail);
                dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ImageView img = (ImageView)dlg.findViewById(R.id.img);
                img.setBackgroundResource(R.drawable.all);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dlg.cancel();
                    }
                });
                dlg.show();
            }
        });

        return view;
    }

    public void onRefresh(){
        refresh.setRefreshing(true);
        cnt = 0;
        adapter.clear();
        try {
            getData(cnt,area);
        } catch (Exception e) {
            e.printStackTrace();
        }
        refresh.setRefreshing(false);
    }
    //각 버튼 클릭시
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnSK:
                //데이터베이스에서 json 파일로 바꿀 때 쿼리문 각각 다르게해서 가져오는 방법
                area = "경기도,서울특별시,인천광역시";
                sanView.setImageResource(R.drawable.sk);
                sanView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dlg = new Dialog(getActivity());
                        dlg.setContentView(R.layout.photo_popup_detail);
                        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        ImageView img = (ImageView)dlg.findViewById(R.id.img);
                        img.setBackgroundResource(R.drawable.sk);
                        img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dlg.cancel();
                            }
                        });
                        dlg.show();
                    }
                });
                adapter.clear();
                try {
                    getData(0,area);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.btnKang:
                area = "강원도";
                sanView.setImageResource(R.drawable.kang);
                sanView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dlg = new Dialog(getActivity());
                        dlg.setContentView(R.layout.photo_popup_detail);
                        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        ImageView img = (ImageView)dlg.findViewById(R.id.img);
                        img.setBackgroundResource(R.drawable.kang);
                        img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dlg.cancel();
                            }
                        });
                        dlg.show();
                    }
                });
                adapter.clear();
                try {
                    getData(0,area);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.btnChoong:
                area = "충청북도,충청남도,대전광역시";
                sanView.setImageResource(R.drawable.choong);
                sanView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dlg = new Dialog(getActivity());
                        dlg.setContentView(R.layout.photo_popup_detail);
                        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        ImageView img = (ImageView)dlg.findViewById(R.id.img);
                        img.setBackgroundResource(R.drawable.choong);
                        img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dlg.cancel();
                            }
                        });
                        dlg.show();
                    }
                });
                adapter.clear();
                try {
                    getData(0,area);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.btnJeon:
                area = "전라북도,전라남도,광주광역시";
                sanView.setImageResource(R.drawable.jeon);
                sanView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dlg = new Dialog(getActivity());
                        dlg.setContentView(R.layout.photo_popup_detail);
                        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        ImageView img = (ImageView)dlg.findViewById(R.id.img);
                        img.setBackgroundResource(R.drawable.jeon);
                        img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dlg.cancel();
                            }
                        });
                        dlg.show();
                    }
                });
                adapter.clear();
                try {
                    getData(0,area);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.btnKyung:
                area = "경상북도,경상남도,부산광역시,울산광역시";
                sanView.setImageResource(R.drawable.kyung);
                sanView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dlg = new Dialog(getActivity());
                        dlg.setContentView(R.layout.photo_popup_detail);
                        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        ImageView img = (ImageView)dlg.findViewById(R.id.img);
                        img.setBackgroundResource(R.drawable.kyung);
                        img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dlg.cancel();
                            }
                        });
                        dlg.show();
                    }
                });
                adapter.clear();
                try {
                    getData(0,area);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.btnJe:
                area = "제주도";
                sanView.setImageResource(R.drawable.je);
                sanView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dlg = new Dialog(getActivity());
                        dlg.setContentView(R.layout.photo_popup_detail);
                        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        ImageView img = (ImageView)dlg.findViewById(R.id.img);
                        img.setBackgroundResource(R.drawable.je);
                        img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dlg.cancel();
                            }
                        });
                        dlg.show();
                    }
                });
                adapter.clear();
                try {
                    getData(0,area);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    private class DataAdapter extends ArrayAdapter<CData> {
        // 레이아웃 XML을 읽어들이기 위한 객체
        private LayoutInflater mInflater;
        private int position;


        public DataAdapter(Context context, ArrayList<CData> object) {

            // 상위 클래스의 초기화 과정
            // context, 0, 자료구조
            super(context, 0, object);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        }

        public void refreshAdapter(ArrayList<CData> items) {
            alist.clear();
            alist.addAll(items);
            notifyDataSetChanged();
        }
        @Override
        public View getView(int position, View v, ViewGroup parent) {
            View view = null;

            // 현재 리스트의 하나의 항목에 보일 컨트롤 얻기

            if (v == null) {

                // XML 레이아웃을 직접 읽어서 리스트뷰에 넣음
                view = mInflater.inflate(R.layout.mt_list_mtinfo, null);
            } else {

                view = v;
            }



            // 자료를 받는다.
            final CData data = this.getItem(position);
            /*view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch(event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //눌린 상태
                            v.setBackgroundColor(Color.parseColor("#BDBDBD"));
                            break;
                        case MotionEvent.ACTION_UP:
                            //뗀 상태
                            v.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            break;
                    }
                    return true;
                }
            });*/
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //<여기다 상세정보데이터 얻어오는 메소드>
                    // ArrayAdapter객체(여기선 adapter)의 getItem메소드로 선택된 리스트아이템의 position을 얻어서 해당 아이템(여기선 제너릭으로 CData)형식으로 형변환하여 가져옴.

                    String sanName_f = ((TextView)v.findViewById(R.id.sanName)).getText().toString();
                    Log.e("산이름", sanName_f);

                    Intent intent = new Intent(getActivity(),MtDetailInfoActivity.class);
                    intent.putExtra("mtName",spliteSanName(sanName_f));
                    intent.putExtra("mtCode", getMtNum(spliteSanName(sanName_f)));
                    Log.e("mtNum", getMtNum(spliteSanName(sanName_f)));
                    startActivity(intent);
                    //intent로 이용하여 산이름만 보내주기
                }

                public String spliteSanName(String sanName_f){
                    String sanName =sanName_f.substring(sanName_f.indexOf(":")+1,sanName_f.length());
                    sanName = sanName.trim();
                    return sanName;
                }
                public String getMtNum(String sanName_f){
                    String mtNum = "";
                    for(int i=0; i<arrMntNm.length; i++){
                        if(sanName_f.equals(arrMntNm[i]))
                            mtNum = arrNum[i];
                    }
                    return mtNum;
                }
            });

            if (data != null) {
                // 화면 출력
                TextView name = (TextView) view.findViewById(R.id.sanName);
                TextView time = (TextView) view.findViewById(R.id.sanAdd);
                TextView height = (TextView) view.findViewById(R.id.sanHeight);
                //텍스트뷰1(name)에 getM_name()출력 즉, 첫번째 인수값
                name.setText(data.getM_sanName());
                //텍스트뷰2(time)에 getM_time()출력 즉, 두번째 인수값
                time.setText(data.getM_sanAdd());
                //텍스트뷰3(content)에 getM_content()출력 즉, 세번째 인수값
                height.setText(data.getM_sanHeight());
                name.setTextColor(Color.BLACK);
                time.setTextColor(Color.BLACK);
                height.setTextColor(Color.BLACK);

                ImageView pic = (ImageView) view.findViewById(R.id.sanPic);

                // 이미지 뷰에 뿌려질 해당 이미지 값을 연결 즉, 네번째 인수값
                pic.setImageBitmap(data.getM_sanPic());


            }

            return view;

        }



    }
    //jsp에서 카운트 해서 가져오는 메소드
    //데이터를 추가로 가져와야하기 때문에 서버요청을 다시해서 데이터를 가져온다.
    public void getData(int count, String area) throws Exception{
        if(area.equals("")){
            mtInfos = Util.mtManager.getAreaMts(count);
        }else{
            mtInfos = Util.mtManager.getAreaMts(count,area);
        }

        arrArea = mtInfos[0];
        arrMntNm = mtInfos[1];
        arrHeight = mtInfos[2];
        arrPhotos = mtInfos[3];

        img = new Bitmap[arrPhotos.length];

        for (int i = 0; i < arrArea.length; i++) {
            img[i] = Util.getIFS().execute(arrPhotos[i]).get();
            adapter.add(new CData(getActivity().getApplicationContext(), arrMntNm[i],
                    arrArea[i], arrHeight[i] + "m",img[i]));

        }
        adapter.notifyDataSetChanged();
        lockListView = false;
    }
    // CData안에 받은 값을 직접 할당
    class CData {

        private String m_sanName, m_sanAdd, m_sanHeight;
        private Bitmap m_sanPic;

        public CData(Context context, String name, String time,
                     String height, Bitmap pic) {

            m_sanName = name;
            m_sanAdd = time;
            m_sanHeight = height;
            m_sanPic = pic;

        }

        public String getM_sanName() {
            return m_sanName;
        }

        public String getM_sanAdd() {
            return m_sanAdd;
        }

        public String getM_sanHeight() {
            return m_sanHeight;
        }

        public Bitmap getM_sanPic() {
            return m_sanPic;
        }
    }



}