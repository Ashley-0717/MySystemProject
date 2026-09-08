package com.micro.entity;

import java.util.Date;

public class NewsHeadline {
    private Integer hid;
    private String title;
    private String content;
    private Integer typeId;
    private Integer publisherUid;
    private Date createTime;
    private Date updateTime;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer isDeleted;
    private String publisherNick;
    private String typeName;

    public NewsHeadline() {}

    public Integer getHid() { return hid; }
    public void setHid(Integer hid) { this.hid = hid; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getTypeId() { return typeId; }
    public void setTypeId(Integer typeId) { this.typeId = typeId; }
    public Integer getPublisherUid() { return publisherUid; }
    public void setPublisherUid(Integer publisherUid) { this.publisherUid = publisherUid; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
    public String getPublisherNick() { return publisherNick; }
    public void setPublisherNick(String publisherNick) { this.publisherNick = publisherNick; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
}
