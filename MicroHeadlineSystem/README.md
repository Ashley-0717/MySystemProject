# 微头条系统 (MicroHeadlineSystem)

基于 Java + Servlet + JDBC + MySQL + JSP 的 B/S 架构微头条内容分享系统。

## 技术栈

- **后端**：Java 8 + Servlet 4.0 + JDBC
- **数据库**：MySQL 8.0 + Druid 连接池
- **前端**：JSP + JSTL + CSS
- **构建**：Maven
- **服务器**：Tomcat 8.5/9

## 项目结构

```
MicroHeadlineSystem/
├── pom.xml
├── sql/init.sql                          # 数据库初始化脚本
└── src/main/
    ├── java/com/micro/
    │   ├── entity/                        # 实体类 (7个)
    │   │   ├── User.java
    │   │   ├── NewsType.java
    │   │   ├── NewsHeadline.java
    │   │   ├── NewsLike.java
    │   │   ├── Comment.java
    │   │   ├── Announcement.java
    │   │   └── PageBean.java
    │   ├── util/                          # 工具类 (3个)
    │   │   ├── DruidUtil.java
    │   │   ├── MD5Util.java
    │   │   └── WebUtil.java
    │   ├── dao/                           # 数据访问层 (7个)
    │   │   ├── BaseDao.java
    │   │   ├── UserDao.java
    │   │   ├── NewsTypeDao.java
    │   │   ├── NewsHeadlineDao.java
    │   │   ├── NewsLikeDao.java
    │   │   ├── CommentDao.java
    │   │   └── AnnouncementDao.java
    │   ├── service/                       # 业务逻辑层 (6个)
    │   │   ├── UserService.java
    │   │   ├── NewsTypeService.java
    │   │   ├── NewsHeadlineService.java
    │   │   ├── NewsLikeService.java
    │   │   ├── CommentService.java
    │   │   └── AnnouncementService.java
    │   └── servlet/                       # 控制器 (8个)
    │       ├── EncodingFilter.java
    │       ├── UserServlet.java
    │       ├── NewsHeadlineServlet.java
    │       ├── NewsLikeServlet.java
    │       ├── CommentServlet.java
    │       ├── NewsTypeServlet.java
    │       ├── AnnouncementServlet.java
    │       └── AdminServlet.java
    └── webapp/
        ├── WEB-INF/web.xml
        ├── static/css/style.css
        ├── index.jsp                      # 首页(头条列表)
        ├── login.jsp                      # 登录
        ├── register.jsp                   # 注册
        ├── user/
        │   ├── personal.jsp               # 个人中心
        │   ├── myNews.jsp                 # 我的头条
        │   ├── publishNews.jsp            # 发布头条
        │   ├── editNews.jsp               # 编辑头条
        │   └── newsDetail.jsp             # 头条详情(点赞/评论)
        └── admin/
            ├── adminIndex.jsp             # 管理面板首页
            ├── userManage.jsp             # 用户管理
            ├── newsManage.jsp             # 头条管理
            ├── typeManage.jsp             # 类型管理
            ├── announcementManage.jsp     # 公告管理
            ├── commentManage.jsp          # 评论管理
            └── hotRank.jsp                # 热门排行
```

## 快速开始

### 1. 环境要求
- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Tomcat 8.5/9

### 2. 初始化数据库
```bash
mysql -u root -p < sql/init.sql
```
这会创建 `micro_headline` 数据库及全部表，并预置：
- 管理员账号：`admin` / `123456`
- 基础头条类型：科技、体育、娱乐、财经、社会

### 3. 修改数据库连接
编辑 `src/main/java/com/micro/util/DruidUtil.java`，修改用户名和密码：
```java
dataSource.setUsername("root");
dataSource.setPassword("你的密码");
```

### 4. 构建部署
```bash
mvn clean package
# 将 target/MicroHeadlineSystem.war 部署到 Tomcat
```
或在 IDEA 中配置 Tomcat 直接运行。

### 5. 访问系统
- 首页：`http://localhost:8080/MicroHeadlineSystem/`
- 管理员登录：`admin` / `123456`
- 普通用户：自行注册

## 功能清单

### 普通用户
- [x] 注册（用户名、密码、确认密码、昵称）
- [x] 登录（显示昵称和角色）
- [x] 发布头条（标题、内容≤5000字、选择类型）
- [x] 查看所有头条（分页、关键字搜索、类型筛选、按时间倒序）
- [x] 查看我的头条（查询、修改、删除）
- [x] 头条详情（完整内容、浏览量+1）
- [x] 点赞/取消点赞（每用户每条只能点赞一次，事务维护冗余字段）
- [x] 发表评论（≤500字，事务维护comment_count）
- [x] 回复评论（parent_cid 树形结构）
- [x] 删除自己的评论（级联删除子评论）
- [x] 个人信息查看
- [x] 修改密码（校验旧密码）

### 管理员（唯一固定账号 role=1）
- [x] 用户管理（查看、删除普通用户）
- [x] 头条全量管理（查看所有、删除任何头条、含已删除筛选）
- [x] 类型管理（新增、修改、删除，删除前校验关联头条）
- [x] 公告管理（发布、修改、删除、按标题搜索）
- [x] 评论管理（查看所有、删除任何评论）
- [x] 热门排行（按点赞/评论/浏览量排序，前三名高亮）

## JDBC 技术要点

1. **PreparedStatement** 防止 SQL 注入（全部 DAO 使用）
2. **Druid 连接池** 管理数据库连接
3. **事务控制**：
   - 点赞/取消点赞：同时更新 like_count + 插入/删除 news_like
   - 发表评论：同时更新 comment_count + 插入 comment
   - 删除评论：同时更新 comment_count + 删除 comment（级联子评论）
4. **冗余字段维护**：like_count、comment_count 通过事务同步
5. **外键级联删除**：ON DELETE CASCADE（头条删除→点赞/评论级联删除）
6. **权限校验**：Service 层校验当前用户 ID == 资源所有者，管理员跳过
7. **分页查询**：LIMIT ?, ?
8. **自增主键回填**：PreparedStatement.RETURN_GENERATED_KEYS
9. **BaseDao 通用封装**：update/queryList/queryOne/queryCount/insertReturnKey
10. **密码加密**：MD5 存储

## 数据库表

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| user | 用户 | uid, username, password(MD5), nickname, role |
| news_type | 头条类型 | type_id, type_name |
| news_headline | 头条 | hid, title, content, type_id, publisher_uid, view_count, like_count, comment_count, is_deleted |
| news_like | 点赞 | lid, hid, uid, UNIQUE(hid,uid) |
| comment | 评论 | cid, hid, uid, parent_cid, content |
| announcement | 公告 | aid, title, content, remark |

## 测试说明

### Postman 接口测试
系统主要为表单提交（Servlet），可通过以下方式测试：
- 注册：POST `/user` action=register
- 登录：POST `/user` action=login
- 点赞：POST `/like` action=do&hid=1
- 发表评论：POST `/comment` action=add&hid=1&content=xxx

### JMeter 压力测试
- 线程组：模拟 50-100 并发用户
- HTTP 请求：GET `/headline?action=list`（头条列表页）
- 聚合报告：查看吞吐量、平均响应时间、错误率
