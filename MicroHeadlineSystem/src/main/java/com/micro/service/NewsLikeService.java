package com.micro.service;

import com.micro.dao.NewsHeadlineDao;
import com.micro.dao.NewsLikeDao;
import com.micro.util.DruidUtil;

import java.sql.Connection;
import java.sql.SQLException;

public class NewsLikeService {
    private final NewsLikeDao likeDao = new NewsLikeDao();
    private final NewsHeadlineDao headlineDao = new NewsHeadlineDao();

    /**
     * 点赞（事务）：插入点赞记录 + like_count+1
     * @return 0成功，1已点赞，-1失败
     */
    public int doLike(Integer uid, Integer hid) {
        Connection conn = null;
        try {
            conn = DruidUtil.getConnection();
            conn.setAutoCommit(false);

            if (likeDao.isUserLiked(conn, uid, hid)) {
                conn.rollback();
                return 1;
            }
            likeDao.insertLike(conn, uid, hid);
            headlineDao.incLikeCount(conn, hid);

            conn.commit();
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return -1;
        } finally {
            DruidUtil.close(conn);
        }
    }

    /**
     * 取消点赞（事务）：删除点赞记录 + like_count-1
     * @return 0成功，1未点赞，-1失败
     */
    public int cancelLike(Integer uid, Integer hid) {
        Connection conn = null;
        try {
            conn = DruidUtil.getConnection();
            conn.setAutoCommit(false);

            if (!likeDao.isUserLiked(conn, uid, hid)) {
                conn.rollback();
                return 1;
            }
            likeDao.deleteLike(conn, uid, hid);
            headlineDao.decLikeCount(conn, hid);

            conn.commit();
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return -1;
        } finally {
            DruidUtil.close(conn);
        }
    }

    public boolean isLiked(Integer uid, Integer hid) {
        try {
            return likeDao.isUserLiked(null, uid, hid);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
