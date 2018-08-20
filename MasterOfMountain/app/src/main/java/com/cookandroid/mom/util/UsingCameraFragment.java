package com.cookandroid.mom.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Shining on 2017-10-27.
 */

public class UsingCameraFragment extends Fragment{
    private final int REQUEST_TAKE_ALBUM = 2;
    private final int REQUEST_IMAGE_CROP = 3;

    String mCurrentPhotoPath;

    Uri photoURI, albumURI;

    public ImageView displayImage;
    public TextView fileName;

    public boolean isSelected = false;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                    if(displayImage != null) {
                        displayImage.setImageURI(albumURI);
                    }
                    if(fileName != null) {
                        fileName.setText(albumURI.getPath());
                    }
                    isSelected = true;
                    Log.e("albumURI:",albumURI.getPath());
                }
                break;
        }
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

    public void getAlbum(){
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

}
