package com.cookandroid.mom.community.club;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.mom.R;
import com.cookandroid.mom.util.Util;
import com.dd.processbutton.iml.ActionProcessButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InsertClubActivity extends ActionBarActivity {
    private TextView clubHost;
    private EditText club_name,club_content;
    private ImageView clubImg;
    private Button btnClubimg, btnNo;
    private final int MY_PERMISSION_CAMERA = 1;
    private final int REQUEST_TAKE_ALBUM = 2;
    private final int REQUEST_IMAGE_CROP = 3;
    private ActionProcessButton btnOK;
    String mCurrentPhotoPath;

    Uri photoURI, albumURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_insert_club);

        Toolbar toolbar = (Toolbar)findViewById(R.id.clubR_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("동호회 등록");


        btnClubimg = (Button)findViewById(R.id.btnImage);
        btnOK =(ActionProcessButton)findViewById(R.id.btnOK);
        btnOK.setMode(ActionProcessButton.Mode.ENDLESS);
        btnNo = (Button)findViewById(R.id.btnNo);
        clubImg = (ImageView)findViewById(R.id.club_image);
        clubHost = (TextView)findViewById(R.id.club_host);
        club_name = (EditText)findViewById(R.id.club_name);
        club_content = (EditText)findViewById(R.id.club_content);

        clubHost.setText("개설자 ID : "+ Util.user.getId());
        checkPermission();
        btnClubimg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getAlbum();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                btnOK.setProgress(50);
                String Icon = Util.getITS().getImagePathToUri(albumURI);
                Log.e("Icon:",Icon);
                Util.getITS().execute(albumURI.getPath());
                try {
                    final String message = Util.community.InsertClub(club_name.getText().toString(), Util.user.getId(),club_content.getText().toString(),Icon);
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(InsertClubActivity.this, message, Toast.LENGTH_SHORT).show();
                            if(message.equals("동호회가 등록되었습니다.")){
                                btnOK.setProgress(100);
                                Intent intent = new Intent(InsertClubActivity.this, ClubManagementActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                btnOK.setProgress(-1);
                            }
                        }
                    }, 500);
                } catch (Exception e) {
                    btnOK.setProgress(-1);
                    e.printStackTrace();
                }
            }
        });
    }
    public File createImageFile() throws IOException {
        Log.i("createImageFile", "Call");
        //Create an image file name, 외장 메모리 저장
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File imageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/gyeom" + imageFileName);

        if(!imageFile.exists()){
            imageFile.getParentFile().mkdir();
            imageFile.createNewFile();
        }

        mCurrentPhotoPath = imageFile.getAbsolutePath();
        Log.i("createImageFile", mCurrentPhotoPath);

        return imageFile;
    }

    private void getAlbum(){
        Log.i("getAlbum", "Call");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
    }


    //카메라 전용 크랩
    public void cropImage(){
        Log.i("cropImage","Call");
        Log.i("cropImage", "photoURI : " + photoURI + " / albumURI : " + albumURI);

        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        //50x50픽셀미만은 편집할 수 없다는 문구 처리 + 갤러리, 포토 둘다 호환하는 방법
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoURI, "image/*");
        //cropIntent.putExtra("outputX", 200); //crop한 이미지의 x축 크기, 결과물의 크기
        //cropIntent.putExtra("outputY", 200); //crop한 이미지의 y축 크기
        cropIntent.putExtra("aspectX",1); //crop 박스의 x축 비율, 1&1이면 정사각형
        cropIntent.putExtra("aspectY",1); //crop박스의 y축 비율
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", albumURI); //크랍된 이미지를 해당 경로에 저장
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case REQUEST_TAKE_ALBUM:
                if(resultCode == Activity.RESULT_OK){
                    if(data.getData() != null){
                        try{
                            File albumFile = null;
                            albumFile = createImageFile();
                            photoURI = data.getData();
                            albumURI = Uri.fromFile(albumFile);
                            cropImage();
                        }catch (Exception e){
                            Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
                        }
                    }
                }
                break;

            case REQUEST_IMAGE_CROP:
                if(resultCode == Activity.RESULT_OK){
                    clubImg.setImageURI(albumURI);

                    Log.e("albumURI:",albumURI.getPath());
                }
                break;
        }
    }

    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //다시 보지 않기 버튼을 만드려면 이 부분에 바로 요청을 하도록 하면됨 (아래 else{..} 부분 제거)
            //ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_

            //처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
            if((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) || (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))){
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정>권한>권한을 사용하시면 됩니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i ){
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i){
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                //해당 버튼을 클릭시 액션 삽입
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case MY_PERMISSION_CAMERA:
                for(int i = 0; i<grantResults.length; i++){
                    //grantResults[] : 허용된 권한은 0, 거부한 권한은 -1
                    if(grantResults[i]<0){
                        Toast.makeText(InsertClubActivity.this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                //허용했다면 이부분에서

                break;
        }
    }

}
