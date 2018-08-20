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
	JSONObject jsonMain = new JSONObject();
	JSONArray jsonarr = new JSONArray();
	int n=0;
	try{
		if(request.getParameter("club")!=null){
		conn = MoMConnection.getConnection();
		String sql = "select * from club where clubName=?";
		
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, request.getParameter("club"));
		}else{
			conn = MoMConnection.getConnection();
			String sql = "select * from club where clubNum=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, Integer.parseInt(request.getParameter("clubNum")));
		}
		
		rs = pstmt.executeQuery();
		
		JSONObject jsonobj = new JSONObject();
		if(rs.next()){
			jsonobj.put("clubNum", rs.getInt(1));
			jsonobj.put("clubNm", rs.getString(2));
			jsonobj.put("clubID", rs.getString(3));
			jsonobj.put("clubContent", rs.getString(4));
			jsonobj.put("clubDate", rs.getString(5));
			jsonobj.put("clubJoin", rs.getInt(6));
			jsonobj.put("clubIcon", rs.getString(7));
		}else{
			jsonobj.put("status","empty");
		}
		
		out.println(jsonobj.toString());
		System.out.println(jsonobj.toString());
		
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
