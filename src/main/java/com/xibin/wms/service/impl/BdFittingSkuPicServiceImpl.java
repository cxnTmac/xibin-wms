package com.xibin.wms.service.impl;

import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.security.pojo.MyUserDetails;
import com.xibin.core.security.util.SecurityUtil;
import com.xibin.wms.dao.BdFittingSkuPicMapper;
import com.xibin.wms.pojo.BdFittingSkuPic;
import com.xibin.wms.service.BdFittingSkuPicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class BdFittingSkuPicServiceImpl extends BaseManagerImpl implements BdFittingSkuPicService {
	@Value("${file.webPicUploadUrl}")
	private String webPicUploadUrl;
	@Autowired
	HttpSession session;

	@Resource
	private BdFittingSkuPicMapper bdFittingSkuPicMapper;

	@Override
	public BdFittingSkuPic getFittingSkuPicById(int id) {
		// TODO Auto-generated method stub
		return bdFittingSkuPicMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<BdFittingSkuPic> getAllFittingSkuPicByPage(Map map) {
		// TODO Auto-generated method stub
		return bdFittingSkuPicMapper.selectAllByPage(map);
	}

	@Override
	public int removeFittingSkuPic(int idNormal, int idZip) {
		// TODO Auto-generated method stub
		BdFittingSkuPic normal = bdFittingSkuPicMapper.selectByPrimaryKey(idNormal);
		BdFittingSkuPic zip = bdFittingSkuPicMapper.selectByPrimaryKey(idZip);
		String normalPath = webPicUploadUrl + "\\"
				+ normal.getFittingSkuPicUrl().substring(4, normal.getFittingSkuPicUrl().length());
		String zipPath = webPicUploadUrl + "\\"
				+ zip.getFittingSkuPicUrl().substring(4, zip.getFittingSkuPicUrl().length());
		File normalfile = new File(normalPath);
		// 路径为文件且不为空则进行删除
		if (normalfile.isFile() && normalfile.exists()) {
			// 如果文件删除失败，则需要进行GC，防止JAVA占用文件无法进行删除
			if (!normalfile.delete()) {
				System.gc();
				normalfile.delete();
			}
		}
		File zipfile = new File(zipPath);
		// 路径为文件且不为空则进行删除
		if (zipfile.isFile() && zipfile.exists()) {
			// 如果文件删除失败，则需要进行GC，防止JAVA占用文件无法进行删除
			if (!zipfile.delete()) {
				System.gc();
				zipfile.delete();
			}
		}
		int[] ids = { idNormal, idZip };
		int result = this.delete(ids);
		return result;
	}

	@Override
	public BdFittingSkuPic saveFittingSkuPic(BdFittingSkuPic model) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		model.setCompanyId(myUserDetails.getCompanyId());
		return (BdFittingSkuPic) this.save(model);
	}

	@Override
	public List<BdFittingSkuPic> selectByFittingSkuCode(String fittingSkuCode) {
		// TODO Auto-generated method stub
		MyUserDetails myUserDetails = SecurityUtil.getMyUserDetails();
		return bdFittingSkuPicMapper.selectByFittingSkuCode(fittingSkuCode, myUserDetails.getCompanyId().toString());
	}

	@Override
	public List<BdFittingSkuPic> selectByFittingSkuCode(String fittingSkuCode, String companyId) {
		// TODO Auto-generated method stub
		return bdFittingSkuPicMapper.selectByFittingSkuCode(fittingSkuCode, companyId);
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return bdFittingSkuPicMapper;
	}

}
