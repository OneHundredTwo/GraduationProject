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
		String sql = "select a.id,b.isadmin from mountain.token a, mountain.member b where" 
					+" a.tokenid = ?"
					+" and "
					+" a.id = b.id";
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, request.getParameter("token"));
		rs = pstmt.executeQuery();
		
		JSONObject user_info = new JSONObject();
		
		if(rs.next()){
			String id = rs.getString(1);
			boolean isAdmin = rs.getBoolean(2);
		
			responseJSON.put("status",1);
			user_info.put("id", id);
			user_info.put("isAdmin", isAdmin);	
			responseJSON.put("user_info",user_info);
		}else{
			responseJSON.put("status",0);
		}
		

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
