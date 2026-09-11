package com.micro.servlet;

import com.micro.entity.User;
import com.micro.service.UserService;
import com.micro.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/user")
public class UserServlet extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "toLogin";

        switch (action) {
            case "toLogin":
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                break;
            case "toRegister":
                req.getRequestDispatcher("/register.jsp").forward(req, resp);
                break;
            case "register":
                register(req, resp);
                break;
            case "login":
                login(req, resp);
                break;
            case "logout":
                logout(req, resp);
                break;
            case "personal":
                personal(req, resp);
                break;
            case "updatePwd":
                updatePwd(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/user?action=toLogin");
        }
    }

    private void register(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirmPwd = req.getParameter("confirmPwd");
        String nickname = req.getParameter("nickname");

        if (!password.equals(confirmPwd)) {
            req.setAttribute("msg", "两次输入密码不一致");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname);
        int res = userService.register(user);
        if (res == 1) {
            req.setAttribute("msg", "用户名已存在");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        } else {
            req.setAttribute("msg", "注册成功，请登录");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        User loginUser = userService.login(username, password);
        if (loginUser == null) {
            req.setAttribute("msg", "用户名或密码错误");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }
        HttpSession session = req.getSession();
        session.setAttribute("loginUid", loginUser.getUid());
        session.setAttribute("loginNick", loginUser.getNickname());
        session.setAttribute("loginRole", loginUser.getRole());
        if (loginUser.getRole() == 1) {
            resp.sendRedirect(req.getContextPath() + "/admin?action=index");
        } else {
            resp.sendRedirect(req.getContextPath() + "/headline?action=list");
        }
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.getSession().invalidate();
        resp.sendRedirect(req.getContextPath() + "/user?action=toLogin");
    }

    private void personal(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!WebUtil.isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/user?action=toLogin");
            return;
        }
        Integer uid = WebUtil.getLoginUid(req);
        User user = userService.findByUid(uid);
        req.setAttribute("user", user);
        req.getRequestDispatcher("/user/personal.jsp").forward(req, resp);
    }

    private void updatePwd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!WebUtil.isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/user?action=toLogin");
            return;
        }
        Integer uid = WebUtil.getLoginUid(req);
        String oldPwd = req.getParameter("oldPwd");
        String newPwd = req.getParameter("newPwd");
        String confirmNew = req.getParameter("confirmNewPwd");

        if (!newPwd.equals(confirmNew)) {
            req.setAttribute("msg", "两次新密码不一致");
            personal(req, resp);
            return;
        }
        int r = userService.updatePassword(uid, oldPwd, newPwd);
        if (r == 1) {
            req.setAttribute("msg", "旧密码不正确");
            personal(req, resp);
        } else {
            req.getSession().invalidate();
            req.setAttribute("msg", "密码修改成功，请重新登录");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
