package com.micro.servlet;

import com.micro.service.NewsLikeService;
import com.micro.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/like")
public class NewsLikeServlet extends HttpServlet {
    private final NewsLikeService likeService = new NewsLikeService();

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
        Integer hid = WebUtil.getIntParam(req, "hid", 0);
        Integer uid = WebUtil.getLoginUid(req);

        if ("do".equals(action)) {
            int r = likeService.doLike(uid, hid);
            if (r == 1) req.getSession().setAttribute("msg", "您已点赞过该头条");
        } else if ("cancel".equals(action)) {
            likeService.cancelLike(uid, hid);
        }
        resp.sendRedirect(req.getContextPath() + "/headline?action=detail&hid=" + hid);
    }
}
