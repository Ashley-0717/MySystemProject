package com.micro.service;

import com.micro.dao.NewsTypeDao;
import com.micro.entity.NewsType;

import java.util.List;

public class NewsTypeService {
    private final NewsTypeDao typeDao = new NewsTypeDao();

    public List<NewsType> listAll() {
        return typeDao.listAll();
    }

    public NewsType findById(Integer typeId) {
        return typeDao.findById(typeId);
    }

    /** 新增：0成功，1名称已存在 */
    public int addType(String typeName) {
        if (typeDao.findByName(typeName) != null) return 1;
        typeDao.addType(typeName);
        return 0;
    }

    /** 修改：0成功，1名称已存在 */
    public int updateType(Integer typeId, String typeName) {
        NewsType exist = typeDao.findByName(typeName);
        if (exist != null && !exist.getTypeId().equals(typeId)) return 1;
        typeDao.updateType(typeId, typeName);
        return 0;
    }

    /** 删除：0成功，1有关联头条不可删除 */
    public int deleteType(Integer typeId) {
        long count = typeDao.countHeadlineByType(typeId);
        if (count > 0) return 1;
        typeDao.deleteType(typeId);
        return 0;
    }
}
