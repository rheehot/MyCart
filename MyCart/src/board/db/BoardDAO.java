package board.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class BoardDAO {

	private DataSource ds;
	private Connection con=null;
	private PreparedStatement pstmt=null;
	private ResultSet rs=null;

	public BoardDAO() {

		// 생성자로 DB 연결 (보안 위해 context.xml?)
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			ds = (DataSource) envCtx.lookup("jdbc/OracleDB");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
		
	public Timestamp getDate() { //작성시간 기입
		String sql = null;
		try {
			con = ds.getConnection(); 
			sql = "select systimestamp from dual";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				return rs.getTimestamp(1);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {con.close();}
				if (pstmt != null) {pstmt.close();}
				if (rs != null) {rs.close();}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		return null; //DB 오류
	}
	
	public int getNext() { //게시글 번호
		String sql = null;
		try {
			con = ds.getConnection(); 
			sql = "select boardID from userMyTableBoard order by boardID desc";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				return rs.getInt(1) + 1;
			}
			return 1; //첫번째 게시물인 경우
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {con.close();}
				if (pstmt != null) {pstmt.close();}
				if (rs != null) {rs.close();}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		return -1; //DB 오류
	}
	
	public boolean nextPage(int pageNumber) { //게시글 페이징 처리
		String sql = null;
		
		try {
			con = ds.getConnection(); 
			sql = "select * from userMyTableBoard where boardID < ? and boardAvailable = 1 and boardUserID = ?"; 
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, getNext() - (pageNumber - 1) * 10); //로그인에서부터 readAction 까지 가져온 userID 를 여기에 넣자는 의미. 지금은 땜빵
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {con.close();}
				if (pstmt != null) {pstmt.close();}
				if (rs != null) {rs.close();}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		return false; //DB 오류
		
	}

	public ArrayList<BoardDTO> read(int pageNumber, String userID) {
		String sql = null;
		
		try {
			ArrayList<BoardDTO> boardList = new ArrayList<BoardDTO>();
			con = ds.getConnection(); 
			sql = "select * from userMyTableBoard where boardID < ?  and boardAvailable = 1 and boardUserID = ?  and rownum <= 10 order by boardID desc";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, getNext() - (pageNumber - 1) * 10); //로그인에서부터 readAction 까지 가져온 userID 를 여기에 넣자는 의미. 지금은 땜빵
			pstmt.setString(2, userID); //로그인에서부터 readAction 까지 가져온 userID 를 여기에 넣자는 의미. 지금은 땜빵
			rs = pstmt.executeQuery();
			
			while(rs.next()) { //로그인한 사람이 이전에 MyTable 을 저장해둔 경우
				BoardDTO boardDTO = new BoardDTO();
				boardDTO.setBoardID(rs.getInt("boardID"));
				boardDTO.setBoardTitle(rs.getString("boardTitle"));
				boardDTO.setBoardPrice(rs.getInt("boardPrice"));
				boardDTO.setBoardEa(rs.getInt("boardEa"));
				boardDTO.setBoardMemo(rs.getString("boardMemo"));
				boardDTO.setBoardSellerLink(rs.getString("boardSellerLink"));
				boardDTO.setBoardTag(rs.getString("boardTag"));
				boardDTO.setBoardDate(rs.getTimestamp("boardDate"));
				boardDTO.setBoardAvailable(rs.getInt("boardAvailable"));
				boardDTO.setBoardUserID(rs.getString(userID));
				boardDTO.setBoardType(rs.getString("boardType"));
				
				boardList.add(boardDTO);
			}
			System.out.println("읽는중 DAO");
			return boardList; 
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {con.close();}
				if (pstmt != null) {pstmt.close();}
				if (rs != null) {rs.close();}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		return null; //DB 오류
		
	}
	
	public boolean update(BoardDTO boardDTO, int boardID, String id) {
		String sql = null;
		try {
			con = ds.getConnection();
			sql = "update userMyTableBoard set boardTitle = ?, boardPrice = ?, boardEa = ?, boardSellerLink = ?, boardMemo = ?, boardTag = ?, boardDate = ?, boardAvailable = ?, boardType = ? where boardUserID = ? and boardID =?";
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, boardDTO.getBoardTitle());
			pstmt.setInt(2, boardDTO.getBoardPrice());
			pstmt.setInt(3, boardDTO.getBoardEa());
			pstmt.setString(4, boardDTO.getBoardSellerLink());
			pstmt.setString(5, boardDTO.getBoardMemo());
			pstmt.setString(6, boardDTO.getBoardTag());
			pstmt.setTimestamp(7, boardDTO.getBoardDate());
			pstmt.setInt(8, boardDTO.getBoardAvailable());
			pstmt.setString(9, boardDTO.getBoardType());
			pstmt.setString(10, id);
			pstmt.setInt(11, boardID);

			pstmt.executeUpdate();
			
			return true;	
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {con.close();}
				if (pstmt != null) {pstmt.close();}
				if (rs != null) {rs.close();}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false; //DB 오류
	}
	
	public int write(BoardDTO boardDTO) {
		
		//String userID;
		String sql = null;
		try {
			con = ds.getConnection(); 
			sql = "insert into userMyTableBoard values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, boardDTO.getBoardID());
			pstmt.setString(2, boardDTO.getBoardTitle());
			pstmt.setInt(3, boardDTO.getBoardPrice());
			pstmt.setInt(4, boardDTO.getBoardEa());
			pstmt.setString(5, boardDTO.getBoardMemo());
			pstmt.setString(6, boardDTO.getBoardSellerLink());
			pstmt.setString(7, boardDTO.getBoardTag());
			pstmt.setTimestamp(8, boardDTO.getBoardDate());
			pstmt.setInt(9, boardDTO.getBoardAvailable()); //boardAvailable; 처음 작성시 글은 보여진다는 의미. 
			pstmt.setString(10, boardDTO.getBoardUserID()); //로그인에서부터 readAction 까지 가져온 userID 를 여기에 넣자는 의미. 지금은 땜빵
			pstmt.setString(11, boardDTO.getBoardType());
			
			pstmt.executeUpdate();
			
			return 1;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {con.close();}
				if (pstmt != null) {pstmt.close();}
				if (rs != null) {rs.close();}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		return -1; //DB 오류
		
	}

	public int getListCount(String id) { //총 등재된 쇼핑물품의 갯수
		int x = 0;
		String sql = null;
			try {
				con = ds.getConnection();
				sql = "select count(*) from userMyTableBoard where boardUserID = ? and boardAvailable = 1";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, id);
				rs = pstmt.executeQuery();
				
				if(rs.next()) {
					x = rs.getInt(1);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (con != null) {con.close();}
					if (pstmt != null) {pstmt.close();}
					if (rs != null) {rs.close();}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
		return x;
	}

	public List getBoardList(String userID, int page, int limit) {
		String sql = null;
		List list = new ArrayList();
		
		int startrow=(page-1)*5+1; //읽기 시작할 row 번호.
		int endrow=startrow+limit-1; //읽을 마지막 row 번호.	
		
		try {
			con = ds.getConnection();
			
			//rownum 은 where 에서만 사용되고 끝. 출력되는 게시글의 양을 제어해주는 환경변수.
			sql = "select * from (select rownum as rnum, boardID, boardTitle, boardPrice, boardEa, boardMemo, boardSellerLink, boardTag, boardDate, boardAvailable ,boardUserID, boardType  from (select * from userMyTableBoard where boardUserID = ? and boardAvailable = 1 order by boardID desc)) where rnum >=? and rnum <=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userID);
			pstmt.setInt(2, startrow);
			pstmt.setInt(3, endrow);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				BoardDTO board = new BoardDTO();
				board.setBoardNumber(rs.getInt("rnum"));
				board.setBoardID(rs.getInt("boardID"));
				board.setBoardTitle(rs.getString("boardTitle"));
				board.setBoardPrice(rs.getInt("boardPrice"));
				board.setBoardEa(rs.getInt("boardEa"));
				board.setBoardMemo(rs.getString("boardMemo"));
				board.setBoardSellerLink(rs.getString("boardSellerLink"));
				board.setBoardTag(rs.getString("boardTag"));
				board.setBoardDate(rs.getTimestamp("boardDate"));
				board.setBoardAvailable(rs.getInt("boardAvailable"));
				board.setBoardUserID(rs.getString("boardUserID")); //여기서 세션과 바꿔치기.
				board.setBoardType(rs.getString("boardType"));
				
				list.add(board);
			}
			return list;
		} catch (SQLException e) {
			System.out.println("getBoardList 에러 : " + e);
			e.printStackTrace();
		}finally {
			try {
				if (con != null) {con.close();}
				if (pstmt != null) {pstmt.close();}
				if (rs != null) {rs.close();}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	
		
		
		return null;
	}
	
	public int getBoardListSum(String userID) {
		String sql = null;
		
		int sum = 0; //읽기 시작할 row 번호.
		
		try {
			con = ds.getConnection();
			
			sql = "select sum(priceSum) as PriceSum from (select boardPrice*boardEa as priceSum from userMyTableBoard where boardUserID = ? and boardAvailable = 1)";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			
			
			while(rs.next()) {
				sum = rs.getInt("PriceSum");
			}
			
			return sum;
			
		} catch (SQLException e) {
			System.out.println("getBoardList 에러 : " + e);
			e.printStackTrace();
		}finally {
			try {
				if (con != null) {con.close();}
				if (pstmt != null) {pstmt.close();}
				if (rs != null) {rs.close();}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
	
	public int getBoardListAvg(String userID) {
		String sql = null;
		
		int sum = 0; //읽기 시작할 row 번호.
		
		try {
			con = ds.getConnection();
			
			sql = "select sum(priceSum) / sum(boardEa) as priceAvg from (select boardPrice*boardEa as priceSum, boardEa from userMyTableBoard where boardUserID = ? and boardAvailable = 1)";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			
			
			while(rs.next()) {
				sum = rs.getInt("priceAvg");
			}
			
			return sum;
			
		} catch (SQLException e) {
			System.out.println("getBoardList 에러 : " + e);
			e.printStackTrace();
		}finally {
			try {
				if (con != null) {con.close();}
				if (pstmt != null) {pstmt.close();}
				if (rs != null) {rs.close();}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
	
	public int getBoardListItems(String userID) {
		String sql = null;
		
		int sum = 0; //읽기 시작할 row 번호.
		
		try {
			con = ds.getConnection();
			
			sql = "select sum(boardEa) as noOfItems from userMyTableBoard where boardUserID = ? and boardAvailable = 1 group by boardUserID";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			
			
			while(rs.next()) {
				sum = rs.getInt("noOfItems");
			}
			
			return sum;
			
		} catch (SQLException e) {
			System.out.println("getBoardList 에러 : " + e);
			e.printStackTrace();
		}finally {
			try {
				if (con != null) {con.close();}
				if (pstmt != null) {pstmt.close();}
				if (rs != null) {rs.close();}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	public boolean delete(int boardID, String id) {
		String sql = null;
		try {
			con = ds.getConnection(); 
			sql = "update userMyTableBoard set boardAvailable = ? where boardID = ? and boardUserID =?";
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, 0);
			pstmt.setInt(2, boardID);
			pstmt.setString(3, id);
			
			pstmt.executeUpdate();
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {con.close();}
				if (pstmt != null) {pstmt.close();}
				if (rs != null) {rs.close();}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		return false; //DB 오류
		
	}

	public BoardDTO getDetail(int boardID, String id) {
		BoardDTO board = null;
		String sql = null;

		try {
			con = ds.getConnection(); 
			sql = "select * from userMyTableBoard where boardID = ? and boardUserID =?";
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, boardID);
			pstmt.setString(2, id);

			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				board = new BoardDTO();
				board.setBoardID(rs.getInt("boardID"));
				board.setBoardTitle(rs.getString("boardTitle"));
				board.setBoardPrice(rs.getInt("boardPrice"));
				board.setBoardEa(rs.getInt("boardEa"));
				board.setBoardMemo(rs.getString("boardMemo"));
				board.setBoardSellerLink(rs.getString("boardSellerLink"));
				board.setBoardTag(rs.getString("boardTag"));
				board.setBoardDate(rs.getTimestamp("boardDate"));
				board.setBoardAvailable(rs.getInt("boardAvailable"));
				board.setBoardUserID(rs.getString("boardUserID"));
				board.setBoardType(rs.getString("boardType"));
			}
			return board;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {con.close();}
				if (pstmt != null) {pstmt.close();}
				if (rs != null) {rs.close();}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
		
		return null;
	}

}
