package com.cookandroid.mom.community.timeline;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cookandroid.mom.community.WriteTimelineActivity;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.cookandroid.mom.R;
/**
 * Created by RJH on 2017-09-06.
 */

public class CheckActivity extends AppCompatActivity {
    private static final String TAG = "CheckActivity";

    private ImageView img;
    private TextView btnEdit;
    private Toolbar toolbar;
    private String uri;
    private String currentLatiFromCamera, currentLongiFromCamera; //CameraFragment에서 전달 받은 변수
    private String currentLatiFromPhoto, currentLongiFromPhoto;//PictureFragment에서 전달 받은 변수
    private Uri mDestinationUri, mResultUri;
    private MediaScanner scanner;
    private File rsltFile;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage.jpeg"; //캐시에 임시 저장시 파일 이름
    private static final int RATIO_ORIGIN = 0;
    private static final int RATIO_SQUARE = 1;
    private static final int RATIO_DYNAMIC = 2;
    private static final int RATIO_CUSTOM = 3;
    private static final int FORMAT_PNG = 0;
    private static final int FORMAT_WEBP = 1;
    private static final int FORMAT_JPEG = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_check_activity);
        WriteTimelineActivity.actList.add(this);
        Log.e("ArrayList.actList(사이즈)", WriteTimelineActivity.actList.size()+"");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("사진 확인");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        uri = intent.getStringExtra("uri");
        currentLatiFromCamera = intent.getStringExtra("currentLatiFromCamera");
        currentLongiFromCamera = intent.getStringExtra("currentLongiFromCamera");
        currentLatiFromPhoto = intent.getStringExtra("currentLatiFromPhoto");
        currentLongiFromPhoto = intent.getStringExtra("currentLongiFromPhoto");
        Log.e("CheckActivity", "onCreate");
        Log.e("좌표 확인 FromCamera", currentLatiFromCamera+","+currentLongiFromCamera);
        Log.e("좌표 확인 FromPhoto", currentLatiFromPhoto+","+currentLongiFromPhoto);

        img = (ImageView)findViewById(R.id.img);
        Glide.with(this).load(uri).into(img);
        //img.setImageURI(Uri.parse(uri));

        // setup crop
        mDestinationUri = Uri.fromFile(new File(getCacheDir(), SAMPLE_CROPPED_IMAGE_NAME));

        btnEdit = (TextView)findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCropActivity(Uri.parse("file://"+uri));
            }
        });

    }

    private void startCropActivity(@NonNull Uri uri) {
        UCrop uCrop = UCrop.of(uri, mDestinationUri)/*.withAspectRatio(1, 1)*/;
        uCrop = _setRatio(uCrop, RATIO_ORIGIN, 0, 0);
        uCrop = _setSize(uCrop, 0, 0);
        uCrop = _advancedConfig(uCrop, FORMAT_JPEG, 90);
        uCrop.start(CheckActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Urop 라이브러리로 만들어진 액티비티에서 돌아오는 onActivityResult
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            mResultUri = resultUri;
            Log.e("mResultUri", mResultUri.getPath());
            //uri = resultUri.getPath();
            Log.e("resultUri", resultUri.getPath());
            rsltFile = _saveCroppedImage();
            Uri file = Uri.fromFile(rsltFile);
            uri = file.getPath();
            img.setImageURI(null);
            img.setImageURI(file);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                WriteTimelineActivity.actList.remove(1);
                finish();
                return true;
            case R.id.btnNext:
                //글쓰기 화면으로 가는 Intent
                String fileName = uri.substring(uri.lastIndexOf("/") + 1);
                Intent intent = new Intent();

                //카메라 프래그먼트로 부터 전달 받은 좌표가 있을 경우
                if(currentLatiFromCamera != null && currentLongiFromCamera != null) {
                    // fileName, 좌표 SQLite에 저장
                    CameraPreview.location.locationMapping(uri, currentLatiFromCamera, currentLongiFromCamera);
                    intent.putExtra("currentLatiFromCamera", currentLatiFromCamera);// WriteTimelineActivity에 좌표 보내줌
                    intent.putExtra("currentLongiFromCamera", currentLongiFromCamera);
                }
                //포토 액티비티(픽쳐 프래그먼트)로 부터 전달 받은 좌표가 있을 경우
                if(currentLatiFromPhoto != null && currentLongiFromPhoto != null){
                    intent.putExtra("currentLatiFromPhoto", currentLatiFromPhoto);// WriteTimelineActivity에 좌표 보내줌
                    intent.putExtra("currentLongiFromPhoto", currentLongiFromPhoto);
                }

                //포토 액티비티(픽쳐 프래그먼트)로 부터 전달 받은 좌표가 없을 경우는 그냥 uri, fileName만 넘김
                intent.putExtra("uri", uri);
                intent.putExtra("fileName", fileName);
                setResult(RESULT_OK, intent);
                for(int i=0; i<WriteTimelineActivity.actList.size(); i++) {
                    WriteTimelineActivity.actList.get(i).finish();
                }
                WriteTimelineActivity.actList.clear();
                finish();


                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private UCrop _setRatio(@NonNull UCrop uCrop, int choice, float xratio, float yratio){
        switch (choice) {
            case RATIO_ORIGIN:
                uCrop = uCrop.useSourceImageAspectRatio();
                break;
            case RATIO_SQUARE:
                uCrop = uCrop.withAspectRatio(1, 1);
                break;
            case RATIO_DYNAMIC:
                // do nothing
                break;
            case RATIO_CUSTOM:
            default:
                try {
                    float ratioX = xratio;
                    float ratioY = yratio;
                    if (ratioX > 0 && ratioY > 0) {
                        uCrop = uCrop.withAspectRatio(ratioX, ratioY);
                    }
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Number please", e);
                }
                break;
        }

        return uCrop;

    }

    private UCrop _setSize(@NonNull UCrop uCrop, int maxWidth, int maxHeight){
        if(maxWidth > 0 && maxHeight > 0){
            return uCrop.withMaxResultSize(maxWidth, maxHeight);
        }
        return uCrop;
    }


    /**
     * Sometimes you want to adjust more options, it's done via {@link UCrop.Options} class.
     *
     * @param uCrop - ucrop builder instance
     * @return - ucrop builder instance
     */
    private UCrop _advancedConfig(@NonNull UCrop uCrop, int format, int quality) {
        UCrop.Options options = new UCrop.Options();

        switch (format) {
            case FORMAT_PNG:
                options.setCompressionFormat(Bitmap.CompressFormat.PNG);
                break;
            case FORMAT_WEBP:
                options.setCompressionFormat(Bitmap.CompressFormat.WEBP);
                break;
            case FORMAT_JPEG:
            default:
                options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                break;
        }
        options.setCompressionQuality(quality); // range [0-100]
        /*
        If you want to configure how gestures work for all UCropActivity tabs
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);*/

        /*
        This sets max size for bitmap that will be decoded from source Uri.
        More size - more memory allocation, default implementation uses screen diagonal.
        options.setMaxBitmapSize(640);*/

       /*
        Tune everything (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧
        options.setMaxScaleMultiplier(5);
        options.setImageToCropBoundsAnimDuration(666);
        options.setDimmedLayerColor(Color.CYAN);
        options.setOvalDimmedLayer(true);
        options.setShowCropFrame(false);
        options.setCropGridStrokeWidth(20);
        options.setCropGridColor(Color.GREEN);
        options.setCropGridColumnCount(2);
        options.setCropGridRowCount(1);
        // Color palette
        options.setToolbarColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.your_color_res));
		options.setToolbarTitleTextColor(ContextCompat.getColor(this, R.color.your_color_res));
         */

        return uCrop.withOptions(options);
    }

    private File _saveCroppedImage() {
        Uri imageUri = mResultUri;
        File returnFile = null;
        Log.e("imageUri", imageUri.getPath());
        if (imageUri != null && imageUri.getScheme().equals("file")) {
            try {
                returnFile = copyFileToDownloads(imageUri);
            } catch (Exception e) {
                Toast.makeText(CheckActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, imageUri.toString(), e);
            }finally{
                return returnFile;
            }
        } else {
            Toast.makeText(CheckActivity.this, "사진 저장 오류", Toast.LENGTH_SHORT).show();
        }
        return null;
    }


    private File copyFileToDownloads(Uri croppedFileUri) throws Exception {
        String downloadsDirectoryPath
                = Environment.getExternalStorageDirectory().getPath() +
                File.separator + "DCIM" + File.separator + "MOM";
        File saveDir = new File(downloadsDirectoryPath);
        if (!saveDir.exists()) {
            if (!saveDir.mkdirs()) {
                Log.d("MyCamera", "failed to create directory");
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = File.separator + "ModifiedMOM_"+ timeStamp + ".jpg";
        // 없는 경로라면 따로 생성한다.
        File saveFile = new File(downloadsDirectoryPath + filename);

        FileInputStream inStream = new FileInputStream(new File(croppedFileUri.getPath()));
        FileOutputStream outStream = new FileOutputStream(saveFile);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
        Log.e("저장 완료",saveFile.getPath());
        scanner = MediaScanner.newInstance(this);
        scanner.mediaScanning(saveFile.getPath());

        return saveFile;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //하드웨어 백버튼 누르면 ArrayList에서 액티비티 삭제
        WriteTimelineActivity.actList.remove(1);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("ArrayList.actList(사이즈)", WriteTimelineActivity.actList.size()+"");
    }
}
