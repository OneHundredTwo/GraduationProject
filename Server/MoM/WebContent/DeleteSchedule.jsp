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
		String sql = "delete from mountain.schedule where schId = ?";
		pstmt = conn.prepareStatement(sql);
		
		String schId = request.getParameter("schId");
		String time = request.getParameter("time");
		
		pstmt.setString(1,schId);
		
		pstmt.executeUpdate();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		
		
		
		
		out.println("{'status':1,'date':"+format.parse(time).getTime()+"}");
		System.out.println("{'status':1}");
		
		/*
		이곳에 각 jsp내용 구현.
		*/
		
	}catch(SQLException e){
		System.out.println(e.toString());
		out.println("{'status':0}");
		System.out.println("{'status':0}");
	}finally{
		if(conn!=null)
			conn.close();
		if(rs!=null)
			rs.close();
		if(pstmt!=null)
			pstmt.close();
	}

%>
