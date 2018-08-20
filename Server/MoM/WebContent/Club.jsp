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
	int n=0;
	try{
		conn = MoMConnection.getConnection();
		String sql = null;
		if(Integer.parseInt(request.getParameter("check"))==1){
			sql = "SELECT club.clubNum, clubName,clubID,clubContent,clubDate,clubjoin,clubIcon, COUNT(planNum) crew "
					+"FROM club LEFT OUTER JOIN plan "
					+"ON club.clubNum = plan.clubNum "
					+"WHERE club.clubNum NOT IN(SELECT clubmem.clubNum "
					+"FROM clubmem "
					+"WHERE clubmem = ?) "
					+"GROUP BY club.clubNum "
					+"ORDER BY club.clubNum DESC";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, request.getParameter("id"));
			
		}else{
			System.out.println(request.getParameter("id"));
			sql = "SELECT a.clubNum, a.clubName, a.clubID, a.clubContent, a.clubDate, a.clubjoin, a.clubIcon, a.crew "
					+"FROM (SELECT club.clubNum, clubName,clubID,clubContent,clubDate,clubjoin,clubIcon, COUNT(planNum) crew "
					+	   "FROM club LEFT OUTER JOIN plan "
					+	   "ON club.clubNum = plan.clubNum "
					+	   "GROUP BY club.clubNum)a, "
					+	   "clubmem b "
					+	"WHERE a.clubNum = b.clubNum AND b.clubmem = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, request.getParameter("id"));
			
		}
		rs = pstmt.executeQuery();
		while(rs.next()){	
			JSONObject jsonobj = new JSONObject();
			jsonobj.put("clubNum", rs.getString(1));
			jsonobj.put("clubname", rs.getString(2));
			jsonobj.put("clubID", rs.getString(3));
			jsonobj.put("clubContent", rs.getString(4));
			jsonobj.put("clubDate", rs.getString(5));
			jsonobj.put("clubjoin", rs.getInt(6));
			jsonobj.put("clubIcon", rs.getString(7));
			jsonobj.put("crew", rs.getString(8));
			jsonarr.put(n,jsonobj);
			
			n++;
			}
		
		pstmt.close();
		rs.close();
		jsonMain.put("club",jsonarr);
		System.out.println(jsonMain.toString());
		out.println(jsonMain.toString());
		
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
