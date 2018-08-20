<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
    <%@ page import = "java.sql.*,org.json.*,database.MoMConnection"%>
<% request.setCharacterEncoding("UTF-8"); %>
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
		

		String sql = "select * from mountain where mName=?";
		String mtName = request.getParameter("mtName");
		
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, mtName);
		
		
		rs = pstmt.executeQuery();
		
		if(rs.next()){
			responseJSON.put("mtCode", rs.getString(1));
			responseJSON.put("mtName", rs.getString(2));
			responseJSON.put("mtAddress", rs.getString(3));
			responseJSON.put("mtHeight", rs.getInt(4));
			responseJSON.put("mtPhoto", rs.getString(5)==null?"":rs.getString(5));
			responseJSON.put("mtArea", rs.getString(6));
			responseJSON.put("mtContent", rs.getString(7));
			responseJSON.put("mtOverview", rs.getString(8));
			responseJSON.put("mtLatitude", rs.getString(9));
			responseJSON.put("mtHardness", rs.getString(10));
		}else{
			responseJSON.put("status","empty");
		}
		
		out.println(responseJSON.toString());
		System.out.println(responseJSON.toString());
		response.setCharacterEncoding("UTF-8");
		
	}catch(SQLException e){
		e.printStackTrace();
	}finally{
		if(conn!=null)
			conn.close();
		if(rs!=null)
			rs.close();
		if(pstmt!=null)
			pstmt.close();
	}

%>
