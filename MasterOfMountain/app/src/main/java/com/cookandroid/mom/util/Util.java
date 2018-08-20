package com.cookandroid.mom.util;

import com.cookandroid.mom.MainActivity;
import com.cookandroid.mom.home.ManageActivity;

/**
 * Created by S401 on 2017-06-08.
 */

public class Util {

    public static final int INSERT = 0;
    public static final int UPDATE = 1;


    public static String tagHeader = "MoM_";

    static String AppServerIP = "192.168.43.84";
    static String AppServerPORT = ":10000";
    /*지금은 둘 다 같지만, 혹시 달라질 수도 있으니 변수를 달리함.*/
    static String imgServerIP = Util.AppServerIP;
    static String imgServerPORT = Util.AppServerPORT;

    static AndroidToServer ant;
    public static User user;
    public static MountainManager mtManager;
    public static Community community;

    public static MainActivity main;
    public static ManageActivity manage;

    //Date Formats




    private Util(){}

    public static void init(){
        user = User.getInstance();
        mtManager = MountainManager.getInstance();
        community = Community.getInstance();
    }

    public static AndroidToServer getAnt(){
        return new AndroidToServer();
    }
    public static AndroidToServer getAnt(String ip, String port){
        return new AndroidToServer(ip, port);
    }
    public static ImgFromServer getIFS(){

        return new ImgFromServer();
    }
    public static ImgFromServer getIFS(String ip, String port){
        return new ImgFromServer(ip,port);
    }

    public static ImgToServer getITS(){return new ImgToServer();}

    public static PhotoImgFromServer getPIFS(){
        return new PhotoImgFromServer();
    }
    public static PhotoImgFromServer getPIFS(String ip, String port){
        return new PhotoImgFromServer(ip,port);
    }



}
