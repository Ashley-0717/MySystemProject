package com.micro.entity;

import java.util.List;

public class PageBean<T> {
    private int pageNum;
    private int pageSize;
    private long totalCount;
    private int totalPage;
    private List<T> dataList;

    public PageBean() {}

    public int getPageNum() { return pageNum; }
    public void setPageNum(int pageNum) { this.pageNum = pageNum; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
    public int getTotalPage() { return totalPage; }
    public void setTotalPage(int totalPage) { this.totalPage = totalPage; }
    public List<T> getDataList() { return dataList; }
    public void setDataList(List<T> dataList) { this.dataList = dataList; }
}
