package com.micro.servlet;

import com.micro.entity.Comment;
import com.micro.service.CommentService;
import com.micro.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/comment")
public class CommentServlet extends HttpServlet {
    private final CommentService commentService = new CommentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!WebUtil.isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/user?action=toLogin");
            return;
        }
        String action = req.getParameter("action");
        Integer uid = WebUtil.getLoginUid(req);
        boolean isAdmin = WebUtil.isAdmin(req);

        if ("add".equals(action)) {
            Integer hid = WebUtil.getIntParam(req, "hid", 0);
            String content = req.getParameter("content");
            String parentCidStr = req.getParameter("parentCid");
            Integer parentCid = null;
            if (parentCidStr != null && !parentCidStr.trim().isEmpty()) {
                parentCid = Integer.parseInt(parentCidStr);
            }
            Comment c = new Comment();
            c.setHid(hid);
            c.setUid(uid);
            c.setParentCid(parentCid);
            c.setContent(content);
            commentService.addComment(c);
            resp.sendRedirect(req.getContextPath() + "/headline?action=detail&hid=" + hid);
        } else if ("delete".equals(action)) {
            Integer cid = WebUtil.getIntParam(req, "cid", 0);
            Integer hid = WebUtil.getIntParam(req, "hid", 0);
            commentService.deleteComment(cid, uid, isAdmin);
            resp.sendRedirect(req.getContextPath() + "/headline?action=detail&hid=" + hid);
        }
    }
}
