package com.micro.dao;

import com.micro.entity.Comment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommentDao extends BaseDao {

    private Comment build(ResultSet rs) throws SQLException {
        Comment c = new Comment();
        c.setCid(rs.getInt("cid"));
        c.setHid(rs.getInt("hid"));
        c.setUid(rs.getInt("uid"));
        Object pc = rs.getObject("parent_cid");
        c.setParentCid(pc == null ? null : (Integer) pc);
        c.setContent(rs.getString("content"));
        c.setCreateTime(rs.getTimestamp("create_time"));
        try { c.setUserNick(rs.getString("nickname")); } catch (Exception e) {}
        return c;
    }

    private static final String SELECT_JOIN = "select c.*, u.nickname from comment c left join `user` u on c.uid = u.uid ";

    /** 查询某头条的一级评论 */
    public List<Comment> findRootByHid(Integer hid, String order) {
        String ord = "asc".equalsIgnoreCase(order) ? "asc" : "desc";
        String sql = SELECT_JOIN + "where c.hid = ? and c.parent_cid is null order by c.create_time " + ord;
        return queryList(this::build, sql, hid);
    }

    /** 查询某评论的子评论 */
    public List<Comment> findChildren(Integer parentCid) {
        String sql = SELECT_JOIN + "where c.parent_cid = ? order by c.create_time asc";
        return queryList(this::build, sql, parentCid);
    }

    /** 根据id查询 */
    public Comment findById(Integer cid) {
        String sql = SELECT_JOIN + "where c.cid = ?";
        return queryOne(this::build, sql, cid);
    }

    /** 插入评论（事务内，回填主键） */
    public int insertComment(Connection conn, Comment c) throws SQLException {
        String sql = "insert into comment(hid, uid, parent_cid, content) values (?,?,?,?)";
        return insertReturnKeyWithConn(conn, sql, c.getHid(), c.getUid(), c.getParentCid(), c.getContent());
    }

    /** 删除评论（级联删除子评论由外键处理） */
    public int deleteComment(Integer cid) {
        String sql = "delete from comment where cid = ?";
        return update(sql, cid);
    }

    /** 统计某头条评论数（含子评论） */
    public long countByHid(Integer hid) {
        String sql = "select count(*) from comment where hid = ?";
        return queryCount(sql, hid);
    }

    /** 管理员全量查询评论 */
    public List<Comment> findAllForAdmin(Integer hid, Integer uid) {
        StringBuilder sql = new StringBuilder(SELECT_JOIN + "where 1=1 ");
        List<Object> params = new ArrayList<>();
        if (hid != null && hid > 0) { sql.append("and c.hid = ? "); params.add(hid); }
        if (uid != null && uid > 0) { sql.append("and c.uid = ? "); params.add(uid); }
        sql.append("order by c.create_time desc");
        return queryList(this::build, sql.toString(), params.toArray());
    }
}
