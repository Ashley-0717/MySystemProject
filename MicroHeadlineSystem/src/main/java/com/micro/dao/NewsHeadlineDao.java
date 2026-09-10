package com.micro.dao;

import com.micro.entity.NewsHeadline;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NewsHeadlineDao extends BaseDao {

    private NewsHeadline build(ResultSet rs) throws SQLException {
        NewsHeadline h = new NewsHeadline();
        h.setHid(rs.getInt("hid"));
        h.setTitle(rs.getString("title"));
        h.setContent(rs.getString("content"));
        h.setTypeId(rs.getInt("type_id"));
        h.setPublisherUid(rs.getInt("publisher_uid"));
        h.setCreateTime(rs.getTimestamp("create_time"));
        h.setUpdateTime(rs.getTimestamp("update_time"));
        h.setViewCount(rs.getInt("view_count"));
        h.setLikeCount(rs.getInt("like_count"));
        h.setCommentCount(rs.getInt("comment_count"));
        h.setIsDeleted(rs.getInt("is_deleted"));
        try { h.setPublisherNick(rs.getString("nickname")); } catch (Exception e) {}
        try { h.setTypeName(rs.getString("type_name")); } catch (Exception e) {}
        return h;
    }

    private static final String SELECT_JOIN = "select h.*, u.nickname, t.type_name from news_headline h " +
            "left join `user` u on h.publisher_uid = u.uid " +
            "left join news_type t on h.type_id = t.type_id ";

    /** 分页查询所有未删除头条，按发布时间倒序 */
    public List<NewsHeadline> findPage(int pageNum, int pageSize, String keyword, Integer typeId) {
        StringBuilder sql = new StringBuilder(SELECT_JOIN + "where h.is_deleted = 0 ");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("and (h.title like ? or h.content like ?) ");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        if (typeId != null && typeId > 0) {
            sql.append("and h.type_id = ? ");
            params.add(typeId);
        }
        sql.append("order by h.create_time desc limit ?, ?");
        params.add((pageNum - 1) * pageSize);
        params.add(pageSize);
        return queryList(this::build, sql.toString(), params.toArray());
    }

    public long countAll(String keyword, Integer typeId) {
        StringBuilder sql = new StringBuilder("select count(*) from news_headline h where h.is_deleted = 0 ");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("and (h.title like ? or h.content like ?) ");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        if (typeId != null && typeId > 0) {
            sql.append("and h.type_id = ? ");
            params.add(typeId);
        }
        return queryCount(sql.toString(), params.toArray());
    }

    /** 查询某用户的头条 */
    public List<NewsHeadline> findByPublisher(Integer uid, String keyword, Integer typeId) {
        StringBuilder sql = new StringBuilder(SELECT_JOIN + "where h.publisher_uid = ? and h.is_deleted = 0 ");
        List<Object> params = new ArrayList<>();
        params.add(uid);
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("and (h.title like ? or h.content like ?) ");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        if (typeId != null && typeId > 0) {
            sql.append("and h.type_id = ? ");
            params.add(typeId);
        }
        sql.append("order by h.create_time desc");
        return queryList(this::build, sql.toString(), params.toArray());
    }

    /** 根据id查询详情 */
    public NewsHeadline findById(Integer hid) {
        String sql = SELECT_JOIN + "where h.hid = ?";
        return queryOne(this::build, sql, hid);
    }

    /** 发布头条，回填主键 */
    public int addHeadline(NewsHeadline h) {
        String sql = "insert into news_headline(title, content, type_id, publisher_uid) values (?,?,?,?)";
        return insertReturnKey(sql, h.getTitle(), h.getContent(), h.getTypeId(), h.getPublisherUid());
    }

    /** 修改头条标题和内容（普通用户，类型不可改） */
    public int updateHeadline(Integer hid, String title, String content) {
        String sql = "update news_headline set title = ?, content = ?, update_time = now() where hid = ?";
        return update(sql, title, content, hid);
    }

    /** 管理员修改头条（可改类型） */
    public int updateHeadlineByAdmin(Integer hid, String title, String content, Integer typeId) {
        String sql = "update news_headline set title = ?, content = ?, type_id = ?, update_time = now() where hid = ?";
        return update(sql, title, content, typeId, hid);
    }

    /** 逻辑删除头条 */
    public int deleteHeadline(Integer hid) {
        String sql = "update news_headline set is_deleted = 1 where hid = ?";
        return update(sql, hid);
    }

    /** 物理删除（级联删除点赞评论） */
    public int physicalDelete(Integer hid) {
        String sql = "delete from news_headline where hid = ?";
        return update(sql, hid);
    }

    /** 浏览量+1 */
    public int incViewCount(Integer hid) {
        String sql = "update news_headline set view_count = view_count + 1 where hid = ?";
        return update(sql, hid);
    }

    /** 点赞数+1（事务内） */
    public int incLikeCount(Connection conn, Integer hid) throws SQLException {
        String sql = "update news_headline set like_count = like_count + 1 where hid = ?";
        return updateWithConn(conn, sql, hid);
    }

    /** 点赞数-1（事务内） */
    public int decLikeCount(Connection conn, Integer hid) throws SQLException {
        String sql = "update news_headline set like_count = like_count - 1 where hid = ? and like_count > 0";
        return updateWithConn(conn, sql, hid);
    }

    /** 评论数+1（事务内） */
    public int incCommentCount(Connection conn, Integer hid) throws SQLException {
        String sql = "update news_headline set comment_count = comment_count + 1 where hid = ?";
        return updateWithConn(conn, sql, hid);
    }

    /** 评论数-1（事务内） */
    public int decCommentCount(Connection conn, Integer hid) throws SQLException {
        String sql = "update news_headline set comment_count = comment_count - 1 where hid = ? and comment_count > 0";
        return updateWithConn(conn, sql, hid);
    }

    /** 热门排行 */
    public List<NewsHeadline> findHotRank(String sortBy, int pageNum, int pageSize) {
        String order = "like_count";
        if ("comment".equals(sortBy)) order = "comment_count";
        else if ("view".equals(sortBy)) order = "view_count";
        String sql = SELECT_JOIN + "where h.is_deleted = 0 order by h." + order + " desc limit ?, ?";
        return queryList(this::build, sql, (pageNum - 1) * pageSize, pageSize);
    }

    /** 管理员全量查询（含已删除） */
    public List<NewsHeadline> findAllForAdmin(String keyword, Integer typeId, Integer publisherUid, Integer isDeleted) {
        StringBuilder sql = new StringBuilder(SELECT_JOIN + "where 1=1 ");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("and (h.title like ? or h.content like ?) ");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        if (typeId != null && typeId > 0) { sql.append("and h.type_id = ? "); params.add(typeId); }
        if (publisherUid != null && publisherUid > 0) { sql.append("and h.publisher_uid = ? "); params.add(publisherUid); }
        if (isDeleted != null) { sql.append("and h.is_deleted = ? "); params.add(isDeleted); }
        sql.append("order by h.create_time desc");
        return queryList(this::build, sql.toString(), params.toArray());
    }
}
