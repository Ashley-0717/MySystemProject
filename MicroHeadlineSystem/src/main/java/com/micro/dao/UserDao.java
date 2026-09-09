package com.micro.dao;

import com.micro.entity.User;

import java.sql.ResultSet;
import java.util.List;

public class UserDao extends BaseDao {

    public User findByUsername(String username) {
        String sql = "select * from `user` where username = ?";
        return queryOne(this::buildUser, sql, username);
    }

    public User findByUid(Integer uid) {
        String sql = "select * from `user` where uid = ?";
        return queryOne(this::buildUser, sql, uid);
    }

    public int addUser(User user) {
        String sql = "insert into `user`(username, password, nickname, role) values (?,?,?,?)";
        return insertReturnKey(sql, user.getUsername(), user.getPassword(), user.getNickname(), user.getRole());
    }

    public int updatePassword(Integer uid, String newPwd) {
        String sql = "update `user` set password = ? where uid = ?";
        return update(sql, newPwd, uid);
    }

    public List<User> listAll() {
        String sql = "select * from `user` order by register_time desc";
        return queryList(this::buildUser, sql);
    }

    public int deleteUser(Integer uid) {
        String sql = "delete from `user` where uid = ? and role = 0";
        return update(sql, uid);
    }

    private User buildUser(ResultSet rs) throws java.sql.SQLException {
        User u = new User();
        u.setUid(rs.getInt("uid"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setNickname(rs.getString("nickname"));
        u.setRole(rs.getInt("role"));
        u.setRegisterTime(rs.getTimestamp("register_time"));
        return u;
    }
}
