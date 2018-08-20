<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import = "java.sql.*,org.json.*,database.MoMConnection"%>
<% request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8"); %>
<%! 
   Connection conn = null;
PreparedStatement pstmt = null;
PreparedStatement pstmt2 = null;
ResultSet rs = null;
%>


<% 
	System.out.println(request.getRequestURL());
	JSONObject jsonMain = new JSONObject();
	JSONArray jsonarr = new JSONArray();
	int[] club;
	String[] clubNm;
	int i=0;
	int n=0;
	try{
		conn = MoMConnection.getConnection();
		String sql = "SELECT a.planNum, planID, prejoin, joinNum, mtNm, planDate, clubName,planContent, planregion,coursenum "
				   + "FROM (SELECT planNum, planID, prejoin, joinNum, mtNm, planDate, plan.clubNum,club.clubName,planContent, planregion, coursenum "
				   + "FROM plan, club " 
				   + "WHERE plan.clubNum = club.clubNum) a, mountain.join b " 
				   + "WHERE a.planNum = b.planNum AND a.planNum = ?";
		
		pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, Integer.parseInt(request.getParameter("crew")));
		
		
		rs = pstmt.executeQuery();
		
		JSONObject jsonobj = new JSONObject();
		if(rs.next()){
			jsonobj.put("planNum", rs.getInt(1));
			jsonobj.put("planID", rs.getString(2));
			jsonobj.put("prejoin", rs.getInt(3));
			jsonobj.put("joinNum", rs.getInt(4));
			jsonobj.put("mtNm", rs.getString(5));
			jsonobj.put("planDate", rs.getString(6));
			jsonobj.put("clubNum", rs.getString(7));
			jsonobj.put("plancontent", rs.getString(8));
			jsonobj.put("planregion", rs.getString(9));
			jsonobj.put("coursenum", rs.getInt(10));
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
