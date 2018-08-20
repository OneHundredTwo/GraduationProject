package com.cookandroid.mom.mt.map;

import android.graphics.Color;
import android.util.Log;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

/**
 * Created by RJH on 2017-09-27.
 * 맵에 코스, 산 위치 그려주는 메소드
 */

public  class DrawRouteMt {

    //코스 그리기 위한 메소드(메소드 명 변경 필요)
    public static void polyline(MapView mapView){
        MapPolyline polyline = new MapPolyline();
        polyline.setTag(2000);
        //polyline 색 지정
        polyline.setLineColor(Color.argb(128,255,51,0));

        //polyline 좌표 지정
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(37.45963055508549, 126.96931111119599));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(37.459441666735586, 126.96914722231385));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(37.45935555570642, 126.96891666708608));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(37.4592194446011, 126.96857222146096));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(37.45903055497907, 126.9683527774847));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(37.458738888940594, 126.96815277810022));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(37.45846666628647, 126.96798611038847));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(37.458224999836204, 126.96774444468358));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(37.45776388901867, 126.9674000001156));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(37.457480555301515, 126.96725277697405));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(37.45731388881344, 126.96714722303442));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(37.457063888723106, 126.96678055511114));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(37.456894444128324, 126.96647777770717));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(37.456686111363766, 126.96620555551793));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(37.456508333375474, 126.96607777793133));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(37.45629166687648, 126.965986112028));

        Log.e("Polyline","ok");
        //polyline 지도에 올리기
        mapView.addPolyline(polyline);

       /* //지도 뷰의 중심좌표와 줌레벨을 polyline이 모두 나오도록 조정
        MapPointBounds mapPointBounds = new MapPointBounds(polyline.getMapPoints());
        int padding = 100; //px
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));*/
    }

    //지도에 산 위치 마커로 그리는 메소드
    public static void mtLocation(MapView mapView){

    }
}
