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
	int n=0;
	try{
		conn = MoMConnection.getConnection();
		String sql =null;
		
		if(Integer.parseInt(request.getParameter("cnt")) == -1){
         String[] area = (request.getParameter("area")).split(",");
         sql = "select * from mountain where mountain.mArea in ('";
         for(int i=0; i<area.length; i++){
            sql+=area[i]+"','";
         }
         sql+="')";
         pstmt = conn.prepareStatement(sql);
      }
		else if(request.getParameter("area")==null){
			sql = "SELECT * FROM mountain limit ?,5";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, Integer.parseInt(request.getParameter("cnt")));
			
		}else{
			System.out.println(request.getParameter("area"));
			String[] area = (request.getParameter("area")).split(",");
			sql = "select * from mountain where mountain.mArea in ('";
			for(int i=0; i<area.length; i++){
				sql+=area[i]+"','";
			}
			sql+="') limit ?, 5";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, Integer.parseInt(request.getParameter("cnt")));
		}
		rs = pstmt.executeQuery();
		while(rs.next()){
		JSONObject jsonobj = new JSONObject();
		jsonobj.put("mntncd", rs.getString(1));
		jsonobj.put("mntnm", rs.getString(2));
		jsonobj.put("areanm", rs.getString(3));
		jsonobj.put("mntheight", rs.getString(4));
		jsonobj.put("mPhoto",rs.getString(5));
		jsonobj.put("area", rs.getString(6));
		jsonobj.put("details", rs.getString(7));
		jsonobj.put("overview",rs.getString(8));
		
		
		jsonArr.put(n,jsonobj);
		n++;
		}
		responseJSON.put("item",jsonArr);
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
