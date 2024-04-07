package com.xibin.finance.service.impl;

import com.xibin.core.exception.BusinessException;
import com.xibin.finance.dao.FItemDao;
import com.xibin.finance.pojo.FItem;
import com.xibin.finance.service.FItemService;
import com.xibin.wms.pojo.BdArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class FItemServiceImpl implements FItemService
{
    @Autowired
    private HttpSession session;
    @Autowired
    private FItemDao fItemDao;
    @Override
    public FItem getItemById(int id) {
        return null;
    }

    @Override
    public List<FItem> getAllItemByPage(Map map) {
        return null;
    }
    @Override
    public List<FItem> getAllItem(Map map) {
        return fItemDao.findFItemList(map);
    }
    @Override
    public int removeItem(int id, String areaCode) throws BusinessException {
        return 0;
    }

    @Override
    public FItem saveItem(BdArea model) throws BusinessException {
        return null;
    }

    @Override
    public List<FItem> selectByKey(String itemID) {
        return null;
    }
}
