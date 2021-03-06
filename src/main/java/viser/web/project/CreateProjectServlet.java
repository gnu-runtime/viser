package viser.web.project;

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

import viser.dao.project.ProjectDAO;
import viser.domain.project.Project;
import viser.domain.user.User;
import viser.service.support.SessionUtils;
import viser.web.user.LogInServlet;

@WebServlet("/project/createProject")
public class CreateProjectServlet extends HttpServlet {
  public static Logger logger = LoggerFactory.getLogger(CreateProjectServlet.class);

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Project project = new Project();
    User user = new User();

    ProjectDAO projectDAO = new ProjectDAO();
    HttpSession session = request.getSession();

    String userId = new SessionUtils().getStringValue(session, LogInServlet.SESSION_USER_ID);
    String projectName = request.getParameter("projectName");

    project.setProjectName(projectName);
    user.setUserId(userId);

    try {
      projectDAO.addProject(project);
      projectDAO.addprojectMember(project, user, 1);

      response.sendRedirect("/project/projectlist");

    } catch (Exception e) {
      logger.debug("Project create Fail : " + e);
    }

  }
}
