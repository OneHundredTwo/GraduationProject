<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import = "java.sql.*,org.json.*,database.MoMConnection"%>
<% request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8"); %>
<%! 
   Connection conn = null;
   PreparedStatement pstmt = null;
   ResultSet rs = null;
%>


<% 
	System.out.println(request.getRequestURL());
	JSONObject jobj = new JSONObject();
	JSONArray jobjArr = new JSONArray();
	int n=0;
	try{
		conn = MoMConnection.getConnection();
		String sql = "Delete from mountain.token where tokenid = ?";
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, request.getParameter("token"));
		
		pstmt.execute();
		
		out.println("{'status':1}");
		
		
		
	}catch(SQLException e){
		System.out.println(e.toString());
		out.println("{'status':0}");
	}finally{
		if(conn!=null)
			conn.close();
		if(rs!=null)
			rs.close();
	}

%>
