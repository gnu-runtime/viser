package viser.dao.card;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import viser.dao.cardlist.CardListDAO;
import viser.domain.card.Card;
import viser.domain.cardlist.CardList;
import viser.service.support.jdbc.JdbcTemplate;
import viser.service.support.jdbc.PreparedStatementSetter;
import viser.service.support.jdbc.RowMapper;
import viser.service.support.jdbc.SelectAndUpdateSetter;

public class CardDAO {
  private static final Logger logger = LoggerFactory.getLogger(CardDAO.class);
  JdbcTemplate jdbc = new JdbcTemplate();

  public List<Card> getCards(int listNum) throws SQLException {
    String sql = "select Card_Num,Subject from cards where List_Num=? && taskOrder>=0 order by Card_Order asc";
    return jdbc.list(sql, new PreparedStatementSetter() {
      @Override
      public void setParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setInt(1, listNum);
      }
    }, new RowMapper() {
      @Override
      public Card mapRow(ResultSet rs) throws SQLException {
        Card card = new Card();
        card.setCardNum(rs.getInt("Card_Num"));
        card.setSubject(rs.getString("Subject"));
        return card;
      }
    });
  }
  
  public List<Card> getCardsForGantt(int listNum){
    String sql = "select * from cards where List_Num=? order by taskOrder asc";
    return jdbc.list(sql, new PreparedStatementSetter() {
      @Override
      public void setParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setInt(1, listNum);
      }
    }, new RowMapper() {
      @Override
      public Card mapRow(ResultSet rs) throws SQLException {
        Card card = new Card();
        card.setCardNum(rs.getInt("Card_Num"));
        card.setUserId(rs.getString("userId"));
        card.setSubject(rs.getString("Subject"));
        card.setContent(rs.getString("Content"));
        card.setProgress(rs.getInt("progress"));
        card.setLevel(rs.getInt("level"));
        card.setStatus(rs.getString("status"));
        card.setCanWrite(rs.getBoolean("canWrite"));
        card.setStart(rs.getLong("start"));
        card.setDuration(rs.getInt("duration"));
        card.setHasChild(rs.getBoolean("hasChild"));
        card.setAssigUnchanged(true);
        card.setUnchanged(true);
        return card;
      }
    });
  }

  public int addCard(Card card) {
    String sql = "insert into cards(userId, Subject, Content, List_Num, Card_Order, DueDate) values(?,?,?,?,?,?)";
    return jdbc.generatedExecuteUpdate(sql, new PreparedStatementSetter() {
      @Override
      public void setParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, card.getUserId());
        pstmt.setString(2, card.getSubject());
        pstmt.setString(3, card.getContent());
        pstmt.setInt(4, card.getListNum());
        pstmt.setInt(5, card.getCardOrder());
        pstmt.setString(6, card.getDueDate());
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

  public int addCard(Card card,int taskOrder) {
    String sql = "insert into cards(userId, Subject,Content ,List_Num, Card_Order,progress,level,status,canWrite,start,duration,hasChild,taskOrder) values(?,?,?,?,(select max(Card_Order)+1 from cards as cardAdd where List_Num=?),?,?,?,?,?,?,?,?)";
    return jdbc.generatedExecuteUpdate(sql, new PreparedStatementSetter() {
      @Override
      public void setParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, card.getUserId());
        pstmt.setString(2, card.getSubject());
        pstmt.setString(3, card.getContent());
        pstmt.setInt(4, card.getListNum());
        pstmt.setInt(5, card.getListNum());
        pstmt.setInt(6, card.getProgress());
        pstmt.setInt(7, card.getLevel());
        pstmt.setString(8, card.getStatus());
        pstmt.setBoolean(9, card.isCanWrite());
        pstmt.setLong(10, card.getStart());
        pstmt.setInt(11, card.getDuration());
        pstmt.setBoolean(12, card.isHasChild());
        pstmt.setInt(13, taskOrder);
       }
    }, new RowMapper() {
      @Override
      public Integer mapRow(ResultSet rs) throws SQLException {
        if(rs.next())
          return rs.getInt(1);
        return null;
      }
    });
  }
  
  public void addBoardCardForGantt(String userId,String subject,int listNum) {
    String sql = "call CreateBoardTask(?,?,?)";
    jdbc.executeUpdate(sql, new PreparedStatementSetter() {
      @Override
      public void setParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, userId);
        pstmt.setString(2, subject);
        pstmt.setInt(3, listNum);
       }
    });
  }
  
  public void removeCard(int num) {
    // delete card
    String sql="select List_Num,Card_Order from cards where Card_Num=?"; 
    Card deleteCard=jdbc.executeQuery(sql, new PreparedStatementSetter() {
      @Override
      public void setParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setInt(1, num);
      }
    },new RowMapper() {
      @Override
      public Card mapRow(ResultSet rs) throws SQLException {
        while(rs.next()){
          return new Card(rs.getInt(1),rs.getInt(2));
        }
        return null;
      }
    });
    
    sql = "delete from cards where Card_Num = ?";
    jdbc.executeUpdate(sql, new PreparedStatementSetter() {
      @Override
      public void setParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setInt(1, num);
      }
    });

    // select and update cardOrder
    sql = "select Card_Num from cards where List_Num=?&& Card_Order>?";
    String sql2 = "update cards set Card_Order=? where Card_Num=?";
    jdbc.selectAndUpdate(sql, sql2, new PreparedStatementSetter() {
      @Override
      public void setParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setInt(1, deleteCard.getListNum());
        pstmt.setInt(2, deleteCard.getCardOrder());
      }
    }, new SelectAndUpdateSetter() {
      int changeOrder = deleteCard.getCardOrder(); // 삭제되는 위치부터 그 뒤까지 순서 갱신을 위해 사용하는 변수

      @Override
      public void setParametersBySelect(PreparedStatement pstmt, ResultSet rs) throws SQLException {
        pstmt.setInt(1, changeOrder++);
        pstmt.setInt(2, rs.getInt("Card_Num"));
      }
    });
  }

  public Card viewCard(int num) throws SQLException {
    String sql = "select * from cards where Card_Num = ?";
    return jdbc.executeQuery(sql, new PreparedStatementSetter() {
      @Override
      public void setParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setInt(1, num);
      }
    }, new RowMapper() {
      @Override
      public Card mapRow(ResultSet rs) throws SQLException {
        if (rs.next()) {
          return new Card(rs.getInt("Card_Num"), 
                          rs.getString("userId"), 
                          rs.getString("Subject"), 
                          rs.getString("Content"), 
                          rs.getTimestamp("Modify_Time"),
                          rs.getInt("List_Num"),
                          rs.getInt("Card_Order"), 
                          rs.getString("DueDate"));
        }
        return null;
      }
    });
  }

  public void updateCard(Card card) throws SQLException {
    String sql = "update cards set SubJect = ?, Content = ?, Modify_Time = ? where Card_Num = ?";
    jdbc.executeUpdate(sql, new PreparedStatementSetter() {
      @Override
      public void setParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, card.getSubject());
        pstmt.setString(2, card.getContent());
        pstmt.setTimestamp(3, new Timestamp(new Date().getTime()));
        pstmt.setInt(4, card.getCardNum());
      }
    });
  }

  public void updateCard(Card card,int taskOrder){
    String sql="update cards set userId=?, Subject=?,Content=?,Modify_Time=?,progress=?,level=?,status=?,start=?,duration=?,hasChild=?,taskOrder=?,canWrite=? where Card_Num=?";
    jdbc.executeUpdate(sql, new PreparedStatementSetter() {
      @Override
      public void setParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, card.getUserId());
        pstmt.setString(2, card.getSubject());
        pstmt.setString(3, card.getContent());
        pstmt.setTimestamp(4, new Timestamp(new Date().getTime()));
        pstmt.setInt(5, card.getProgress());
        pstmt.setInt(6, card.getLevel());
        pstmt.setString(7, card.getStatus());
        pstmt.setLong(8,card.getStart());
        pstmt.setInt(9, card.getDuration());
        pstmt.setBoolean(10, card.hasChild());
        pstmt.setInt(11, taskOrder);
        pstmt.setBoolean(12, card.isCanWrite());
        pstmt.setInt(13, card.getCardNum());
      }
    });
  }
  
  // cardNum : 기존 위치의 카드 순서 번호, listNum1 : 추가 카드의 리스트 번호, listnum2 : 삭제 카드의 리스트 번호, changeOrder : 인덱스 변화시 사용 순서 번호
  public void updateCardOrder(int boardNum, int currentListOrder, int changeListOrder, int currentCardOrder, int changeCardOrder) {
    String sql, sql2;
    CardListDAO cardListDAO = new CardListDAO();

    // Find listNum
    CardList addedList = new CardList(boardNum, changeListOrder);
    CardList removedList = new CardList(boardNum, currentListOrder);
    final int listNum1 = cardListDAO.getListNum(addedList.getBoardNum(), addedList.getListOrder());
    final int listNum2 = cardListDAO.getListNum(removedList.getBoardNum(), removedList.getListOrder());

    // Find Updated CardNum
    CardDAO cardDAO = new CardDAO();
    final int cardNum = cardDAO.getCardNum(listNum2, currentCardOrder);

    int changeOrder;

    // If Card drag in same list
    if (currentListOrder == changeListOrder) {
      if (currentCardOrder > changeCardOrder) {
        sql = "select Card_Num from cards where List_Num=? && Card_Order>=? && Card_Order<?  order by Card_Order asc";
        sql2 = "update cards set Card_Order = ? where Card_Num=?";
        changeOrder = changeCardOrder + 1;
        jdbc.selectAndUpdate(sql, sql2, new PreparedStatementSetter() {
          @Override
          public void setParameters(PreparedStatement pstmt) throws SQLException {
            pstmt.setInt(1, listNum2);
            pstmt.setInt(2, changeCardOrder);
            pstmt.setInt(3, currentCardOrder);
          }
        }, new SelectAndUpdateSetter() {
          int changeOrder = changeCardOrder + 1;

          @Override
          public void setParametersBySelect(PreparedStatement pstmt, ResultSet rs) throws SQLException {
            pstmt.setInt(1, changeOrder++);
            pstmt.setInt(2, rs.getInt("Card_Num"));
          }
        });
      }

      // Sorting : If previous listOrder < changed listOrder
      else {
        sql = "select Card_Num from cards where List_Num=? && Card_Order>? && Card_Order<=? order by Card_Order asc";
        sql2 = "update cards set Card_Order = ? where Card_Num=?";
        jdbc.selectAndUpdate(sql, sql2, new PreparedStatementSetter() {
          @Override
          public void setParameters(PreparedStatement pstmt) throws SQLException {
            pstmt.setInt(1, listNum2);
            pstmt.setInt(2, currentCardOrder);
            pstmt.setInt(3, changeCardOrder);
          }
        }, new SelectAndUpdateSetter() {
          int changeOrder = currentCardOrder;

          @Override
          public void setParametersBySelect(PreparedStatement pstmt, ResultSet rs) throws SQLException {
            pstmt.setInt(1, changeOrder++);
            pstmt.setInt(2, rs.getInt("Card_Num"));
          }
        });
      }
    }

    // If drag Card in differ list
    else {
      // 카드가 없어지는 리스트에 카드 번호 갱신
      sql = "select Card_Num from cards where List_Num=? && Card_Order>? order by Card_Order asc";
      sql2 = "update cards set Card_Order=? where Card_Num=?";
      jdbc.selectAndUpdate(sql, sql2, new PreparedStatementSetter() {
        @Override
        public void setParameters(PreparedStatement pstmt) throws SQLException {
          pstmt.setInt(1, listNum2);
          pstmt.setInt(2, currentCardOrder);
        }
      }, new SelectAndUpdateSetter() {
        int changeOrder = currentCardOrder;

        @Override
        public void setParametersBySelect(PreparedStatement pstmt, ResultSet rs) throws SQLException {
          pstmt.setInt(1, changeOrder++);
          pstmt.setInt(2, rs.getInt("Card_Num"));
        }
      });

      // 카드가 추가된 리스트에 카드 번호 갱신
      sql = "select Card_Num from cards where List_Num=? && Card_Order>=? order by Card_Order asc";
      sql2 = "update cards set Card_Order=? where Card_Num=?";
      jdbc.selectAndUpdate(sql, sql2, new PreparedStatementSetter() {
        @Override
        public void setParameters(PreparedStatement pstmt) throws SQLException {
          pstmt.setInt(1, listNum1);
          pstmt.setInt(2, changeCardOrder);
        }
      }, new SelectAndUpdateSetter() {
        int changeOrder = changeCardOrder + 1;

        @Override
        public void setParametersBySelect(PreparedStatement pstmt, ResultSet rs) throws SQLException {
          pstmt.setInt(1, changeOrder++);
          pstmt.setInt(2, rs.getInt("Card_Num"));
        }
      });
    }

    // 드래그하여 옮긴 카드 위치 갱신
    sql = "update cards set Card_Order = ?, Modify_Time=? ,List_Num=? where Card_Num = ?";
    jdbc.executeUpdate(sql, new PreparedStatementSetter() {
      @Override
      public void setParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setInt(1, changeCardOrder);
        pstmt.setTimestamp(2, new Timestamp(new Date().getTime()));
        pstmt.setInt(3, listNum1);
        pstmt.setInt(4, cardNum);
      }
    });
  }

  public int getCardNum(int listNum, int cardOrder) {
    String sql = "select Card_Num from cards where List_Num=? && Card_Order=?";
    return jdbc.executeQuery(sql, new PreparedStatementSetter() {
      @Override
      public void setParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setInt(1, listNum);
        pstmt.setInt(2, cardOrder);
      }
    }, new RowMapper() {

      @Override
      public Integer mapRow(ResultSet rs) throws SQLException {
        while (rs.next())
          return rs.getInt("Card_Num");
        return 0;
      }
    });
  }

  public void updateCardDueDate(String dueDate, int cardNum) {
    String sql = "update cards set DueDate = ? where Card_Num = ?";
    jdbc.executeUpdate(sql, new PreparedStatementSetter() {
      @Override
      public void setParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, dueDate);
        pstmt.setInt(2, cardNum);
      }
    });
  }
}
