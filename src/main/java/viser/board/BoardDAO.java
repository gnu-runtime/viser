package viser.board;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * review :
 * XXXDAO에서 Connection 맺는 부분의 중복이 많은데 중복 제거할 수 있지 않을까?
 */
public class BoardDAO {
	private static final Logger logger = LoggerFactory.getLogger(BoardDAO.class);

	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;

	PreparedStatement pstmt2 = null;
	ResultSet rs2 = null;
	
			public void SourceReturn() throws SQLException {

		if (this.conn != null) {
			conn.close();
		}
		if (this.pstmt != null) {
			pstmt.close();
		}
		if (this.rs != null) {
			rs.close();
		}

	}

	public Connection getConnection() throws SQLException {
		Properties props = new Properties();
		InputStream in = BoardDAO.class.getResourceAsStream("/db.properties");
		try {
			props.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String driver = props.getProperty("jdbc.driver");
		String url = props.getProperty("jdbc.url");
		String username = props.getProperty("jdbc.username");
		String password = props.getProperty("jdbc.password");

		try {
			Class.forName(driver);
			return DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			return null;
		}
	}

	public List getBoardList(String projectName) throws SQLException {

		List boardlist = new ArrayList();

		// 목록를 조회하기 위한 쿼리
		
		String sql = "select * from boards where Project_Name = ?";
		
		try {
			conn = getConnection();
			
			// 실행을 위한 쿼리 및 파라미터 저장
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, projectName);
			
			rs = pstmt.executeQuery(); // 쿼리 실행
			
			while (rs.next()) {
					Board board =new Board();
					board.setBoardNum(rs.getInt("Board_Num"));
					board.setBoardName(rs.getString("Board_Name"));
					boardlist.add(board);
			}
			
			return boardlist;

		} catch (Exception e) {
			logger.debug("getBoardList Error : " + e);
		}

		finally { // DB 관련들 객체를 종료
			SourceReturn();
		}

		return null;
	}
	
	public void addBoard(Board board) throws SQLException {
		
		String sql = "insert into boards (Project_Name , Board_Name) values (? , ?)";
		
		try {
			conn = getConnection();		
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, board.getProjectName());
			pstmt.setString(2, board.getBoardName());
			pstmt.executeUpdate();

		} finally {
			SourceReturn();
		}
	}

	public void removeBoard(String boardName) throws SQLException {
		String sql = "delete from boards where Board_Name = ?";

		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, boardName);

			pstmt.executeUpdate();

		} finally {
			SourceReturn();
		}
	}
	
	public void updateBoard(String newName, String preName) throws SQLException {
		String sql = "update boards set Board_Name = ? where Board_Name = ?";
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, newName);
			pstmt.setString(2, preName);

			pstmt.execute();
		} catch (Exception e) {
			logger.debug("Updateproject error : " + e);
		} finally {
			SourceReturn();
		}
	}
}
