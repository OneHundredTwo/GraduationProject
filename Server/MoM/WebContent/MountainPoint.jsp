<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page import = "java.sql.*,org.json.*,database.MoMConnection"%>
<% request.setCharacterEncoding("UTF-8");
	%>
<%! 
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
%>


<% 
	JSONObject jsonMain = new JSONObject();
	JSONArray jsonarr = new JSONArray();
	//getParameter로 불러오면 , 안깨져있고
	//getQueryString으로 출력하면 깨져있음. 
	int n=0;
	try{
		conn = MoMConnection.getConnection();
		String sql =null;
	
		sql = "SELECT * FROM mountain";
		pstmt = conn.prepareStatement(sql);
		rs = pstmt.executeQuery();
		
		while(rs.next()){
		JSONObject jsonobj = new JSONObject();
		jsonobj.put("mName", rs.getString(2));
		jsonobj.put("mLatitude", rs.getString(9));
		jsonobj.put("mLongitude", rs.getString(10));
		jsonobj.put("mNum", rs.getString(1));
		
		jsonarr.put(n,jsonobj);
		n++;
		}
		pstmt.close();
		rs.close();
		jsonMain.put("item",jsonarr);
		out.println(jsonMain.toString());
	}catch(SQLException e){
			System.out.println(e.toString());
}
%>
