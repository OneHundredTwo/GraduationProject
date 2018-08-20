package database;

import java.sql.DriverManager;
import java.io.File;
import java.sql.Connection;

public class MoMConnection {
	final static private String dbServerIp = "localhost";
	final static private String port = "4000";
	final static private String dbId = "root";
	final static private String dbPwd = "0000";
	final static private String dbName = "mountain";
	final static private String DRIVERNAME = "com.mysql.jdbc.Driver";
	final static private String URL = "jdbc:mysql://"+dbServerIp+":"+port+"/"+dbName;
	
	final static public String path = "C:/Users/Shining/Dropbox/Server/MoM/WebContent/";
	final static public String imgpath = path+"img/";
	final static public String timelinepath = path+"TimelineImages/";
	
	public static Connection getConnection() throws Exception{
		Class.forName(DRIVERNAME);
		return DriverManager.getConnection(URL, dbId, dbPwd);
	}

}
