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
 * 
 * @author zy
 * 公告新增
 */
@Controller
public class BulletinInsertAction extends BaseAction{
	private static final Logger log = Logger.getLogger(BulletinInsertAction.class);

	@Resource(name="fileService")
	private IFileService  fileService;
	
	@Resource(name="bulletinService")
	private IBulletinService bulletinService;

	
	@InitBinder("bulletin")
	public void initBinder2(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("bulletin.");
	}
	

	
	
	/**
	 * 新增公告准备
	 * @Description:   TODO
	 * @param model
	 * @return  String
	 * @exception:
	 * @author:   
	 * @time:  2017年4月21日 下午5:39:00
	 */
	@RequestMapping("busi/bulletin/bulletinInsert!prepare.action")
	public String bulletinInsertPrepare(ModelMap model){
		
		return "/WEB-INF/busi/bulletin/bulletinInsert.jsp";
	}
	
	
	/**
	 * 新增公告
	 * @param model
	 * @param bulletin
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("busi/bulletin/bulletinInsert!insert.action")
	public JSONObject bulletinInsertSubmit(ModelMap model,Bulletin bulletin,HttpServletRequest request){
		JSONObject object = new JSONObject();
		object.put("code", 1);
		object.put("msg", "新增成功");
		
		User user = getLoginUser();
		bulletin.setPublishUser(user);
		try{
			//把图片信息保存到表中
			saveImageFile(bulletin,request);
			
			bulletinService.insertBulletinTransaction(bulletin);

		}catch(BusinessException e){
			e.printStackTrace();
			object.put("code", 0);
			object.put("reason", "新增公告失败，原因："+e.getMessage());
		}catch(ParamException e){
			e.printStackTrace();
			object.put("code", 0);
			object.put("reason", "新增公告失败，原因："+e.getMessage());
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
		if(uploaderList == null && bulletin.getContent()!=null && bulletin.getContent().contains("<img")){
			throw new ParamException("您新建的公告中包含图片，登陆超时,无法提交公告，请重新上传图片或者删除图片并提交");
		}
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
	

	
	

}
