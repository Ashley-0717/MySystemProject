package com.micro.entity;

import java.util.Date;

public class Announcement {
    private Integer aid;
    private String title;
    private String content;
    private String remark;
    private Date publishTime;

    public Announcement() {}

    public Integer getAid() { return aid; }
    public void setAid(Integer aid) { this.aid = aid; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Date getPublishTime() { return publishTime; }
    public void setPublishTime(Date publishTime) { this.publishTime = publishTime; }
}
