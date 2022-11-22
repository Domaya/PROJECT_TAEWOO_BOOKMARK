package kr.or.kosa.popup;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.or.kosa.action.Action;
import kr.or.kosa.action.ActionForward;
import kr.or.kosa.dao.BlogDao;
import kr.or.kosa.dao.PopupDao;
import kr.or.kosa.dto.Blog_Board;

public class PopupLikeListService implements Action {
//관리자 - 팝업조회
	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = new ActionForward();
		
		try {
			PopupDao dao = new PopupDao();
			
			List<PopupDao> list = dao.LikeListPopup(request.getParameter(""));
			
			request.setAttribute("blogboardlist", list);
			
			forward.setPath("관리자블로그게시글조회.do");
			forward.setRedirect(false);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("msg", "에러가 발생했습니다.");
			request.setAttribute("url", "main.do");
			forward.setPath("에러페이지");
			forward.setRedirect(true);
		}
		return forward;
	}

}