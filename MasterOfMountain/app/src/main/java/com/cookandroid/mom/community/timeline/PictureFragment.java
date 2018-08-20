package com.cookandroid.mom.community.timeline;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import com.cookandroid.mom.R;


/**
 * Created by RJH on 2017-08-09.
 */

public class PictureFragment extends Fragment {
    private GridView gridView;
    private ImageAdapter imageAdapter;
    private ArrayList<ImageArray> imageArrayList;
    private PhotoView pv;
    private PhotoViewAttacher pva;
    private ImageView fixpic, bg;

    protected String strUri;

    public PictureFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_picture_fragment, container, false);
        imageArrayList = new ArrayList<ImageArray>();
        gridView = (GridView)view.findViewById(R.id.gridView);
        pv = (PhotoView)view.findViewById(R.id.photoView);
        setFindImage();

        imageAdapter = new ImageAdapter(getActivity(), R.layout.photo_list_item, imageArrayList);
        gridView.setAdapter(imageAdapter);
        Log.e("setFindImage확인", imageArrayList.get(0).getData().toString());

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                strUri = imageArrayList.get(position).getData();
                //Uri uri = Uri.parse(strUri);
                //Log.e("uri",uri+"");
                Glide.with(getContext()).load(strUri).into(pv);
                //pv.setImageURI(uri);
            }
        });
        return view;
    }

    /**사진 모두 가져와서 날짜 최신순으로 ArrayList에 저장하는 메소드*/
    public void setFindImage() {

        String proj[] = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        // Uri uri = Uri.parse(path1);

        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, null, null, MediaStore.Images.Media.DATE_ADDED + " desc ");
        if (cursor != null && cursor.getCount() > 0) {

            // 컬럼 인덱스
            int imageIDCol = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            int imageDataCol = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

            // 커서에서 이미지의 ID와 경로명을 가져와서 ThumbImageInfo 모델 클래스를 생성해서
            // 리스트에 더해준다.
            while (cursor.moveToNext())
            {
                ImageArray imageArray = new ImageArray();

                imageArray.setId(cursor.getString(imageIDCol));
                imageArray.setData(cursor.getString(imageDataCol));

                imageArrayList.add(imageArray);

            }
        }
        //cursor.moveToFirst();

        //이미지의 경로 값
        //String imgPath = cursor.getString(imageDataCol);
        //Log.e("절대경로",imgPath);
        cursor.close();
    }

    static class ImageViewHolder{
        ImageView imageView;
    }

    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private int cellLayout;
        private LayoutInflater inflater;
        private ArrayList<ImageArray> mImageArrayArrayList;
        private String path;

        public ImageAdapter(Context c, int cellLayout, ArrayList<ImageArray> imageArrayArrayList) {
            context = c;
            this.cellLayout = cellLayout;
            mImageArrayArrayList = imageArrayArrayList;
            inflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            return mImageArrayArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return mImageArrayArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (convertView == null) {
                //view = inflater.inflate(cellLayout, parent, false);
                view = inflater.inflate(cellLayout, parent, false);
            }else{
                view = convertView;
            }

            ImageViewHolder holder = new ImageViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.pictureImage);
            view.setTag(holder);

            path = imageArrayList.get(position).getData();
            Log.e("어댑터 내부 경로 확인", path);
            //사진 불러오기
            Glide
                    .with(context)
                    .load(path)
                    .dontAnimate()
                    .fitCenter()
                    .centerCrop()
                    .into(holder.imageView);

            return view;
        }
    }



}


