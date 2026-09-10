package com.micro.service;

import com.micro.dao.UserDao;
import com.micro.entity.User;
import com.micro.util.MD5Util;

import java.util.List;

public class UserService {
    private final UserDao userDao = new UserDao();

    /** 注册：返回0成功，1用户名已存在 */
    public int register(User user) {
        User exist = userDao.findByUsername(user.getUsername());
        if (exist != null) return 1;
        user.setPassword(MD5Util.md5(user.getPassword()));
        user.setRole(0);
        userDao.addUser(user);
        return 0;
    }

    /** 登录 */
    public User login(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user == null) return null;
        if (!user.getPassword().equals(MD5Util.md5(password))) return null;
        return user;
    }

    /** 修改密码：0成功，1旧密码错误 */
    public int updatePassword(Integer uid, String oldPwd, String newPwd) {
        User user = userDao.findByUid(uid);
        if (user == null) return 1;
        if (!user.getPassword().equals(MD5Util.md5(oldPwd))) return 1;
        userDao.updatePassword(uid, MD5Util.md5(newPwd));
        return 0;
    }

    public User findByUid(Integer uid) {
        return userDao.findByUid(uid);
    }

    public List<User> listAll() {
        return userDao.listAll();
    }

    public int deleteUser(Integer uid) {
        return userDao.deleteUser(uid);
    }
}
