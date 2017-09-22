package viser.web.role;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import viser.dao.role.RoleDAO;
import viser.service.support.SessionUtils;

@WebServlet("/roles/readRoleList")
public class ReadRoleListServlet extends HttpServlet {
  private static final Logger logger = LoggerFactory.getLogger(ReadRoleListServlet.class);
  
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession();
    RoleDAO roleDAO = new RoleDAO();
    
    String projectName = SessionUtils.getStringValue(session, "projectName");
    
    try {
      List roleList = roleDAO.getRoleList(projectName);
      Gson gson = new Gson();
      String gsonData = gson.toJson(roleList);
      PrintWriter out = response.getWriter();
      out.print(gsonData);
      logger.debug("RoleList gsonData : {}", gsonData);
    } catch (Exception e) {
      logger.debug("ReadRoleListServlet Error : " + e.getMessage());
    }
  }
}
