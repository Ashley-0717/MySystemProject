package com.micro.service;

import com.micro.dao.CommentDao;
import com.micro.dao.NewsHeadlineDao;
import com.micro.entity.Comment;
import com.micro.util.DruidUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentService {
    private final CommentDao commentDao = new CommentDao();
    private final NewsHeadlineDao headlineDao = new NewsHeadlineDao();

    /**
     * 发表评论（事务）：插入评论 + comment_count+1
     */
    public int addComment(Comment c) {
        Connection conn = null;
        try {
            conn = DruidUtil.getConnection();
            conn.setAutoCommit(false);

            int cid = commentDao.insertComment(conn, c);
            headlineDao.incCommentCount(conn, c.getHid());

            conn.commit();
            return cid;
        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return -1;
        } finally {
            DruidUtil.close(conn);
        }
    }

    /**
     * 删除评论（事务）：删除评论 + comment_count-1
     * 普通用户只能删自己的，管理员可删全部
     * @return 0成功，-1无权限，-2不存在
     */
    public int deleteComment(Integer cid, Integer uid, boolean isAdmin) {
        Comment c = commentDao.findById(cid);
        if (c == null) return -2;
        if (!isAdmin && !c.getUid().equals(uid)) return -1;

        Connection conn = null;
        try {
            conn = DruidUtil.getConnection();
            conn.setAutoCommit(false);

            // 统计该评论及其子评论数量，用于减少comment_count
            long deleteCount = 1 + countChildren(cid);
            commentDao.deleteComment(cid); // 外键级联删除子评论
            for (int i = 0; i < deleteCount; i++) {
                headlineDao.decCommentCount(conn, c.getHid());
            }

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

    private long countChildren(Integer parentCid) {
        List<Comment> children = commentDao.findChildren(parentCid);
        long count = children.size();
        for (Comment child : children) {
            count += countChildren(child.getCid());
        }
        return count;
    }

    /** 获取头条评论（含子评论） */
    public Map<String, Object> getCommentsByHid(Integer hid, String order) {
        List<Comment> roots = commentDao.findRootByHid(hid, order);
        Map<Integer, List<Comment>> childMap = new HashMap<>();
        for (Comment root : roots) {
            childMap.put(root.getCid(), commentDao.findChildren(root.getCid()));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("roots", roots);
        result.put("childMap", childMap);
        return result;
    }

    public Comment findById(Integer cid) {
        return commentDao.findById(cid);
    }

    public List<Comment> findAllForAdmin(Integer hid, Integer uid) {
        return commentDao.findAllForAdmin(hid, uid);
    }
}
