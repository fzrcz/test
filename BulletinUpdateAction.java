package com.itgroup.busi.action.bulletin;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.itgroup.busi.entities.Bulletin;
import com.itgroup.busi.service.IBulletinService;
import com.itgroup.busi.util.ueditor.Uploader;
import com.itgroup.core.action.BaseAction;
import com.itgroup.core.action.file.FileAction;
import com.itgroup.core.entities.File;
import com.itgroup.core.entities.User;
import com.itgroup.core.exception.BusinessException;
import com.itgroup.core.exception.ParamException;
import com.itgroup.core.service.IFileService;


/**
 * @author zy
 * 公告更新
 */
@Controller
public class BulletinUpdateAction extends BaseAction{
	private static final Logger log = Logger.getLogger(BulletinUpdateAction.class);
	
	@Resource(name="fileService")
	private IFileService  fileService;
	
	@Resource(name="bulletinService")
	private IBulletinService bulletinService;
	
	@InitBinder("bulletin")
	public void initBinder2(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("bulletin.");
	}
	
	
	

	
	/**
	 * 更新公告页面
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping("busi/bulletin/bulletinUpdate!prepare.action")
	public String bulletinUpdatePrepare(ModelMap model,Integer id){

		if(id == null){
			throw new BusinessException("id不能为空");
		}
		
		//获取公告信息
		Bulletin bulletin = bulletinService.getById(id);
		model.put("bulletin", bulletin);
		
		return "/WEB-INF/busi/bulletin/bulletinUpdate.jsp";
	}
	
	
	
	/**
	 * 更新公告
	 * @param model
	 * @param bulletin
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("busi/bulletin/bulletinUpdate!update.action")
	public JSONObject bulletinUpdateSubmit(ModelMap model,Bulletin bulletin,HttpServletRequest request){
		JSONObject object = new JSONObject();
		object.put("code", 1);
		object.put("msg", "修改成功");
		User user = getLoginUser();
		bulletin.setLastModifyUser(user);
		bulletin.setLastModifyTime(new Date());
		try{
			
			//把图片信息保存到表中
			saveImageFile(bulletin,request);
			
			bulletinService.updateBulletinTransaction(bulletin);
			
		
		}catch(ParamException e){
			log.error("错误异常描述");
			log.error(e);
			object.put("code", 0);
			object.put("reason", "更新公告失败，原因："+e.getMessage());
		}catch(BusinessException e){
			log.error("错误异常描述");
			log.error(e);
			object.put("code", 0);
			object.put("reason", "更新公告失败，原因："+e.getMessage());
		}
		return object;
	}
	
	
	/**
	 * 把图片信息保存到session中
	 * @Description:
	 * @exception:
	 * @author:   zy
	 * @time:  2017年5月10日 上午12:49:16
	 */
	public void saveImageFile(Bulletin bulletin,HttpServletRequest request){
		List<Uploader> uploaderList = (List<Uploader>)request.getSession().getAttribute("uploaderObjectList");
/*		if(uploaderList == null && bulletin.getContent()!=null && bulletin.getContent().contains("<img")){
			throw new ParamException("您新建的公告中包含图片，登陆超时,无法提交公告，请重新上传图片或者删除图片并提交");
		}*/
		if(uploaderList != null){
			for(Uploader uploader:uploaderList){
				if(uploader!=null && bulletin.getContent()!=null && bulletin.getContent().contains("<img")){
					File file = new File();
					//查询数据库判断是否已经存在此记录
					String location = uploader.getFileName();
					file.setLocation(location);
			        Map<String, Object> condition = new HashMap<>();
			        condition.put("model", file);
			        //获取总记录数
					int totalCount = fileService.getWithConditionCount(condition);
					
					if(totalCount>0){
						continue;
					}
				
					
					User loginUser = getLoginUser();
					file.setCreateUser(loginUser!=null?loginUser.getId():null);
					file.setCreateDate(new Date());
		
					file.setLocation(uploader.getFileName());
					//图片url
					file.setUrl(uploader.getUrl());
					//略缩图url
					file.setThumburl(FileAction.domain+"/"+uploader.getFileName()+FileAction.thumbPicture);
					file.setBucket(uploader.getBucket());
		
					//源文件名
					String fileOldName = uploader.getOriginalName();
					int docIndex = fileOldName.lastIndexOf(".");
					//文件名
					String fileName = fileOldName.substring(0, docIndex);
					//文件后缀
					String subfix = fileOldName.substring(docIndex+1, fileOldName.length());
					file.setFileName(fileName);
					long size = 1;
					file.setSize(size);
					file.setSubfix(subfix);
					fileService.add(file);
				}
			}
		}
	}
	
	

	
	/**
	 * 修改 上架状态
	 * @Description: 
	 * @param model
	 * @param ids
	 * @return  JSONObject
	 * @exception:
	 * @author:   
	 * @time:  2017年4月22日 下午3:15:58
	 */
	@ResponseBody
	@RequestMapping("busi/bulletin/bulletinShelvesStatus!update.action")
	public JSONObject updateShelvesStatus(ModelMap model, String ids){
		JSONObject object = new JSONObject();
		object.put("code", 1);
		object.put("msg", "提交成功");
		User user = getLoginUser();
		Bulletin bulletin = new Bulletin();
		bulletin.setLastModifyUser(user);
		bulletin.setLastModifyTime(new Date());
		try{
			bulletinService.updateShelvesStatus(bulletin,ids);
		}catch(BusinessException e){
			log.error("错误异常描述");
			log.error(e);
			object.put("code", 0);
			object.put("reason", "修改上架状态失败,原因："+e.getMessage());
		}catch(ParamException e){
			log.error("错误异常描述");
			log.error(e);
			object.put("code", 0);
			object.put("reason", "修改上架状态失败,原因："+e.getMessage());
		}
		return object;
	}
	
	/**
	 * 修改 上架状态为  下架
	 * @Description: 
	 * @param model
	 * @param ids
	 * @return  JSONObject
	 * @exception:
	 * @author:   
	 * @time:  2017年4月22日 下午3:15:58
	 */
	@ResponseBody
	@RequestMapping("busi/bulletin/bulletinShelvesStatusOff!update.action")
	public JSONObject updateShelvesStatusOff(ModelMap model, String ids){
		JSONObject object = new JSONObject();
		object.put("code", 1);
		object.put("msg", "提交成功");
		User user = getLoginUser();
		Bulletin bulletin = new Bulletin();
		bulletin.setLastModifyUser(user);
		bulletin.setLastModifyTime(new Date());
		try{
			bulletinService.updateShelvesStatusOff(bulletin,ids);
		}catch(ParamException e){
			log.error("错误异常描述");
			log.error(e);
			object.put("code", 0);
			object.put("reason", "修改上架状态失败,原因："+e.getMessage());
		}catch(BusinessException e){
			log.error("错误异常描述");
			log.error(e);
			object.put("code", 0);
			object.put("reason", "修改上架状态失败,原因："+e.getMessage());
		}
		return object;
	}
	
	

	 



}
