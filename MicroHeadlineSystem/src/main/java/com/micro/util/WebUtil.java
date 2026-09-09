package com.micro.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class WebUtil {
    public static Integer getLoginUid(HttpServletRequest req) {
        HttpSession session = req.getSession();
        return (Integer) session.getAttribute("loginUid");
    }

    public static Integer getLoginRole(HttpServletRequest req) {
        HttpSession session = req.getSession();
        return (Integer) session.getAttribute("loginRole");
    }

    public static String getLoginNick(HttpServletRequest req) {
        HttpSession session = req.getSession();
        return (String) session.getAttribute("loginNick");
    }

    public static boolean isLoggedIn(HttpServletRequest req) {
        return getLoginUid(req) != null;
    }

    public static boolean isAdmin(HttpServletRequest req) {
        Integer role = getLoginRole(req);
        return role != null && role == 1;
    }

    public static int getIntParam(HttpServletRequest req, String name, int def) {
        String v = req.getParameter(name);
        if (v == null || v.trim().isEmpty()) return def;
        try { return Integer.parseInt(v.trim()); } catch (Exception e) { return def; }
    }
}
