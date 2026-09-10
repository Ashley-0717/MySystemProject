package com.micro.service;

import com.micro.dao.AnnouncementDao;
import com.micro.entity.Announcement;

import java.util.List;

public class AnnouncementService {
    private final AnnouncementDao announcementDao = new AnnouncementDao();

    public List<Announcement> findAll(String keyword) {
        return announcementDao.findAll(keyword);
    }

    public Announcement findById(Integer aid) {
        return announcementDao.findById(aid);
    }

    public int addAnnouncement(Announcement a) {
        return announcementDao.addAnnouncement(a);
    }

    public int updateAnnouncement(Integer aid, String title, String content, String remark) {
        return announcementDao.updateAnnouncement(aid, title, content, remark);
    }

    public int deleteAnnouncement(Integer aid) {
        return announcementDao.deleteAnnouncement(aid);
    }
}
