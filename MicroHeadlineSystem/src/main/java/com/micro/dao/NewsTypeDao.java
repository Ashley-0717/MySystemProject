package com.micro.dao;

import com.micro.entity.NewsType;

import java.sql.ResultSet;
import java.util.List;

public class NewsTypeDao extends BaseDao {

    public List<NewsType> listAll() {
        String sql = "select * from news_type order by type_id";
        return queryList(this::buildType, sql);
    }

    public NewsType findById(Integer typeId) {
        String sql = "select * from news_type where type_id = ?";
        return queryOne(this::buildType, sql, typeId);
    }

    public NewsType findByName(String typeName) {
        String sql = "select * from news_type where type_name = ?";
        return queryOne(this::buildType, sql, typeName);
    }

    public int addType(String typeName) {
        String sql = "insert into news_type(type_name) values (?)";
        return insertReturnKey(sql, typeName);
    }

    public int updateType(Integer typeId, String typeName) {
        String sql = "update news_type set type_name = ? where type_id = ?";
        return update(sql, typeName, typeId);
    }

    public int deleteType(Integer typeId) {
        String sql = "delete from news_type where type_id = ?";
        return update(sql, typeId);
    }

    public long countHeadlineByType(Integer typeId) {
        String sql = "select count(*) from news_headline where type_id = ?";
        return queryCount(sql, typeId);
    }

    private NewsType buildType(ResultSet rs) throws java.sql.SQLException {
        NewsType t = new NewsType();
        t.setTypeId(rs.getInt("type_id"));
        t.setTypeName(rs.getString("type_name"));
        return t;
    }
}
