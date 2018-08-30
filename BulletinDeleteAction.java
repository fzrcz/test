package com.itgroup.busi.action.bulletin;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.itgroup.busi.entities.Bulletin;
import com.itgroup.busi.service.IBulletinService;
import com.itgroup.core.action.BaseAction;
import com.itgroup.core.entities.User;
import com.itgroup.core.exception.BusinessException;
import com.itgroup.core.exception.ParamException;


/**
 * @author zy
 * 公告删除
 */
@Controller
public class BulletinDeleteAction extends BaseAction{
	
	private static final Logger log = Logger.getLogger(BulletinDeleteAction.class);


	@Resource(name="bulletinService")
	private IBulletinService bulletinService;
	


	/**
	 * 删除 by ids
	 * @Description:
	 * @param model
	 * @param ids
	 * @return  JSONObject
	 * @exception:
	 * @author:   
	 * @time:  2017年4月22日 下午3:15:58
	 */
	@ResponseBody
	@RequestMapping("busi/bulletin/bulletinDelete!delete.action")
	public JSONObject deleteBulletinByIds(ModelMap model, String ids){
		JSONObject object = new JSONObject();
		object.put("code", 1);
		object.put("msg", "删除成功");
		User user = getLoginUser();
		Bulletin bulletin = new Bulletin();
		bulletin.setLastModifyUser(user);
		bulletin.setLastModifyTime(new Date());
		
		try{
			bulletinService.deleteBulletinByIds(bulletin,ids);
		}catch(ParamException e){
			log.error("错误异常描述");
			log.error(e);
			object.put("code", 0);
			object.put("reason", "删除失败,原因："+e.getMessage());
		}catch(BusinessException e){
			log.error("错误异常描述");
			log.error(e);
			object.put("code", 0);
			object.put("reason", "删除失败,原因："+e.getMessage());
		}
		return object;
	}
	
	


}
