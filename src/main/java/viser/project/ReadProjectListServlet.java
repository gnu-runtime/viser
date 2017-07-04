package viser.project;

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

@WebServlet("/project/projectlist")
public class ReadProjectListServlet extends HttpServlet{
	public static Logger logger=LoggerFactory.getLogger(ReadProjectListServlet.class);
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		HttpSession session=request.getSession();
		/**
		 * review : "형근 :" 이렇게 안써도 누가 기록했는지 깃 이력에 다 남아 있음
		 */
		session.removeAttribute("projectName"); //형근: 목록으로돌와왔을때 이전에 세션에 저장했던 프로젝트 이름 삭제하기위해
		logger.debug("ReadProjectListServlet에서 세션에서 불러온  projectname:"+(String)session.getAttribute("projectname")); //형근: 프로젝트 목록으로돌와왔을때 이전에 세션에 저장했던 프로젝트 이름이 잘 삭제되었는지 확인
		ProjectDAO projectDao = new ProjectDAO();
		try {
			request.setAttribute("isReadProject", true);
			request.setAttribute("list", projectDao.getProjectList((String)session.getAttribute("userId")));//형근: 세션에 저장된 유저 id로 projectlit조회
			
			RequestDispatcher rd = request.getRequestDispatcher("/list.jsp");
			rd.forward(request, response);
		} catch (SQLException e) {
			logger.debug("ReadProjectListServlet error:"+e.getMessage());
		}
	}
}
