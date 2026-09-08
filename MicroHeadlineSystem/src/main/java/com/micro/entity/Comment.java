package com.micro.entity;

import java.util.Date;

public class Comment {
    private Integer cid;
    private Integer hid;
    private Integer uid;
    private Integer parentCid;
    private String content;
    private Date createTime;
    private String userNick;

    public Comment() {}

    public Integer getCid() { return cid; }
    public void setCid(Integer cid) { this.cid = cid; }
    public Integer getHid() { return hid; }
    public void setHid(Integer hid) { this.hid = hid; }
    public Integer getUid() { return uid; }
    public void setUid(Integer uid) { this.uid = uid; }
    public Integer getParentCid() { return parentCid; }
    public void setParentCid(Integer parentCid) { this.parentCid = parentCid; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public String getUserNick() { return userNick; }
    public void setUserNick(String userNick) { this.userNick = userNick; }
}
