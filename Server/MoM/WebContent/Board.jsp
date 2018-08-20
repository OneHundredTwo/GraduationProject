<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page import = "java.sql.*,org.json.*,database.MoMConnection"%>
<% request.setCharacterEncoding("UTF-8"); %>
<%! 
   Connection conn = null;
   PreparedStatement pstmt = null;
   PreparedStatement pstmt1 = null;
   ResultSet rs = null;
   ResultSet rs1 = null;
%>
<% 
System.out.println(request.getRequestURL());
JSONObject responseJSON = new JSONObject();
JSONArray jsonArr = new JSONArray();
    int n=0;
   	int[] club;
	String[] clubNm;
	int i=0;
   try{
      conn = MoMConnection.getConnection();
      

      //디비에 이름 같은 다른 사진이 저장될 때 중복 되지 않도록 저장하기 위함
      String photo = request.getParameter("photo");
      if(photo!=null){
      String photoOK = photo.substring(0,photo.lastIndexOf("."));//파일 이름
       String extension = photo.substring(photo.lastIndexOf("."));//확장자
       //System.out.println("extension:"+extension);
       //System.out.println("photoOK:"+photoOK);
       String select = "Select boardImg from mountain.board";
      pstmt = conn.prepareStatement(select);
      rs = pstmt.executeQuery();
      i=1;
      
      while(rs.next()){
         //System.out.println(photo +"," + rs.getString(1));
         if(photo.equals(rs.getString(1))){
            String rsltPhoto= "";
            while(true){
               rsltPhoto = photoOK+i; 
               //System.out.println("rsltPhoto확인:"+rsltPhoto);
               if(!(rsltPhoto+extension).equals(rs.getString(1))){
                  photo = rsltPhoto+extension;
                  break;
               }
               i++;
            }
         }
      }
      System.out.println("변경후photo확인:"+photo);
      
      pstmt.close();
      rs.close();
      }
      
		if((Integer.parseInt(request.getParameter("check"))==0)){
			String sql = "SELECT a.boardNum, writerID, boardContent, good, boardrange, clubNum, Latitude,Hardness,ifnull(boardImg,'no_picture') as boardImg,boardDate,reply,IF(ISNULL(goodID),false,true) checking "
					+ "FROM (SELECT * FROM mountain.board WHERE clubNum=?) a LEFT JOIN good b "
					+ "ON a.boardNum = b.boardNum AND goodID = ? ";
				   sql+="ORDER BY boardNum DESC";
			pstmt1 = conn.prepareStatement(sql);
			pstmt1.setString(1, request.getParameter("clubNum"));
			pstmt1.setString(2, request.getParameter("id"));
		}else{
			String sql1 = "SELECT club.clubName, club.clubNum "+
					 "FROM club, clubmem "+
					 "WHERE club.clubNum = clubmem.clubNum AND clubmem = ?";
		pstmt = conn.prepareStatement(sql1);
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
		System.out.println("clublength"+club.length);
			String sql = "SELECT a.boardNum, writerID, boardContent, good, boardrange, clubNum, Latitude,Hardness, ifnull(boardImg,'no_picture') as boardImg,boardDate,reply,IF(ISNULL(goodID),false,true) checking FROM "
					+"(SELECT * FROM mountain.board WHERE boardrange = '모두' ";
			for(int j=0; j<club.length; j++){
				sql += "UNION ALL SELECT * FROM "
						+"mountain.board WHERE clubNum=? ";
			}
				   sql+= "UNION ALL SELECT * FROM "
						 +"mountain.board WHERE boardrange = '비공개' "
						 +"AND boardNum IN (SELECT boardNum FROM mountain.board "
						 +"WHERE writerID = '" +request.getParameter("id")+"')) a LEFT JOIN good b "
						 +"ON a.boardNum = b.boardNum AND goodID = '"+request.getParameter("id")+"'";
			
			sql+=" ORDER BY boardNum DESC";
			pstmt1 = conn.prepareStatement(sql);
			for(int k=1; k<=club.length; k++){
				pstmt1.setInt(k,club[k-1]);
			}
		}
			rs1 = pstmt1.executeQuery();
			
			while(rs1.next()){	
				JSONObject jsonobj = new JSONObject();
				jsonobj.put("boardNum",rs1.getInt(1));
				jsonobj.put("id", rs1.getString(2));
				jsonobj.put("content", rs1.getString(3));
				jsonobj.put("good",rs1.getInt(4));
				jsonobj.put("boardrange", rs1.getString(5));
				jsonobj.put("clubNum",rs1.getString(6));
				jsonobj.put("photo",rs1.getString(9));
				jsonobj.put("date", rs1.getString(10));
				jsonobj.put("reply", rs1.getInt(11));
				jsonobj.put("checking",rs1.getBoolean(12));
				jsonArr.put(n,jsonobj);
				
				n++;
				}
	
		
	
		responseJSON.put("board",jsonArr);
		System.out.println(responseJSON.toString());
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