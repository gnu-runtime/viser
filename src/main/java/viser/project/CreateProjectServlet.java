package viser.project;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import viser.support.SessionUtils;
import viser.user.LogInServlet;
import viser.user.User;

@WebServlet("/project/createProject")
public class CreateProjectServlet extends HttpServlet {
	public static Logger logger=LoggerFactory.getLogger(CreateProjectServlet.class);
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Project project = new Project();
		User user = new User();

		/**
		 * review : ProjectDAO를 pjtDao로 줄인 이유는??
		 */
		ProjectDAO pjtDao = new ProjectDAO();
		HttpSession session = req.getSession();
		
		req.setCharacterEncoding("utf-8");
		String userId = new SessionUtils().getStringValue(session, LogInServlet.SESSION_USER_ID);
		String projectName = req.getParameter("projectName");
		
		project.setProjectName(projectName);
		user.setUserId(userId);
		
		try {
			pjtDao.addProject(project);
			pjtDao.addprojectMember(project, user, 1);
			
			resp.sendRedirect("/project/projectlist");
			
		} catch (Exception e) {
			logger.debug("Project create Fail : " + e);
		}
		
	}
}
