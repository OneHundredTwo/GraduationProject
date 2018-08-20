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
		String sql = ""
				+ "Insert into mountain.todaymt(mtNum, event, content, picture, title, date, isshow) "
				+ "values(?,?,?,?,?,?,?)";
		pstmt = conn.prepareStatement(sql);
		
		String mtNum = request.getParameter("mtNum");
		String event = request.getParameter("event");
		String content = request.getParameter("content");
		String picture = request.getParameter("picture");
		String title = request.getParameter("title");
		long date = Long.parseLong(request.getParameter("date"));
		String date_s = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);
		boolean isshow = Boolean.parseBoolean(request.getParameter("isshow"));
		
		pstmt.setString(1, mtNum);
		pstmt.setString(2, event);
		pstmt.setString(3,content);
		pstmt.setString(4, picture);
		pstmt.setString(5, title);
		pstmt.setString(6,date_s);
		pstmt.setBoolean(7, isshow);
		
		pstmt.executeUpdate();
		
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

