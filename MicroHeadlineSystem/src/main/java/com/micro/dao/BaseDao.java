package com.micro.dao;

import com.micro.util.DruidUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDao {

    /** 通用更新：insert update delete，自动获取连接 */
    public int update(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DruidUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            setParams(pstmt, params);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            DruidUtil.close(pstmt, conn);
        }
    }

    /** 事务内更新：使用外部传入的Connection */
    public int updateWithConn(Connection conn, String sql, Object... params) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            setParams(pstmt, params);
            return pstmt.executeUpdate();
        } finally {
            DruidUtil.close(pstmt);
        }
    }

    /** 插入并回填自增主键 */
    public int insertReturnKey(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DruidUtil.getConnection();
            pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            setParams(pstmt, params);
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            DruidUtil.close(rs, pstmt, conn);
        }
    }

    /** 事务内插入并回填主键 */
    public int insertReturnKeyWithConn(Connection conn, String sql, Object... params) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            setParams(pstmt, params);
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            return -1;
        } finally {
            DruidUtil.close(rs, pstmt);
        }
    }

    /** 通用查询返回List */
    public <T> List<T> queryList(RowMapper<T> mapper, String sql, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<T> list = new ArrayList<>();
        try {
            conn = DruidUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            setParams(pstmt, params);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapper.mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            DruidUtil.close(rs, pstmt, conn);
        }
    }

    /** 事务内查询 */
    public <T> List<T> queryListWithConn(Connection conn, RowMapper<T> mapper, String sql, Object... params) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<T> list = new ArrayList<>();
        try {
            pstmt = conn.prepareStatement(sql);
            setParams(pstmt, params);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapper.mapRow(rs));
            }
            return list;
        } finally {
            DruidUtil.close(rs, pstmt);
        }
    }

    /** 查询单个对象 */
    public <T> T queryOne(RowMapper<T> mapper, String sql, Object... params) {
        List<T> list = queryList(mapper, sql, params);
        return list.size() > 0 ? list.get(0) : null;
    }

    /** 查询count */
    public long queryCount(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DruidUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            setParams(pstmt, params);
            rs = pstmt.executeQuery();
            if (rs.next()) return rs.getLong(1);
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            DruidUtil.close(rs, pstmt, conn);
        }
    }

    private void setParams(PreparedStatement pstmt, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
    }

    @FunctionalInterface
    public interface RowMapper<T> {
        T mapRow(ResultSet rs) throws SQLException;
    }
}
