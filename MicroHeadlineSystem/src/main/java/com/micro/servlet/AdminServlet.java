package com.micro.servlet;

import com.micro.entity.Comment;
import com.micro.entity.NewsHeadline;
import com.micro.entity.NewsType;
import com.micro.entity.User;
import com.micro.service.*;
import com.micro.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    private final UserService userService = new UserService();
    private final NewsHeadlineService headlineService = new NewsHeadlineService();
    private final NewsTypeService typeService = new NewsTypeService();
    private final CommentService commentService = new CommentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!WebUtil.isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/user?action=toLogin");
            return;
        }
        String action = req.getParameter("action");
        if (action == null) action = "index";

        switch (action) {
            case "index":
                index(req, resp);
                break;
            case "userManage":
                userManage(req, resp);
                break;
            case "deleteUser":
                deleteUser(req, resp);
                break;
            case "newsManage":
                newsManage(req, resp);
                break;
            case "deleteNews":
                deleteNews(req, resp);
                break;
            case "editNews":
                editNews(req, resp);
                break;
            case "commentManage":
                commentManage(req, resp);
                break;
            case "deleteComment":
                deleteComment(req, resp);
                break;
            case "hotRank":
                hotRank(req, resp);
                break;
            default:
                index(req, resp);
        }
    }

    private void index(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/admin/adminIndex.jsp").forward(req, resp);
    }

    private void userManage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<User> users = userService.listAll();
        req.setAttribute("users", users);
        req.getRequestDispatcher("/admin/userManage.jsp").forward(req, resp);
    }

    private void deleteUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer uid = WebUtil.getIntParam(req, "uid", 0);
        userService.deleteUser(uid);
        resp.sendRedirect(req.getContextPath() + "/admin?action=userManage");
    }

    private void newsManage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        Integer typeId = WebUtil.getIntParam(req, "typeId", 0);
        if (typeId == 0) typeId = null;
        Integer isDeleted = WebUtil.getIntParam(req, "isDeleted", -1);
        if (isDeleted == -1) isDeleted = null;

        List<NewsHeadline> headlines = headlineService.findAllForAdmin(keyword, typeId, null, isDeleted);
        List<NewsType> types = typeService.listAll();
        req.setAttribute("headlines", headlines);
        req.setAttribute("types", types);
        req.setAttribute("keyword", keyword);
        req.setAttribute("typeId", typeId);
        req.setAttribute("isDeleted", isDeleted);
        req.getRequestDispatcher("/admin/newsManage.jsp").forward(req, resp);
    }

    private void deleteNews(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer hid = WebUtil.getIntParam(req, "hid", 0);
        headlineService.deleteByAdmin(hid);
        resp.sendRedirect(req.getContextPath() + "/admin?action=newsManage");
    }

    private void editNews(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer hid = WebUtil.getIntParam(req, "hid", 0);
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        Integer typeId = WebUtil.getIntParam(req, "typeId", 0);
        headlineService.updateByAdmin(hid, title, content, typeId);
        resp.sendRedirect(req.getContextPath() + "/admin?action=newsManage");
    }

    private void commentManage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer hid = WebUtil.getIntParam(req, "hid", 0);
        Integer uid = WebUtil.getIntParam(req, "uid", 0);
        if (hid == 0) hid = null;
        if (uid == 0) uid = null;
        List<Comment> comments = commentService.findAllForAdmin(hid, uid);
        req.setAttribute("comments", comments);
        req.getRequestDispatcher("/admin/commentManage.jsp").forward(req, resp);
    }

    private void deleteComment(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer cid = WebUtil.getIntParam(req, "cid", 0);
        commentService.deleteComment(cid, null, true);
        resp.sendRedirect(req.getContextPath() + "/admin?action=commentManage");
    }

    private void hotRank(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sortBy = req.getParameter("sortBy");
        if (sortBy == null) sortBy = "like";
        int pageNum = WebUtil.getIntParam(req, "pageNum", 1);
        int pageSize = 10;
        List<NewsHeadline> rankList = headlineService.hotRank(sortBy, pageNum, pageSize);
        req.setAttribute("rankList", rankList);
        req.setAttribute("sortBy", sortBy);
        req.getRequestDispatcher("/admin/hotRank.jsp").forward(req, resp);
    }
}
