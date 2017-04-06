package viser; // 우철 - 개인정보수정폼 개발

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import viser.user.User;
import viser.user.UserDAO;

@WebServlet("/users/updateForm")
public class UpdateFormUserServlet extends HttpServlet{
	
	private static final Logger logger = LoggerFactory.getLogger(UpdateFormUserServlet.class);
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		
			HttpSession session = req.getSession();
			Object obj = session.getAttribute(LoginServlet.SESSION_USER_ID);
			String userId;
			
			if(obj == null) {
				resp.sendRedirect("/");
				return;
			}
			
			userId = (String)obj;
			logger.debug("User Id : " + userId);
			UserDAO userDao = new UserDAO();
	
			try {
				
				User user = userDao.findByUserId(userId);
				req.setAttribute("user", user);
				RequestDispatcher rd = req.getRequestDispatcher("/form.jsp");
				rd.forward(req, resp);
				
			} catch (SQLException e) {
			}
			
			
	}
	

}