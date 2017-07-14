package viser.board;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import viser.project.ProjectDAO;
import viser.project.ProjectDAOTest;

public class BoardDAOTest {

  private static final Logger logger = LoggerFactory.getLogger(BoardDAOTest.class);
  private static Board TEST_BOARD = new Board("TEST_BOARD", ProjectDAOTest.TEST_PROJECT.getProjectName());
  private ProjectDAO projectDAO;
  private BoardDAO boardDAO;

  @Before
  public void setup() throws SQLException {
    boardDAO = new BoardDAO();
    projectDAO = new ProjectDAO();
    projectDAO.removeProject(ProjectDAOTest.TEST_PROJECT.getProjectName());
    projectDAO.addProject(ProjectDAOTest.TEST_PROJECT);
  }

  @After
  public void returns() throws SQLException {
    projectDAO.removeProject(ProjectDAOTest.TEST_PROJECT.getProjectName());
  }

  @Test
  public void Connection() throws SQLException {
    Connection con = boardDAO.getConnection();
    assertNotNull(con);
  }

  @Test
  public void crud() throws SQLException {
    Board board = TEST_BOARD;
    boardDAO.removeBoard(board.getBoardName());
    boardDAO.addBoard(board);
    board.setBoardNum(boardDAO.getBoardNum(board.getBoardName(), ProjectDAOTest.TEST_PROJECT.getProjectName()));
    logger.debug("board : {}", board);
    Board dbBoard = boardDAO.findByBoardNum(board.getBoardNum());
    
    assertEquals(board.getBoardName(), dbBoard.getBoardName());
    logger.debug("dbBoard : {}", dbBoard);

    Board UpdateBoard = new Board();
    UpdateBoard.setBoardName("UpdateBoard");
    
    boardDAO.updateBoard(UpdateBoard.getBoardName(), dbBoard.getBoardName());
    logger.debug("boardNum = " + board.getBoardNum());
    dbBoard = boardDAO.findByBoardNum(board.getBoardNum());
    assertEquals(dbBoard.getBoardName(), UpdateBoard.getBoardName());
  }

  @Test
  public void getList() throws SQLException {
    logger.debug("list : {}", boardDAO.getBoardList(ProjectDAOTest.TEST_PROJECT.getProjectName()));
    assertNotNull(boardDAO.getBoardList(ProjectDAOTest.TEST_PROJECT.getProjectName()));
  }
}
