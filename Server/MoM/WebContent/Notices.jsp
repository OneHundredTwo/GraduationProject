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
		String sql = "select * from mountain.notice order by date desc";
		pstmt = conn.prepareStatement(sql);
		
		
		rs = pstmt.executeQuery();
		SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		while(rs.next()){
			JSONObject notice = new JSONObject();
			
			notice.put("noticeNum", rs.getInt("notice_num"));
			notice.put("content", rs.getString("content"));
			long date = (simple.parse(rs.getString("date"))).getTime();
			notice.put("date", date);
			notice.put("title", rs.getString("title"));
			notice.put("admin_id", rs.getString("admin_id"));
			notice.put("category", rs.getString("category"));
			
			jsonArr.put(notice);
		}
		/*
		이곳에 각 jsp내용 구현.
		*/
		
		responseJSON.put("notices", jsonArr);
		
		out.println(responseJSON.toString());
		
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
