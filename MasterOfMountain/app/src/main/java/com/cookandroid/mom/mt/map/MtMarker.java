package com.cookandroid.mom.mt.map;

import android.util.Log;

import com.cookandroid.mom.util.Util;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

/**
 * Created by RJH on 2017-10-06.
 */

public class MtMarker {

    private String[][] mtPoint;
    private String[] arrName;
    private String[] arrLatitude;
    private String[] arrLongitude;
    private String[] arrNum;
    private MapPOIItem[] marker;
    private MapPoint[] mapPoint;
    private MapPoint mp;
    public void nullMapPOIItem(){
        marker = null;
    }

    public String[] getArrName() {
        return arrName;
    }
    public String getName(int i){
        return arrName[i];
    }
    public String getNum(int i){
        return arrNum[i];
    }

    public void drawMtMarker(MapView mapView) {

        try {
            mtPoint = Util.mtManager.getMtPoint();
            arrName = mtPoint[0];
            arrLatitude = mtPoint[1];
            arrLongitude = mtPoint[2];
            arrNum = mtPoint[3];
           // Log.e("drawMtMarker", "메소드" + arrName.length);
            for (int i = 0; i < arrName.length; i++)
                Log.e("확인", arrName[i] + ","+ arrNum[i]+"," + arrLatitude[i] + "," + arrLongitude[i]);

        } catch (Exception e) {
            e.printStackTrace();
        }
        /*MapPOIItem mtMarker = new MapPOIItem();
        mp = MapPoint.mapPointWithGeoCoord(Double.parseDouble(arrLatitude[0]), Double.parseDouble(arrLongitude[0]));
        mtMarker.setItemName(arrName[0]);
        mtMarker.setMapPoint(mp);
        mapView.addPOIItem(mtMarker);*/
        /*for(int i=0; i<arrName.length; i++)
            mapPoint[i] = MapPoint.mapPointWithGeoCoord(Double.parseDouble(arrLatitude[i]), Double.parseDouble(arrLongitude[i]));
         for(int i=0; i<arrName.length; i++) {
                MapPOIItem mtMarker = new MapPOIItem();
                mtMarker.setItemName(arrName[i]);
                mtMarker.setMapPoint(mapPoint[i]);
                mapView.addPOIItem(mtMarker);
        }*/
        mapPoint = new MapPoint[arrName.length];
        marker = new MapPOIItem[arrName.length];
        Log.e("marker", marker.length+"");
        //mapPoint[0] = MapPoint.mapPointWithGeoCoord(Double.parseDouble(arrLatitude[0]), Double.parseDouble(arrLongitude[0]));
        //mapPoint[1] = MapPoint.mapPointWithGeoCoord(Double.parseDouble(arrLatitude[1]), Double.parseDouble(arrLongitude[1]));
        for (int i = 0; i < arrName.length; i++) {
            mapPoint[i] = MapPoint.mapPointWithGeoCoord(Double.parseDouble(arrLatitude[i]), Double.parseDouble(arrLongitude[i]));
            marker[i] = new MapPOIItem();
            marker[i].setItemName(arrName[i]);
            marker[i].setTag(i);
            marker[i].setMapPoint(mapPoint[i]);
            marker[i].setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            marker[i].setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        }
        mapView.addPOIItems(marker);




        /*지도 뷰의 중심좌표와 줌레벨을 polyline이 모두 나오도록 조정
        MapPointBounds mapPointBounds = new MapPointBounds();
        int padding = 100; //px
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));
        */
    }


}
