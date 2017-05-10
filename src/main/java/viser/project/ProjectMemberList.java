package viser.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/project/memberlist")
public class ProjectMemberList {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String projectname;
		projectname=(String)request.getAttribute("projectname");
		List memberlist = new ArrayList(); 		//chat멤버 목록을 가져오기 위하여 LIST 객체생성
		ProjectDAO projectDao = new ProjectDAO();
		
		try {
		memberlist = projectDao.getChatMemberList(projectname); 	// 게시물을 LIST 객체에 담습니다. 
		request.setAttribute("memberlist", memberlist);
		
		RequestDispatcher rd = request.getRequestDispatcher("/card.jsp");
		rd.forward(request, response);
	
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
