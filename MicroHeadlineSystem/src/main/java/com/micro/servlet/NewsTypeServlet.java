package com.micro.servlet;

import com.micro.service.NewsTypeService;
import com.micro.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/type")
public class NewsTypeServlet extends HttpServlet {
    private final NewsTypeService typeService = new NewsTypeService();

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
                req.setAttribute("types", typeService.listAll());
                req.getRequestDispatcher("/admin/typeManage.jsp").forward(req, resp);
                break;
            case "add":
                String name = req.getParameter("typeName");
                typeService.addType(name);
                resp.sendRedirect(req.getContextPath() + "/type?action=list");
                break;
            case "edit":
                Integer typeId = WebUtil.getIntParam(req, "typeId", 0);
                String newName = req.getParameter("typeName");
                typeService.updateType(typeId, newName);
                resp.sendRedirect(req.getContextPath() + "/type?action=list");
                break;
            case "delete":
                Integer delId = WebUtil.getIntParam(req, "typeId", 0);
                int r = typeService.deleteType(delId);
                if (r == 1) req.getSession().setAttribute("msg", "该类型下有头条，不可删除");
                resp.sendRedirect(req.getContextPath() + "/type?action=list");
                break;
        }
    }
}
