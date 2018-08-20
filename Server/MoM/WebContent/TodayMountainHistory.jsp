<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import = "java.sql.*,org.json.*,database.MoMConnection, java.text.SimpleDateFormat"%>
<% request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8"); %>
<%! 
   Connection conn = null;
   PreparedStatement pstmt = null;
   ResultSet rs = null;
%>


<% 
	System.out.println(request.getRequestURL());
	JSONObject responseJSON = new JSONObject();
	JSONArray jsonArr = new JSONArray();
	int n=0;
	try{
		conn = MoMConnection.getConnection();
		String sql = "select a.*, c.category from todaymt a, mountain b, area_category c where a.mtNum = b.mNum and b.mArea = c.mArea order by todaymt_num desc";
		pstmt = conn.prepareStatement(sql);
		
		SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		rs = pstmt.executeQuery();
		
		while(rs.next()){
			JSONObject todaymt = new JSONObject();
			todaymt.put("todayNum", rs.getInt("todaymt_num"));
			todaymt.put("mtNum", rs.getString("mtNum"));
			todaymt.put("event", rs.getString("event"));
			todaymt.put("content", rs.getString("content"));
			todaymt.put("picture", rs.getString("picture"));
			todaymt.put("title",rs.getString("title"));
			todaymt.put("date", (simple.parse(rs.getString("date"))).getTime());
			todaymt.put("isshow", rs.getBoolean("isshow"));
			todaymt.put("mtArea", rs.getString("category"));
			
			jsonArr.put(todaymt);
		}
		
		responseJSON.put("history", jsonArr);
		
		out.println(responseJSON);
		
		/*
		이곳에 각 jsp내용 구현.
		*/
		
	}catch(SQLException e){
		System.out.println(e.toString());
	}finally{
		if(conn!=null)
			conn.close();
		if(rs!=null)
			rs.close();
		if(pstmt!=null)
			pstmt.close();
	}

%>
