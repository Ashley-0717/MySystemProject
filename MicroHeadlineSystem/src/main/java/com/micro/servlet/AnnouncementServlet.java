package com.micro.servlet;

import com.micro.entity.Announcement;
import com.micro.service.AnnouncementService;
import com.micro.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/announcement")
public class AnnouncementServlet extends HttpServlet {
    private final AnnouncementService announcementService = new AnnouncementService();

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
        if (action == null) action = "list";

        switch (action) {
            case "list":
                String keyword = req.getParameter("keyword");
                req.setAttribute("announcements", announcementService.findAll(keyword));
                req.setAttribute("keyword", keyword);
                req.getRequestDispatcher("/admin/announcementManage.jsp").forward(req, resp);
                break;
            case "add":
                Announcement a = new Announcement();
                a.setTitle(req.getParameter("title"));
                a.setContent(req.getParameter("content"));
                a.setRemark(req.getParameter("remark"));
                announcementService.addAnnouncement(a);
                resp.sendRedirect(req.getContextPath() + "/announcement?action=list");
                break;
            case "edit":
                Integer aid = WebUtil.getIntParam(req, "aid", 0);
                announcementService.updateAnnouncement(aid,
                        req.getParameter("title"),
                        req.getParameter("content"),
                        req.getParameter("remark"));
                resp.sendRedirect(req.getContextPath() + "/announcement?action=list");
                break;
            case "delete":
                Integer delId = WebUtil.getIntParam(req, "aid", 0);
                announcementService.deleteAnnouncement(delId);
                resp.sendRedirect(req.getContextPath() + "/announcement?action=list");
                break;
        }
    }
}
