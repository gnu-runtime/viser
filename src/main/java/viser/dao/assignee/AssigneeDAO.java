package viser.dao.assignee;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import viser.domain.assignee.Assignee;
import viser.service.support.jdbc.JdbcTemplate;
import viser.service.support.jdbc.PreparedStatementSetter;
import viser.service.support.jdbc.RowMapper;
import viser.service.support.jdbc.SelectAndSelectSetter;

public class AssigneeDAO {
  private static final Logger logger = LoggerFactory.getLogger(AssigneeDAO.class);
  JdbcTemplate jdbc = new JdbcTemplate();
  
  public int addAssignee(String assigneeMember, String roleName, int boardNum, int cardNum) {
    String sql = "insert into assignees (PM_Num, roleNum, Card_Num) values ("
               + "(select PM_Num from project_members where userId= ?),"
               + "(select roleNum from roles where roleName = ? and Board_Num = ?), ?)";
    return jdbc.generatedExecuteUpdate(sql, new PreparedStatementSetter() {
      @Override
      public void setParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, assigneeMember);
        pstmt.setString(2, roleName);
        pstmt.setInt(3, boardNum);
        pstmt.setInt(4, cardNum);
      }
    }, new RowMapper() {
      @Override
      public Integer mapRow(ResultSet rs) throws SQLException {
        if(rs.next()) {
          return rs.getInt(1);
        }
        return null;
      }
    });
  }
  
  public void addAssignee(int projectMemberNum, int roleNum, int cardNum) {
    String sql = "insert into assignees (PM_Num,roleNum,Card_Num) value(?,?,?)";
    jdbc.executeUpdate(sql, new PreparedStatementSetter() {
      @Override
      public void setParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setInt(1, projectMemberNum);
        pstmt.setInt(2, roleNum);
        pstmt.setInt(3, cardNum);
      }
    });
  }
  
  public void removeAssignee(int assigneeNum) {
    String sql = "delete from assignees where assigneeNum = ?";
    jdbc.executeUpdate(sql, new PreparedStatementSetter() {
      @Override
      public void setParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setInt(1, assigneeNum);
      }
    });
  }
  
  public void updateAssignee(int assigneeNum, String userId, String roleName, int boardNum) {
    String sql = "update assignees set PM_Num = (select PM_Num from project_members where userId = ?),"
                + "roleNum = (select roleNum from roles where roleName = ? and Board_Num = ?) where assigneeNum = ?";
    jdbc.executeUpdate(sql, new PreparedStatementSetter() {
      @Override
      public void setParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, userId);
        pstmt.setString(2, roleName);
        pstmt.setInt(3, boardNum);
        pstmt.setInt(4, assigneeNum);
      }
    });
  }
  
  public List<Assignee> getAssigneeList(int cardNum) {
    String sql = "select * from assignees where Card_Num = ?";
    String sql2 = "select assigneeNum, Card_Num, (select roleName from roles where roleNum = ?) as roleName, "
                + "(select userId from project_members where PM_Num = ?) as userId from assignees where roleNum = ? and PM_Num = ?";
    return jdbc.selectAndSelect(sql, sql2, new PreparedStatementSetter() {
      @Override
      public void setParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setInt(1, cardNum);
      }
    }, new SelectAndSelectSetter() {
      @Override
      public void setParametersBySelect(PreparedStatement pstmt, ResultSet rs) throws SQLException {
          pstmt.setInt(1, rs.getInt("roleNum"));
          pstmt.setInt(2, rs.getInt("PM_Num"));
          pstmt.setInt(3, rs.getInt("roleNum"));
          pstmt.setInt(4, rs.getInt("PM_Num"));
      }
    }, new RowMapper() {
      @Override
      public Assignee mapRow(ResultSet rs) throws SQLException {
        return new Assignee(rs.getInt("assigneeNum"), rs.getInt("Card_Num"), rs.getString("roleName"), rs.getString("userId"));
      }
    });
 }
}