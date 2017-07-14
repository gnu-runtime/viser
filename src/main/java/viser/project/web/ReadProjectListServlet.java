package viser.project.web;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import viser.project.Project;
import viser.project.ProjectDAO;

@WebServlet("/project/projectlist")
public class ReadProjectListServlet extends HttpServlet {
  public static Logger logger = LoggerFactory.getLogger(ReadProjectListServlet.class);

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession();
    List<Project> projectlist = new ArrayList<Project>();
    ProjectDAO projectDAO = new ProjectDAO();
    
    session.removeAttribute("projectName");
    logger.debug("ReadProjectListServlet에서 세션에서 불러온  projectname:" + (String) session.getAttribute("projectname"));

    try {
      projectlist = projectDAO.getProjectList((String) session.getAttribute("userId"));
      request.setAttribute("isReadProject", true);
      request.setAttribute("list", projectlist);

      RequestDispatcher rd = request.getRequestDispatcher("/list.jsp");
      rd.forward(request, response);
    } catch (SQLException e) {
      logger.debug("ReadProjectListServlet error:" + e.getMessage());
    }
  }
}