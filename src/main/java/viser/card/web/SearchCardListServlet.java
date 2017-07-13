package viser.card.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import viser.card.CardDAO;

@WebServlet("/card/Searchlist")
public class SearchCardListServlet extends HttpServlet {
  private static final Logger logger = LoggerFactory.getLogger(SearchCardListServlet.class);

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    CardDAO cardDAO = new CardDAO();
    List list = new ArrayList();

    int page = 1; // 기본 페이지
    int limit = 10; // 최대 페이지

    // 사용자의 요청(req)을 통해 "page" 파라미터가 있는 확인
    if (request.getParameter("page") != null) {
      page = Integer.parseInt(request.getParameter("page"));
    }

    request.setCharacterEncoding("utf-8");
    String keyfield = request.getParameter("keyField");
    String keyword = request.getParameter("keyWord");

    int listcount;

    try {
      logger.debug(keyfield + " " + keyword);
      // list = cardDao.getSearchcardList(page, limit, keyfield, keyword);
      // // 게시물을 LIST 객체에 담습니다.
      listcount = list.size() + 1; // 게시물의 총 개수를 가져옵니다.

      // 최대 페이지를 구합니다.
      int maxpage = (int) ((double) listcount / limit + 0.95); // 0.95
                                                               // 올림처리

      // 시작 페이지를 구합니다. ex ) start page count(1, 11, 21...)
      int startpage = (((int) ((double) page / 10 + 0.9)) - 1) * 10 + 1;

      // 마지막 페이지를 구합니다. ex ) last page count(10, 20, 30...)
      int endpage = maxpage;
      if (endpage > startpage + 10 - 1)
        endpage = startpage + 10 - 1;

      request.setAttribute("page", page); // 현재 페이지
      request.setAttribute("maxpage", maxpage); // 최대 페이지
      request.setAttribute("startpage", startpage); // 시작 페이지
      request.setAttribute("endpage", endpage); // 마지막 페이지
      request.setAttribute("count", listcount); // 게시물 총 개수
      request.setAttribute("list", list);

      RequestDispatcher rd = request.getRequestDispatcher("/card_list.jsp");
      rd.forward(request, response);

    } catch (Exception e) {
      System.out.println(e);
    }

  }

}
