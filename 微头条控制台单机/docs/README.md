# 01-控制台单机版 · 文档索引

终端 TUI 单机版：**React 18 + Ink 5**（Bun 运行时），数据本地 `db.json` 持久化，无服务端。

| 文档 | 内容 |
|---|---|
| [原子功能与代码映射](./原子功能与代码映射.md) | 结构 → 原子功能 → 业务规则 → 实现代码位置 |
| [流程图](./流程图.md) | 全部原子功能流程的 mermaid 图 |
| [原子功能详解](原子功能详解-首页信息流.md) | 首页信息流：Field/useKeys/MainView 深度走读（参考 p/admin 原子化写法） |

## 架构总览

```mermaid
graph TB
    subgraph 入口
        I[index.js<br/>render 挂载]
    end
    subgraph 界面层 src/App.jsx
        F[Field 自绘输入框] 
        K[useKeys 统一按键钩子]
        V1[LoginView 登录/注册]
        V2[MainView 首页/我的]
        V3[DetailView 详情]
        V4[PublishView 发布/编辑]
        V5[ProfileView 个人中心]
        V6[NoticesView 公告]
        V7[AdminView 管理面板]
    end
    subgraph 业务层 src/store.js
        B[register/login/changePwd<br/>headlines/detail/publish/update/remove<br/>toggleLike/addComment/removeComment<br/>types/notices/allComments/hotRank/userActivity]
    end
    D[(db.json 原子读写)]
    I --> V1 & V2 & V3 & V4 & V5 & V6 & V7
    V1 & V2 & V3 & V4 & V5 & V6 & V7 --> B
    F -.光标/输入.-> V1 & V2 & V3 & V4 & V5 & V6 & V7
    K -.按键归一化.-> V1 & V2 & V3 & V4 & V5 & V6 & V7
    B --> D
```

## 运行

```bash
npm install          # 或 bun install
bun run start        # bun src/index.js；账号 admin/123456
```
