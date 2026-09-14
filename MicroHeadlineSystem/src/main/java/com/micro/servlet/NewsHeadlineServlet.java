package com.micro.servlet;

import com.micro.entity.NewsHeadline;
import com.micro.entity.NewsType;
import com.micro.entity.PageBean;
import com.micro.service.NewsHeadlineService;
import com.micro.service.NewsTypeService;
import com.micro.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/headline")
public class NewsHeadlineServlet extends HttpServlet {
    private final NewsHeadlineService headlineService = new NewsHeadlineService();
    private final NewsTypeService typeService = new NewsTypeService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "list":
                list(req, resp);
                break;
            case "detail":
                detail(req, resp);
                break;
            case "my":
                myHeadlines(req, resp);
                break;
            case "toPublish":
                toPublish(req, resp);
                break;
            case "publish":
                publish(req, resp);
                break;
            case "toEdit":
                toEdit(req, resp);
                break;
            case "edit":
                edit(req, resp);
                break;
            case "delete":
                delete(req, resp);
                break;
            default:
                list(req, resp);
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int pageNum = WebUtil.getIntParam(req, "pageNum", 1);
        int pageSize = 10;
        String keyword = req.getParameter("keyword");
        Integer typeId = WebUtil.getIntParam(req, "typeId", 0);
        if (typeId == 0) typeId = null;

        PageBean<NewsHeadline> pageBean = headlineService.findPage(pageNum, pageSize, keyword, typeId);
        List<NewsType> types = typeService.listAll();

        req.setAttribute("pageBean", pageBean);
        req.setAttribute("types", types);
        req.setAttribute("keyword", keyword);
        req.setAttribute("typeId", typeId);
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    private void detail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer hid = WebUtil.getIntParam(req, "hid", 0);
        String order = req.getParameter("order");
        if (order == null) order = "desc";

        NewsHeadline headline = headlineService.viewDetail(hid);
        if (headline == null) {
            req.setAttribute("msg", "头条不存在或已被删除");
            list(req, resp);
            return;
        }

        Integer uid = WebUtil.getLoginUid(req);
        boolean liked = false;
        if (uid != null) {
            liked = new com.micro.service.NewsLikeService().isLiked(uid, hid);
        }

        req.setAttribute("headline", headline);
        req.setAttribute("liked", liked);
        req.setAttribute("order", order);
        req.getRequestDispatcher("/user/newsDetail.jsp").forward(req, resp);
    }

    private void myHeadlines(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!WebUtil.isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/user?action=toLogin");
            return;
        }
        Integer uid = WebUtil.getLoginUid(req);
        String keyword = req.getParameter("keyword");
        Integer typeId = WebUtil.getIntParam(req, "typeId", 0);
        if (typeId == 0) typeId = null;

        List<NewsHeadline> myList = headlineService.findMyHeadlines(uid, keyword, typeId);
        List<NewsType> types = typeService.listAll();

        req.setAttribute("myList", myList);
        req.setAttribute("types", types);
        req.setAttribute("keyword", keyword);
        req.getRequestDispatcher("/user/myNews.jsp").forward(req, resp);
    }

    private void toPublish(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!WebUtil.isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/user?action=toLogin");
            return;
        }
        List<NewsType> types = typeService.listAll();
        req.setAttribute("types", types);
        req.getRequestDispatcher("/user/publishNews.jsp").forward(req, resp);
    }

    private void publish(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!WebUtil.isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/user?action=toLogin");
            return;
        }
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        Integer typeId = WebUtil.getIntParam(req, "typeId", 0);
        Integer uid = WebUtil.getLoginUid(req);

        NewsHeadline h = new NewsHeadline();
        h.setTitle(title);
        h.setContent(content);
        h.setTypeId(typeId);
        h.setPublisherUid(uid);
        headlineService.publish(h);

        resp.sendRedirect(req.getContextPath() + "/headline?action=my");
    }

    private void toEdit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!WebUtil.isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/user?action=toLogin");
            return;
        }
        Integer hid = WebUtil.getIntParam(req, "hid", 0);
        NewsHeadline h = headlineService.findById(hid);
        List<NewsType> types = typeService.listAll();
        req.setAttribute("headline", h);
        req.setAttribute("types", types);
        req.getRequestDispatcher("/user/editNews.jsp").forward(req, resp);
    }

    private void edit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!WebUtil.isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/user?action=toLogin");
            return;
        }
        Integer hid = WebUtil.getIntParam(req, "hid", 0);
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        Integer uid = WebUtil.getLoginUid(req);

        int r = headlineService.updateByUser(hid, uid, title, content);
        if (r == -2) {
            req.setAttribute("msg", "无权修改他人头条");
        }
        resp.sendRedirect(req.getContextPath() + "/headline?action=my");
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!WebUtil.isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/user?action=toLogin");
            return;
        }
        Integer hid = WebUtil.getIntParam(req, "hid", 0);
        Integer uid = WebUtil.getLoginUid(req);
        headlineService.deleteByUser(hid, uid);
        resp.sendRedirect(req.getContextPath() + "/headline?action=my");
    }
}
