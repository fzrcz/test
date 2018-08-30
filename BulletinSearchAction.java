package com.itgroup.busi.action.bulletin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itgroup.busi.entities.Bulletin;
import com.itgroup.busi.service.IBulletinService;
import com.itgroup.core.action.BaseAction;
import com.itgroup.core.entities.User;
import com.itgroup.core.exception.BusinessException;
import com.itgroup.core.web.pager.Pager;



/**
 * 
 * @author zy
 * 公告查询
 */
@Controller
public class BulletinSearchAction extends BaseAction{

	private static final Logger log = Logger.getLogger(BulletinSearchAction.class);
	
	@Resource(name="bulletinService")
	private IBulletinService bulletinService;
	

	
	@InitBinder("bulletin")
	public void initBinder2(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("bulletin.");
	}
	

	
	/**
	 * 列表查询
	 * @param model
	 * @param pager
	 * @param bulletin
	 * @return
	 */
	@RequestMapping("busi/bulletin/bulletinSearch!search.action")
	public String searchBulletin(ModelMap model, Pager pager, Bulletin bulletin){
        Map<String, Object> condition = new HashMap<>();
        
        //查询有效的公告
        bulletin.setStatus(Bulletin.Status.INVALID);
        condition.put("model", bulletin);
		
		Pager newPager = getPager(pager);
		List<Bulletin> bulletinList = bulletinService.getWithCondition(condition, newPager);
		
        model.put(Pager.FLAG_ATTRIBUT, newPager);
        model.put("bulletin", bulletin);
		model.put("bulletinList", bulletinList);
		return "/WEB-INF/busi/bulletin/bulletinSearch.jsp";
	}
	
	
	/**
	 * 明细查询
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping("busi/bulletin/bulletinSearch!detail.action")
	public String bulletinShowPrepare(ModelMap model,Integer id){
		
		if(id == null){
			throw new BusinessException("id不能为空");
		}
		
		//获取公告信息
		Bulletin bulletin = bulletinService.getById(id);
		if(bulletin == null || bulletin.getStatus() != Bulletin.Status.INVALID){
			throw new BusinessException("根据id:"+id+" 查询不到有效的公告信息");
		}		
		
		model.put("bulletin", bulletin);
		
		return "/WEB-INF/busi/bulletin/bulletinDetail.jsp";
	}
	


}
