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
	JSONObject responseJSON = new JSONObject();
	JSONArray jsonArr = new JSONArray();
	int n=0;
	try{
		conn = MoMConnection.getConnection();
		String sql = "delete from mountain.health where health_id = ?";
		pstmt = conn.prepareStatement(sql);
		
		String ex_id = request.getParameter("ex_id");
		System.out.println(ex_id);
		
		pstmt.setString(1,ex_id);
		
		pstmt.executeUpdate();
		
		/*
		���¸޼��� �ۼ��ڵ�.
		*/
		out.println("done");
		
		/*
		�̰��� �� jsp���� ����.
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
