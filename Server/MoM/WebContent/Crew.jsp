<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import = "java.sql.*,org.json.*,database.MoMConnection"%>
<% request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8"); %>
<%! 
   Connection conn = null;
   PreparedStatement pstmt = null;
   PreparedStatement pstmt1 = null;
   ResultSet rs = null;
   ResultSet rs1 = null;
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
		String sql = "SELECT club.clubName, club.clubNum "+
				 "FROM club, clubmem "+
				 "WHERE club.clubNum = clubmem.clubNum AND clubmem = ?";
	pstmt = conn.prepareStatement(sql);
	pstmt.setString(1, request.getParameter("id"));
	rs = pstmt.executeQuery();
	rs.last();
	System.out.println(rs.getRow());
	club = new int[rs.getRow()];
	clubNm = new String[rs.getRow()];
	rs.beforeFirst();
	System.out.println(rs.getRow());
	while(rs.next()){
		clubNm[i] = rs.getString(1);
		club[i] = rs.getInt(2);
		i++;
	}
	if(Integer.parseInt(request.getParameter("check"))==1){
	String sql1 = "SELECT planNum, planID, prejoin, joinNum, mtNm, planDate, clubName FROM "
			+"(SELECT planNum, planID, prejoin, joinNum, mtNm, planDate, plan.clubNum,club.clubName "
			+"FROM plan, club " 
			+"WHERE plan.clubNum = club.clubNum) a WHERE clubNum=? AND prejoin != joinNum AND planNum NOT IN "
			+"(SELECT b.planNum "
			+"FROM plan a, mountain.join b "
			+"WHERE a.planNum = b.planNum AND b.id = '" +request.getParameter("id")+"')"; 
	for(int j=0; j<club.length-1; j++){
		sql1 += "UNION ALL SELECT planNum, planID, prejoin, joinNum, mtNm, planDate, clubName FROM "
				+"(SELECT planNum, planID, prejoin, joinNum, mtNm, planDate, plan.clubNum,club.clubName "
				+"FROM plan, club " 
				+"WHERE plan.clubNum = club.clubNum) a WHERE clubNum=? AND prejoin != joinNum AND planNum NOT IN "
				+"(SELECT b.planNum "
				+"FROM plan a, mountain.join b "
				+"WHERE a.planNum = b.planNum AND b.id = '" +request.getParameter("id")+"')"; 
	}
	pstmt1 = conn.prepareStatement(sql1);
	for(int k=1; k<=club.length; k++){
		pstmt1.setInt(k,club[k-1]);
	}
	}else{
		String sql1 = "SELECT a.planNum, planID, prejoin, joinNum, mtNm, planDate, clubName "
					 +"FROM (SELECT planNum, planID, prejoin, joinNum, mtNm, planDate, plan.clubNum,club.clubName "
					 +"FROM plan, club "
					 +"WHERE plan.clubNum = club.clubNum) a, mountain.join b "
					 +"WHERE a.planNum = b.planNum AND b.id =?";
		pstmt1 = conn.prepareStatement(sql1);
		pstmt1.setString(1, request.getParameter("id"));
	}
	
	
	rs1 = pstmt1.executeQuery();
	while(rs1.next()){	
		JSONObject jsonobj = new JSONObject();
		jsonobj.put("planNum", rs1.getInt(1));
		jsonobj.put("planID", rs1.getString(2));
		jsonobj.put("prejoin", rs1.getInt(3));
		jsonobj.put("joinNum", rs1.getInt(4));
		jsonobj.put("mNum", rs1.getString(5));
		jsonobj.put("planDate", rs1.getString(6));
		jsonobj.put("clubNum", rs1.getString(7));
		jsonarr.put(n,jsonobj);
		
		n++;
		}
	
	pstmt.close();
	rs.close();
	jsonMain.put("crew",jsonarr);
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
