package viser.web.card;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import viser.dao.card.CardDAO;
import viser.domain.card.Card;

@WebServlet("/cards/CreateDueDate")
public class CreateAndUpdateDueDateServlet extends HttpServlet {
  private static final Logger logger = LoggerFactory.getLogger(CreateAndUpdateDueDateServlet.class);
 
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    CardDAO cardDAO = new CardDAO();
    Card card = new Card();
    int cardNum = Integer.parseInt(request.getParameter("cardNum"));
    String dueDate = request.getParameter("duedate");
    
    try {
      cardDAO.updateCardDueDate(dueDate,cardNum);
      card = new Card(cardNum, dueDate);
      
      Gson gson = new Gson();
      String gsonData = gson.toJson(card);
      logger.debug("CreateDueDateServlet : " + gsonData);
      PrintWriter out = response.getWriter();
      out.print(gsonData);
        
    } catch (Exception e) {
      logger.debug("CreateDueDateServlet Error : " + e.getMessage());
    } 
  }
}
