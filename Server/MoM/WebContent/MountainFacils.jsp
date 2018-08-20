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
		String sql = "select m.mName, f.facilities, f.facilitiescontent"
				+" from mountain m, facilities f "
				+" where m.mNum=f.mNum and f.mNum = ?";
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1,request.getParameter("mNum"));
		
		rs = pstmt.executeQuery();
		int i=0;
		while(rs.next()){
			JSONObject jsonobj = new JSONObject();
			jsonobj.put("mtName",rs.getString(1));
			jsonobj.put("facilName", rs.getString(2));
			jsonobj.put("content", rs.getString(3));
			jsonArr.put(i++, jsonobj);
	}
		responseJSON.put("facilinfos",jsonArr);
		
		out.println(responseJSON.toString());
		response.setCharacterEncoding("UTF-8");
		
		
		
	}catch(SQLException e){
		e.printStackTrace();
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
