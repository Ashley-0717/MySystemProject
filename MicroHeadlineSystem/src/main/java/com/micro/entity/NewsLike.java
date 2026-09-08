package com.micro.entity;

import java.util.Date;

public class NewsLike {
    private Integer lid;
    private Integer hid;
    private Integer uid;
    private Date likeTime;

    public NewsLike() {}

    public Integer getLid() { return lid; }
    public void setLid(Integer lid) { this.lid = lid; }
    public Integer getHid() { return hid; }
    public void setHid(Integer hid) { this.hid = hid; }
    public Integer getUid() { return uid; }
    public void setUid(Integer uid) { this.uid = uid; }
    public Date getLikeTime() { return likeTime; }
    public void setLikeTime(Date likeTime) { this.likeTime = likeTime; }
}
