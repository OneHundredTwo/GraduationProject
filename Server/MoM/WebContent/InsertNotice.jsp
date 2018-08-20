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
		String sql = "Insert into mountain.notice(content, date, title, admin_id, category) values(?,?,?,?,?)";
		pstmt = conn.prepareStatement(sql);
		
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String cat = request.getParameter("category");
		long date = Long.parseLong(request.getParameter("date"));
		String date_s = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);
		String admin_id = request.getParameter("admin_id");
		
		pstmt.setString(1,content);
		pstmt.setString(2,date_s);
		pstmt.setString(3,title);
		pstmt.setString(4,admin_id);
		pstmt.setString(5,cat);
		
		
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
