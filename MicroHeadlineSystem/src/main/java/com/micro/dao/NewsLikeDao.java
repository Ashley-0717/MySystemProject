package com.micro.dao;

import com.micro.entity.NewsLike;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class NewsLikeDao extends BaseDao {

    /** 判断用户是否已点赞（支持事务连接） */
    public boolean isUserLiked(Connection conn, Integer uid, Integer hid) throws SQLException {
        String sql = "select count(*) from news_like where uid = ? and hid = ?";
        if (conn != null) {
            List<Long> list = queryListWithConn(conn, rs -> rs.getLong(1), sql, uid, hid);
            return list.size() > 0 && list.get(0) > 0;
        }
        return queryCount(sql, uid, hid) > 0;
    }

    /** 插入点赞记录（事务内） */
    public int insertLike(Connection conn, Integer uid, Integer hid) throws SQLException {
        String sql = "insert into news_like(hid, uid) values (?,?)";
        return updateWithConn(conn, sql, hid, uid);
    }

    /** 删除点赞记录（事务内） */
    public int deleteLike(Connection conn, Integer uid, Integer hid) throws SQLException {
        String sql = "delete from news_like where uid = ? and hid = ?";
        return updateWithConn(conn, sql, uid, hid);
    }

    /** 查询某头条点赞数 */
    public long countByHid(Integer hid) {
        String sql = "select count(*) from news_like where hid = ?";
        return queryCount(sql, hid);
    }
}
