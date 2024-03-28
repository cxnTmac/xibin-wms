package com.xibin.wms.service.impl;

import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.core.utils.ReadExcelTools;
import com.xibin.wms.dao.BdFittingSkuMapper;
import com.xibin.wms.pojo.BdFittingSku;
import com.xibin.wms.query.BdFittingSkuQueryItem;
import com.xibin.wms.service.BdFittingSkuService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class BdFittingSkuServiceImpl extends BaseManagerImpl implements BdFittingSkuService {
	@Autowired
	HttpSession session;

	@Resource
	private BdFittingSkuMapper bdFittingSkuMapper;

	@Override
	public BdFittingSku getFittingSkuById(int id) {
		// TODO Auto-generated method stub
		return bdFittingSkuMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<BdFittingSkuQueryItem> getAllFittingSkuByPage(Map map) {
		// TODO Auto-generated method stub
		return bdFittingSkuMapper.selectAllByPage(map);
	}

	@Override
	public List<BdFittingSkuQueryItem> MgetAllFittingSkuByPageWithOnePic(Map map) {
		// TODO Auto-generated method stub
		return bdFittingSkuMapper.selectALLByPageWithOnePic(map);
	}

	@Override
	public List<BdFittingSku> selectByExample(BdFittingSku model) {
		// TODO Auto-generated method stub
		return bdFittingSkuMapper.selectByExample(model);
	}

	@Override
	public int removeFittingSku(int id, String fittingSkuCode) throws BusinessException {
		// TODO Auto-generated method stub
		if (!deleteBeforeCheck(fittingSkuCode)) {
			throw new BusinessException("该产品编码[" + fittingSkuCode + "]已被引用，不能删除");
		} else {
			int[] ids = { id };
			return this.delete(ids);
		}
	}

	@Override
	public BdFittingSku saveFittingSku(BdFittingSku model) throws BusinessException {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		model.setCompanyId(myUserDetails.getCompanyId());
		List<BdFittingSkuQueryItem> list = bdFittingSkuMapper.selectByKey(model.getFittingSkuCode(),
				model.getCompanyId().toString());
		if (list.size() > 0 && model.getId() == 0) {
			throw new BusinessException("编码：[" + model.getFittingSkuCode() + "] 已存在，不能重复！");
		}
		return (BdFittingSku) this.save(model);
	}
	@Override
	public BdFittingSku updateSkuPackageCode(String skuCode,String newPackageCode) throws BusinessException {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		List<BdFittingSkuQueryItem> list = bdFittingSkuMapper.selectByKey(skuCode,
				myUserDetails.getCompanyId().toString());
		if (list.size() == 0) {
			throw new BusinessException("编码：[" + skuCode + "] 不存在，无法更新包装规格！");
		}
		BdFittingSkuQueryItem queryItem = list.get(0);
		BdFittingSku model = new BdFittingSku();
		BeanUtils.copyProperties(queryItem,model);
		if(newPackageCode!=null&&!newPackageCode.equals(model.getPackageCode())){
			model.setPackageCode(newPackageCode);
			return (BdFittingSku) this.save(model);
		}else{
			return model;
		}
	}
	@Override
	public List<BdFittingSkuQueryItem> selectByKey(String skuCode) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		return bdFittingSkuMapper.selectByKey(skuCode, myUserDetails.getCompanyId().toString());
	}

	@Override
	public List<BdFittingSkuQueryItem> selectByKey(String skuCode, String companyId) {
		return bdFittingSkuMapper.selectByKey(skuCode, companyId);
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return bdFittingSkuMapper;
	}

	private boolean deleteBeforeCheck(String fittingTypeCode) {
		return true;
	}

	@Override
	public Message importByExcel(MultipartFile file, String skuCodeColumnName) throws BusinessException {
		Message message = new Message();
		try {
			MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
			Message readMessge = ReadExcelTools.readExcelForGetFittingSkuCode(file, skuCodeColumnName);
			List<String> quickCodes = (List<String>) readMessge.getData();
			List<String> skuCodes = new ArrayList<String>();
			for (String quickCode : quickCodes) {
				if (!"NULL".equals(quickCode)) {
					BdFittingSku queryExample = new BdFittingSku();
					queryExample.setCompanyId(myUserDetails.getCompanyId());
					queryExample.setQuickCode(quickCode);
					List<BdFittingSku> results = this.selectByExample(queryExample);
					if (results.size() > 0) {
						skuCodes.add(results.get(0).getFittingSkuCode());
					} else {
						skuCodes.add("");
					}
				} else {
					skuCodes.add("");
				}
			}
			message.setCode(200);
			message.setData(skuCodes);
			message.setMsg(readMessge.getMsg());
			return message;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		} catch (BusinessException e) {
			e.printStackTrace();
			message.setCode(0);
			message.setMsg(e.getMessage());
			return message;
		}
	}
}
