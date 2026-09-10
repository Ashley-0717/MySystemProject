package com.micro.dao;

import com.micro.entity.Announcement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDao extends BaseDao {

    private Announcement build(ResultSet rs) throws SQLException {
        Announcement a = new Announcement();
        a.setAid(rs.getInt("aid"));
        a.setTitle(rs.getString("title"));
        a.setContent(rs.getString("content"));
        a.setRemark(rs.getString("remark"));
        a.setPublishTime(rs.getTimestamp("publish_time"));
        return a;
    }

    public List<Announcement> findAll(String keyword) {
        StringBuilder sql = new StringBuilder("select * from announcement where 1=1 ");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("and title like ? ");
            params.add("%" + keyword + "%");
        }
        sql.append("order by publish_time desc");
        return queryList(this::build, sql.toString(), params.toArray());
    }

    public Announcement findById(Integer aid) {
        String sql = "select * from announcement where aid = ?";
        return queryOne(this::build, sql, aid);
    }

    public int addAnnouncement(Announcement a) {
        String sql = "insert into announcement(title, content, remark) values (?,?,?)";
        return insertReturnKey(sql, a.getTitle(), a.getContent(), a.getRemark());
    }

    public int updateAnnouncement(Integer aid, String title, String content, String remark) {
        String sql = "update announcement set title = ?, content = ?, remark = ? where aid = ?";
        return update(sql, title, content, remark, aid);
    }

    public int deleteAnnouncement(Integer aid) {
        String sql = "delete from announcement where aid = ?";
        return update(sql, aid);
    }
}
