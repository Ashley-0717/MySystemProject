package com.micro.service;

import com.micro.dao.NewsHeadlineDao;
import com.micro.entity.NewsHeadline;
import com.micro.entity.PageBean;

import java.util.List;

public class NewsHeadlineService {
    private final NewsHeadlineDao headlineDao = new NewsHeadlineDao();

    /** 分页查询所有头条 */
    public PageBean<NewsHeadline> findPage(int pageNum, int pageSize, String keyword, Integer typeId) {
        PageBean<NewsHeadline> pb = new PageBean<>();
        pb.setPageNum(pageNum);
        pb.setPageSize(pageSize);
        long total = headlineDao.countAll(keyword, typeId);
        pb.setTotalCount(total);
        pb.setTotalPage((int) Math.ceil((double) total / pageSize));
        pb.setDataList(headlineDao.findPage(pageNum, pageSize, keyword, typeId));
        return pb;
    }

    /** 查询我的头条 */
    public List<NewsHeadline> findMyHeadlines(Integer uid, String keyword, Integer typeId) {
        return headlineDao.findByPublisher(uid, keyword, typeId);
    }

    /** 查看详情（浏览量+1） */
    public NewsHeadline viewDetail(Integer hid) {
        headlineDao.incViewCount(hid);
        return headlineDao.findById(hid);
    }

    public NewsHeadline findById(Integer hid) {
        return headlineDao.findById(hid);
    }

    /** 发布头条 */
    public int publish(NewsHeadline h) {
        return headlineDao.addHeadline(h);
    }

    /** 普通用户修改头条（类型不可改），校验权限 */
    public int updateByUser(Integer hid, Integer uid, String title, String content) {
        NewsHeadline h = headlineDao.findById(hid);
        if (h == null) return -1;
        if (!h.getPublisherUid().equals(uid)) return -2; // 无权限
        headlineDao.updateHeadline(hid, title, content);
        return 0;
    }

    /** 管理员修改头条（可改类型） */
    public int updateByAdmin(Integer hid, String title, String content, Integer typeId) {
        headlineDao.updateHeadlineByAdmin(hid, title, content, typeId);
        return 0;
    }

    /** 普通用户删除（逻辑删除+物理级联），校验权限 */
    public int deleteByUser(Integer hid, Integer uid) {
        NewsHeadline h = headlineDao.findById(hid);
        if (h == null) return -1;
        if (!h.getPublisherUid().equals(uid)) return -2;
        // 物理删除，外键级联删除点赞和评论
        headlineDao.physicalDelete(hid);
        return 0;
    }

    /** 管理员删除任何头条 */
    public int deleteByAdmin(Integer hid) {
        headlineDao.physicalDelete(hid);
        return 0;
    }

    /** 热门排行 */
    public List<NewsHeadline> hotRank(String sortBy, int pageNum, int pageSize) {
        return headlineDao.findHotRank(sortBy, pageNum, pageSize);
    }

    /** 管理员全量查询 */
    public List<NewsHeadline> findAllForAdmin(String keyword, Integer typeId, Integer publisherUid, Integer isDeleted) {
        return headlineDao.findAllForAdmin(keyword, typeId, publisherUid, isDeleted);
    }
}
