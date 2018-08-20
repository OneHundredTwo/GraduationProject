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
		String sql = "select category , group_concat(mArea) as areas from mountain.area_category group by category order by category_index";
		pstmt = conn.prepareStatement(sql);
		
		rs = pstmt.executeQuery(sql);
		
		while(rs.next()){
			String category = rs.getString(1);
			String areas = rs.getString(2);
			JSONObject cat_area = new JSONObject();
			cat_area.put("category", category);
			cat_area.put("areas", areas);
			
			jsonArr.put(cat_area);
		}
		
		responseJSON.put("cats" , jsonArr);
		
		
		rs.close();
		pstmt.close();
		
		sql = ""
				+ "select category_index, mName, mNum "
				+ "from "
				+ "area_category a, mountain m "
				+ "where a.mArea = m.mArea";
		pstmt = conn.prepareStatement(sql);
		
		rs = pstmt.executeQuery();
		JSONArray cat_Mts = new JSONArray();
		while(rs.next()){
			JSONObject mt = new JSONObject();
			mt.put("index", rs.getInt(1));
			mt.put("mName", rs.getString(2));
			mt.put("mNum", rs.getString(3));
			
			cat_Mts.put(mt);
		}
		
		responseJSON.put("cat_Mts", cat_Mts);
		
		out.println(responseJSON);
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
