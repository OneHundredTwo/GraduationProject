package com.cookandroid.mom.mt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cookandroid.mom.R;
import com.cookandroid.mom.util.MountainManager;
import com.cookandroid.mom.util.Util;

import java.util.ArrayList;

public class MtInfoFragment1 extends Fragment {
    private TextView textInfo;
    private MountainManager.Mountain mountain;


    public MtInfoFragment1(){
    }

    public MtInfoFragment1 newInstance(){
        return new MtInfoFragment1();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mountain = MtDetailInfoActivity.mountain;


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.mt_detail_1info_frag, container, false);

        ImageView[] wImgs = new ImageView[]{(ImageView)view.findViewById(R.id.imgCurWeather),(ImageView)view.findViewById(R.id.img4Weather),
                (ImageView)view.findViewById(R.id.img7Weather), (ImageView)view.findViewById(R.id.img10Weather), (ImageView)view.findViewById(R.id.img13Weather),
                (ImageView)view.findViewById(R.id.img16Weather), (ImageView)view.findViewById(R.id.img19Weather), (ImageView)view.findViewById(R.id.img22Weather),
                (ImageView)view.findViewById(R.id.img25Weather)};
        TextView[] wTemps = new TextView[]{(TextView)view.findViewById(R.id.txtCurTemp),(TextView)view.findViewById(R.id.txt4Temp), (TextView)view.findViewById(R.id.txt7Temp),
                (TextView)view.findViewById(R.id.txt10Temp),(TextView)view.findViewById(R.id.txt13Temp),(TextView)view.findViewById(R.id.txt16Temp), (TextView)view.findViewById(R.id.txt19Temp),
                (TextView)view.findViewById(R.id.txt22Temp),(TextView)view.findViewById(R.id.txt25Temp)};
        TextView[] wTimes = new TextView[]{(TextView)view.findViewById(R.id.txtCurTime),(TextView)view.findViewById(R.id.txt4Time), (TextView)view.findViewById(R.id.txt7Time),
                (TextView)view.findViewById(R.id.txt10Time),(TextView)view.findViewById(R.id.txt13Time),(TextView)view.findViewById(R.id.txt16Time), (TextView)view.findViewById(R.id.txt19Time),
                (TextView)view.findViewById(R.id.txt22Time),(TextView)view.findViewById(R.id.txt25Time)};


        ArrayList<MountainManager.Weather> weatherInfos = Util.mtManager.getWeatehrInfos(mountain);
        if(weatherInfos !=null && weatherInfos.size()!=0) {

            Log.e("size of weatherinfos ",""+weatherInfos.size());
            int i = 0;
            for (MountainManager.Weather w : weatherInfos) {

                Log.e("weather check ",w.toString());
                int image = getWeatherImage(w.code);
                wImgs[i].setImageResource(image);
                wTemps[i].setText(w.temperature + "C");
                wTimes[i].setText(w.time);
                i++;
            }

        }else{
            TextView label = (TextView)view.findViewById(R.id.lbWeather);
            label.setText("날씨정보를 받아올 수 없습니다.");
        }



        try {
            textInfo = (TextView) view.findViewById(R.id.text_info);
            textInfo.setText(mountain.mtContent);
            textInfo.setMovementMethod(new ScrollingMovementMethod());//텍스트뷰에 스크롤
        }catch(NullPointerException e){
            e.printStackTrace();
        }




        return view;
    }

    public int getWeatherImage(String code){
        /*
        - SKY_O01: 맑음
- SKY_O02: 구름조금
- SKY_O03: 구름많음
- SKY_O04: 구름많고 비
- SKY_O05: 구름많고 눈
- SKY_O06: 구름많고 비 또는 눈
- SKY_O07: 흐림
- SKY_O08: 흐리고 비
- SKY_O09: 흐리고 눈
- SKY_O10:  흐리고 비 또는 눈
- SKY_O11: 흐리고 낙뢰
- SKY_O12: 뇌우, 비
- SKY_O13: 뇌우, 눈
- SKY_O14: 뇌우, 비 또는 눈
        * */
        if(code.equals("맑음")) return R.drawable.sky_001;
        if(code.equals("구름많음")) return R.drawable.sky_002;
        if(code.equals("구름조금")) return R.drawable.sky_003;
        if(code.equals("구름많고 비")) return R.drawable.sky_004;
        if(code.equals("구름많고 눈")) return R.drawable.sky_005;
        if(code.equals("구름많고 비 또는 눈")) return R.drawable.sky_006;
        if(code.equals("흐림")) return R.drawable.sky_007;
        if(code.equals("흐리고 비")) return R.drawable.sky_008;
        if(code.equals("흐리고 눈")) return R.drawable.sky_009;
        if(code.equals("흐리고 비 또는 눈")) return R.drawable.sky_010;
        if(code.equals("흐리고 낙뢰")) return R.drawable.sky_011;
        if(code.equals("뇌우, 비")) return R.drawable.sky_012;
        if(code.equals("뇌우, 눈")) return R.drawable.sky_013;
        if(code.equals("SKY_013")) return R.drawable.sky_014;

        return R.drawable.sky_001;

    }

}
