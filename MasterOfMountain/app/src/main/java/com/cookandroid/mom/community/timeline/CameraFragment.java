package com.cookandroid.mom.community.timeline;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import com.cookandroid.mom.R;
/**
 * Created by RJH on 2017-08-09.
 */

public class CameraFragment extends Fragment{
    String TAG = "CAMERA";
    private Context mContext;
    private Context mainContext;
    //private Camera mCamera;
    private CameraPreview mPreview;
    private TextView facingButton, captureButton, flashButton;
    private int mCameraFacing; // 전면 or 후면 카메라 상태 저장
    private boolean isLightOn= false;

    public CameraFragment(){
    }

    public static CameraFragment newInstance(Context mainContext){
        CameraFragment fragment = new CameraFragment();
        fragment.mainContext = mainContext;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_camera_fragment, container, false);
        mContext = this.getContext();
        Log.e("mContext 확인", mContext+"");
        mCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK; // 최초 카메라 상태는 후면카메라로 설정
        facingButton = (TextView) view.findViewById(R.id.button_facing);
        captureButton = (TextView) view.findViewById(R.id.button_capture);
        Drawable alpha = (view.findViewById(R.id.button_capture)).getBackground();
        alpha.setAlpha(75);
        flashButton = (TextView) view.findViewById(R.id.button_flash);

        // 카메라 인스턴스 생성
        Log.e("CAMERA TRUE FALSE", checkCameraHardware(mContext)+"");
        if (checkCameraHardware(mContext) == true) {
            //mCamera = getCameraInstance();
            //Log.e("CAMERA Identity1", mCamera+"");
            // 프리뷰창을 생성하고 액티비티의 레이아웃으로 지정합니다
            mPreview = new CameraPreview(mContext,/* mCamera,*/ mCameraFacing, mainContext);
            final FrameLayout preview = (FrameLayout) view.findViewById(R.id.camera_preview);
            preview.addView(mPreview);
            Log.e("각도", facingButton.getRotationY()+"");

            //사진 찍기 버튼
            captureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // JPEG 콜백 메소드로 이미지를 가져옵니다
                    mPreview.capture();
                    //Log.e("카메라패스", cameraPath);
                    Log.e("특정폴더", Environment.getExternalStorageDirectory().getPath() +
                            File.separator + "DCIM" + File.separator + "Camera");
                }
            });

            //카메라 전면 후면 전환 버튼
            facingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //facingButton.startAnimation(ani);
                    // 전면 -> 후면 or 후면 -> 전면으로 카메라 상태 전환
                    mPreview.setVisibility(View.GONE);
                    mCameraFacing = (mCameraFacing== Camera.CameraInfo.CAMERA_FACING_BACK) ?
                            Camera.CameraInfo.CAMERA_FACING_FRONT
                            : Camera.CameraInfo.CAMERA_FACING_BACK;
                    mPreview = new CameraPreview(mContext, /*mCamera,*/ mCameraFacing, mainContext);
                    preview.addView(mPreview);

                    mPreview.setVisibility(View.VISIBLE);
                    // 변경된 방향으로 새로운 카메라 View 생성
                }
            });

            //플래시 On/Off 버튼
            flashButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isLightOn) {
                        mPreview.flashCtrl(isLightOn);
                        flashButton.setBackground(null);
                        flashButton.setBackgroundResource(R.drawable.ic_flash_off_white_24dp);
                        isLightOn = false;
                    }else {
                        mPreview.flashCtrl(isLightOn);
                        flashButton.setBackground(null);
                        flashButton.setBackgroundResource(R.drawable.ic_flash_on_white_24dp);
                        isLightOn = true;
                    }
                }
            });

        }else{
            Toast.makeText(mContext, "no camera on this device!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }


    /** 카메라 하드웨어 지원 여부 확인 */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // 카메라가 최소한 한개 있는 경우 처리
            Log.i(TAG, "Number of available camera : "+ Camera.getNumberOfCameras());
            return true;
        } else {
            // 카메라가 전혀 없는 경우
            Toast.makeText(mContext, "No camera found!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

   /* *//** 카메라 인스턴스를 안전하게 획득합니다 *//*
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
            Log.e("CAMERA Identity(get)", c+"");
        }
        catch (Exception e){
            // 사용중이거나 사용 불가능 한 경우
        }
        return c;
    }*/

}
