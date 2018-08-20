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
<% request.setCharacterEncoding("UTF-8"); 
response.setCharacterEncoding("UTF-8");%>
<%! 
    Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;

%>
<%
System.out.println(request.getRequestURL());
	try{
		JSONObject responseJSON = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		int n=0;
		conn = MoMConnection.getConnection();
		String sql = "select * from mountain.course where mNum = ?";
		String mNum = request.getParameter("mNum");
		System.out.println(mNum);
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, mNum);
		rs = pstmt.executeQuery();
		while(rs.next()){
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("mNum", rs.getString(1));
			jsonObj.put("mCourseNum", rs.getInt(2));
			jsonObj.put("mLatitude", rs.getString(3));
			jsonObj.put("mLongitude",rs.getString(4));
			jsonObj.put("emdCd", rs.getString(5));
			jsonObj.put("sec_len", rs.getString(6));
			jsonObj.put("up_min", rs.getString(7));
			jsonObj.put("down_min", rs.getString(8));
			jsonObj.put("start_z", rs.getString(9));
			jsonObj.put("end_z", rs.getString(10));
			jsonObj.put("cat_nam", rs.getString(11));
			
			jsonArr.put(n, jsonObj);
			n++;
		}
		
		responseJSON.put("item", jsonArr);
		out.println(responseJSON.toString());
	}catch(Exception e){
		if(conn!=null)
			conn.close();
		if(rs!=null)
			rs.close();
		if(pstmt!=null)
			pstmt.close();
	}

%>