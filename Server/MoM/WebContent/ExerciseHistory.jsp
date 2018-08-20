<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import = "java.sql.*,org.json.*,database.MoMConnection, java.text.SimpleDateFormat, java.util.Calendar"%>
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
		String sql = "select * from health where userid = ? order by starttime desc limit ?,10";
		pstmt = conn.prepareStatement(sql);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sformat = new SimpleDateFormat("yyyy년 MM월 dd일");
		SimpleDateFormat lformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		String id = request.getParameter("id");
		int rownum = Integer.parseInt(request.getParameter("row"));
		
		
		pstmt.setString(1,id);
		pstmt.setInt(2,rownum);
		
		rs = pstmt.executeQuery();
		
		
		while(rs.next()){
			JSONObject item = new JSONObject();
			
			Long start = lformat.parse(rs.getString("starttime")).getTime();
			Long end = lformat.parse(rs.getString("endtime")).getTime();
			

			item.put("ex_id", rs.getString("health_id"));
			
			item.put("ex_pic", rs.getString("picpath"));
			item.put("ex_day",sformat.format(format.parse(rs.getString("hDate"))));
			item.put("ex_start",start);
			item.put("ex_end",end);
			item.put("ex_location",rs.getString("location"));
			item.put("step", rs.getInt("step"));
			item.put("calories",rs.getInt("calories"));
			item.put("distance", rs.getInt("distance"));
			item.put("ex_time", rs.getString("exer_time"));
			jsonArr.put(item);
		}
		
		responseJSON.put("items",jsonArr);
		
		out.println(responseJSON.toString());
		System.out.println(responseJSON.toString());
		
		
		/*
		이곳에 각 jsp내용 구현.
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
