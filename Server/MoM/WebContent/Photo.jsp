<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import = " 
org.json.*,
java.sql.*,
database.MoMConnection,
fcm.*,
java.io.BufferedReader, 
java.net.URL, 
java.io.InputStreamReader"%>
<% request.setCharacterEncoding("UTF-8"); %>
<%! 
    Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	
%>
<%
	JSONObject responseJSON = new JSONObject();
	JSONArray jsonArr = new JSONArray();
	int n=0;
	try{
		conn = MoMConnection.getConnection();
 		String sql = "select boardImg, Latitude, Hardness, mName from mountain.board where board.boardNum IN(select boardNum from mountain.good where goodID =?)and board.mName =?";
		pstmt = conn.prepareStatement(sql);
	    pstmt.setString(1, request.getParameter("UserID"));
 	    pstmt.setString(2, request.getParameter("mName"));
 

		rs = pstmt.executeQuery();
		while(rs.next()){
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("photo", rs.getString(1));
			jsonObj.put("mLatitude", rs.getString(2));
			jsonObj.put("mLongitude",rs.getString(3));
			
			jsonArr.put(n, jsonObj);
			n++;
		}
		responseJSON.put("item", jsonArr);
		out.println(responseJSON.toString());
	}catch(Exception e){
		if(rs!=null)
			rs.close();
		if(pstmt!=null)
			pstmt.close();
		if(conn!=null)
			conn.close();
	}

%>