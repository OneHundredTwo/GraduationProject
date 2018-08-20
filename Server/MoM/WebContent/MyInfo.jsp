<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
	JSONObject jsonobj = new JSONObject();
	int n=0;
	try{
		conn = MoMConnection.getConnection();
		String sql = "SELECT * FROM member WHERE id = ?";
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, request.getParameter("id"));
		rs = pstmt.executeQuery();
		while(rs.next()){	
			
			jsonobj.put("id", rs.getString(1));
			jsonobj.put("address", rs.getString(3));
			jsonobj.put("sex", rs.getString(4));
			jsonobj.put("phone", rs.getString(5));
			jsonobj.put("email", rs.getString(6));
			jsonobj.put("hint", rs.getString(7));
			//System.out.println(jsonobj.toJSONString());
			n++;
			}
		
		pstmt.close();
		rs.close();
		System.out.print(responseJSON.toString());
		responseJSON.put("myinfo",jsonobj);
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