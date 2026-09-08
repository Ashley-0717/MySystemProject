-- ============================================
-- 微头条系统 数据库初始化脚本
-- ============================================
CREATE DATABASE IF NOT EXISTS micro_headline DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE micro_headline;

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `uid` INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码(MD5)',
  `nickname` VARCHAR(50) NOT NULL COMMENT '昵称',
  `role` TINYINT NOT NULL DEFAULT 0 COMMENT '0普通用户 1管理员',
  `register_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 头条类型表
DROP TABLE IF EXISTS news_type;
CREATE TABLE news_type (
    type_id INT PRIMARY KEY AUTO_INCREMENT,
    type_name VARCHAR(50) NOT NULL UNIQUE COMMENT '类型名称'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 头条主表
DROP TABLE IF EXISTS news_headline;
CREATE TABLE news_headline (
    hid INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '头条标题',
    content TEXT NOT NULL COMMENT '头条内容(≤5000字)',
    type_id INT NOT NULL COMMENT '头条类型外键',
    publisher_uid INT NOT NULL COMMENT '发布者uid',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    like_count INT DEFAULT 0 COMMENT '点赞数(冗余字段)',
    comment_count INT DEFAULT 0 COMMENT '评论数(冗余字段)',
    is_deleted TINYINT DEFAULT 0 COMMENT '0未删除 1逻辑删除',
    FOREIGN KEY (type_id) REFERENCES news_type(type_id),
    FOREIGN KEY (publisher_uid) REFERENCES `user`(uid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 点赞表
DROP TABLE IF EXISTS news_like;
CREATE TABLE news_like (
    lid INT PRIMARY KEY AUTO_INCREMENT,
    hid INT NOT NULL COMMENT '头条id',
    uid INT NOT NULL COMMENT '点赞用户id',
    like_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_hid_uid(hid, uid),
    FOREIGN KEY (hid) REFERENCES news_headline(hid) ON DELETE CASCADE,
    FOREIGN KEY (uid) REFERENCES `user`(uid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 评论表(支持回复)
DROP TABLE IF EXISTS comment;
CREATE TABLE comment (
    cid INT PRIMARY KEY AUTO_INCREMENT,
    hid INT NOT NULL COMMENT '所属头条',
    uid INT NOT NULL COMMENT '评论用户',
    parent_cid INT NULL COMMENT '父评论id,null为一级评论',
    content VARCHAR(500) NOT NULL COMMENT '评论内容(≤500字)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (hid) REFERENCES news_headline(hid) ON DELETE CASCADE,
    FOREIGN KEY (uid) REFERENCES `user`(uid),
    FOREIGN KEY (parent_cid) REFERENCES comment(cid) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 公告表
DROP TABLE IF EXISTS announcement;
CREATE TABLE announcement (
    aid INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    remark VARCHAR(300),
    publish_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 预置唯一管理员 admin/123456 (MD5: e10adc3949ba59abbe56e057f20f883e)
INSERT INTO `user`(username, password, nickname, role)
VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', 1);

-- 预置基础头条类型
INSERT INTO news_type(type_name) VALUES ('科技'), ('体育'), ('娱乐'), ('财经'), ('社会');
