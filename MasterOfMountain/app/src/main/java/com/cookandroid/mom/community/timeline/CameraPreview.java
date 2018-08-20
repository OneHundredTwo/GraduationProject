package com.cookandroid.mom.community.timeline;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.cookandroid.mom.util.MoMLocation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by RJH on 2017-09-01.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    String TAG = "CAMERA";
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.Parameters parameters;
    private int mCameraFacing;
    private Context mContext, mainContext;
    private boolean isLight;
    private int orient;
    protected MediaScanner scanner;
    protected static MoMLocation location;

    public CameraPreview(Context context, /*Camera camera,*/ int cameraFacing, Context mainContext) {
        super(context);
        mContext = context;
        this.mainContext = mainContext;
        // mCamera = camera;
        // SurfaceHolder 가 가지고 있는 하위 Surface가 파괴되거나 업데이트 될경우 받을 콜백을 세팅한다
        mHolder = getHolder();
        mHolder.addCallback(this);

        // deprecated 되었지만 3.0 이하 버젼에서 필수 메소드라서 호출해둠.
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCameraFacing = cameraFacing;

        location = new MoMLocation(mContext);
    }


    //SurfaceView가 만들어질 때 호출
    public void surfaceCreated(SurfaceHolder holder) {
        // Surface가 생성되었으니 프리뷰를 어디에 띄울지 지정해준다. (holder 로 받은 SurfaceHolder에 뿌려준다.
        try {
            orient = 0;
            Log.e("CAMERA Identity", mCamera+"");
            mCamera = Camera.open(mCameraFacing);
            parameters = mCamera.getParameters();
            //포커스 잡기
            if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            //프리뷰 방향 설정
            if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                mCamera.setDisplayOrientation(90);
                //parameters.setRotation(90);

            } else {
                parameters.set("orientation", "landscape");
                mCamera.setDisplayOrientation(0);
                //parameters.setRotation(0);
            }
            mCamera.setParameters(parameters);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();

        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }

    }

    //SurfaceView가 종료될 때 호출
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 프리뷰 제거시 카메라 사용도 끝났다고 간주하여 리소스를 전부 반환한다
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.Size getBestPreviewSize(int width, int height)
    {
        Camera.Size result=null;
        Camera.Parameters p = mCamera.getParameters();
        for (Camera.Size size : p.getSupportedPreviewSizes()) {
            if (size.width<=width && size.height<=height) {
                if (result==null) {
                    result=size;
                } else {
                    int resultArea=result.width*result.height;
                    int newArea=size.width*size.height;

                    if (newArea>resultArea) {
                        result=size;
                    }
                }
            }
        }
        return result;

    }
    //SurfaceView의 크기가 바뀔 때 호출
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // 프리뷰를 회전시키거나 변경시 처리를 여기서 해준다.
        // 프리뷰 변경시에는 먼저 프리뷰를 멈춘다음 변경해야한다.

        if (mHolder.getSurface() == null){
            // 프리뷰가 존재하지 않을때
            return;
        }
        // 우선 멈춘다
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // 프리뷰가 존재조차 하지 않는 경우다
        }
        // 프리뷰 변경, 처리 등을 여기서 해준다.

        //가로, 세로 모드 이벤트 리스너
        OrientationEventListener oel = new OrientationEventListener(mContext) {
            @Override
            public void onOrientationChanged(int orientation) {
                if(orientation > 45 && orientation <=135) {
                    //LandScape
                    orient = 90;
                    //Log.e("LandScape", "LandScape/"+orient);
                }else if(orientation > 225 && orientation <= 315){
                    orient = 270;
                    // Log.e("LandScape", "LandScape/"+orient);
                }else if(orientation > 135 && orientation <= 225){
                    //Portrait
                    orient = 180;
                    // Log.e("Portrait", "Portrait/"+orient);
                }else{
                    //Portrait
                    orient = 0;
                    // Log.e("Portrait", "Portrait/"+orient);
                }
            }
        };
        oel.enable();
        parameters = mCamera.getParameters();
        Camera.Size size = null;
        size = getBestPreviewSize(w, h);
        parameters.setPreviewSize(size.width, size.height);
        mCamera.setParameters(parameters);
        // 새로 변경된 설정으로 프리뷰를 재생성한다
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    //플래시 On/Off
    public void flashCtrl(boolean light){
        if (light) {
            Log.i("info", "torch is turn off!");
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
            isLight = false;
        } else {
            Log.i("info", "torch is turn on!");
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
            isLight = true;
        }
    }

    //사진찍기
    public void capture(){
        //플래쉬o
        mCamera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {
            }
        }, null, mPicture);

        //플래쉬x
        //mCamera.takePicture(null, null, mPicture);
    }

    //카메라 찍기 콜백
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            mCamera.stopPreview();
            // data[] 로 넘어온 데이터를 bitmap으로 변환
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            // 화면 회전을 위한 matrix객체 생성
            Matrix m = new Matrix();
            // matrix객체에 회전될 정보 입력
            //카메라 전,후면에 따라 저장될 사진의 방향 설정(안하면 저장 사진 방향 이상함)
            if(mCameraFacing== Camera.CameraInfo.CAMERA_FACING_FRONT) {
                //m.setRotate(270, (float) bmp.getWidth(), (float) bmp.getHeight());
                Log.e("스위치문","전");
                m.setScale(-1, 1);
                switch (orient){
                    case 0:
                        //m.setRotate(270, (float) bmp.getHeight(), (float) bmp.getWidth());
                        m.postRotate(90, (float) bmp.getWidth(), (float) bmp.getHeight());
                        Log.e("case", "0");
                        break;
                    case 90:
                        //m.setRotate(270, (float) bmp.getHeight(), (float) bmp.getWidth());
                        m.postRotate(180, (float) bmp.getWidth(), (float) bmp.getHeight());
                        Log.e("case","90");
                        break;
                    case 180:
                        //m.setRotate(180, (float) bmp.getWidth(), (float) bmp.getHeight());
                        m.postRotate(270, (float) bmp.getWidth(), (float) bmp.getHeight());
                        Log.e("case","180");
                        break;
                    case 270:
                        //m.setRotate(90, (float) bmp.getWidth(), (float) bmp.getHeight());
                        m.postRotate(0, (float) bmp.getWidth(), (float) bmp.getHeight());
                        Log.e("case","270");
                        break;
                    default:
                        break;
                }
                Log.e("스위치문","후");
            }else {
                //m.setRotate(90, (float) bmp.getWidth(), (float) bmp.getHeight());
                switch (orient){
                    case 0:
                        m.postRotate(90, (float) bmp.getWidth(), (float) bmp.getHeight());
                        break;
                    case 90:
                        m.postRotate(180, (float) bmp.getWidth(), (float) bmp.getHeight());
                        break;
                    case 180:
                        m.postRotate(270, (float) bmp.getWidth(), (float) bmp.getHeight());
                        break;
                    case 270:
                        m.postRotate(0, (float) bmp.getWidth(), (float) bmp.getHeight());
                        break;
                    default:
                        break;
                }
            }
            // 기존에 저장했던 bmp를 Matrix를 적용하여 다시 생성
            Bitmap rotateBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, false);

            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Toast.makeText(getContext(), "Error saving!!", Toast.LENGTH_SHORT).show();
                return;
            }
            try {

                FileOutputStream fos = new FileOutputStream(pictureFile);
                rotateBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                //fos.write(data);
                fos.close();
                //Thread.sleep(500);
                //mCamera.startPreview();
                scanner = MediaScanner.newInstance(mContext);
                scanner.mediaScanning(pictureFile.getPath());


                Intent intent = new Intent(mContext, CheckActivity.class);
                intent.putExtra("uri", pictureFile.getPath());
                //Toast.makeText(mContext, "좌표:"+location.getLatitude(), Toast.LENGTH_SHORT).show();
                intent.putExtra("currentLatiFromCamera", location.getLatitude()+"");
                intent.putExtra("currentLongiFromCamera", location.getLongitude()+"");
                Log.e("putExtra 확인", pictureFile.getPath());
                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                mContext.startActivity(intent);
                //((Activity)mainContext).finish();

            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }
    };

    /** 이미지를 저장할 파일 객체를 생성합니다 */
    private File getOutputMediaFile() {
        // SD카드가 마운트 되어있는지 먼저 확인해야합니다
        // Environment.getExternalStorageState() 로 마운트 상태 확인 가능합니다

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // 굳이 이 경로로 하지 않아도 되지만 가장 안전한 경로이므로 추천함.
        Log.e("저장경로확인", mediaStorageDir+"");
        Log.e("저장경로확인2", Environment.getExternalStorageDirectory().getPath()+ File.separator+"DCIM"+ File.separator+"Camera");
        Log.e("확인", Environment.DIRECTORY_PICTURES);
        // 없는 경로라면 따로 생성한다.
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCamera", "failed to create directory");
                return null;
            }
        }
        // 파일명을 적당히 생성. 여기선 시간으로 파일명 중복을 피한다.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        //mediaFile = new File(mediaStorageDir.getPath() + File.separator + "MOM_"+ timeStamp + ".jpg");
        mediaFile = new File(Environment.getExternalStorageDirectory().getPath() +
                File.separator + "DCIM" + File.separator + "Camera" +
                File.separator + "MOM_"+ timeStamp + ".jpg");
        //Log.i("MyCamera", "Saved at"+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        Log.i("MyCamera", "Saved at"+ Environment.getExternalStorageDirectory().getPath() +
                File.separator + "DCIM" + File.separator + "Camera");
        return mediaFile;
    }

}
