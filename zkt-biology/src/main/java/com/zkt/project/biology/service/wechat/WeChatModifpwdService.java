package com.zkt.project.biology.service.wechat;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zkt.common.core.util.RequestConstance;
import com.zkt.common.core.util.RequestTag;
import com.zkt.common.core.util.ValidateUtil;
import com.zkt.project.biology.entity.Login;
import com.zkt.project.biology.entity.Wechatusers;
import com.zkt.project.biology.mapper.CageMapper;
import com.zkt.project.biology.mapper.LoginMapper;
import com.zkt.project.biology.mapper.ModelMapper;
import com.zkt.project.biology.mapper.OperatrecordMapper;
import com.zkt.project.biology.mapper.OrderMapper;
import com.zkt.project.biology.mapper.PicMapper;
import com.zkt.project.biology.mapper.ProblemMapper;
import com.zkt.project.biology.mapper.RunningMapper;
import com.zkt.project.biology.mapper.SampleMapper;
import com.zkt.project.biology.mapper.WechatusersMapper;
import com.zkt.project.biology.service.CommonService;

import net.sf.json.JSONObject;

@Service
public class WeChatModifpwdService {

	@Autowired
	private LoginMapper loginMapper;

	@Autowired
	private SampleMapper sampleMapper;

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private CageMapper cageMapper;

	@Autowired
	private OperatrecordMapper operatrecordMapper;

	@Autowired
	private CommonService commonServiceImpl;

	@Autowired
	private PicMapper picMapper;

	@Autowired
	private ProblemMapper problemMapper;

	@Autowired
	private WechatusersMapper wechatusersMapper;

	@Autowired
	private RunningMapper runningMapper;

	@Autowired
	private ModelMapper modelMapper;

	@Resource(name = "proper")
	private Map<?, ?> proper;

	
	public String modifpwd(HttpServletRequest request) throws Exception {
		
		JSONObject result = new JSONObject();

		String userName = request.getParameter("userName");// 用户名
		String oldpwd = request.getParameter("oldpwd");// 老密码 提交的密码都是经过MD5加密后	无需处理
		String newpwd = request.getParameter("newpwd");// 新密码 提交的密码都是经过MD5加密后	无需处理
		if (ValidateUtil.isEmpty(userName) || ValidateUtil.isEmpty(oldpwd) || ValidateUtil.isEmpty(newpwd)) {
			result.put(RequestConstance.RESULT_CODE, RequestTag.CODE_MISSING_PARAMETER);
			result.put(RequestConstance.RESULT_MSG, "缺少参数");
			return result.toString();
		}

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("password", oldpwd);
		map.put("userName", userName);

		Login login = loginMapper.login(map);
		Wechatusers wechatusers = wechatusersMapper.selectByUsername(userName);

		if (login != null) {
			login.setPassword(newpwd);
			loginMapper.updateByPrimaryKeySelective(login);
			wechatusers.setPassword(newpwd);
			wechatusersMapper.updateByPrimaryKeySelective(wechatusers);
		} else {
			result.put(RequestConstance.RESULT_CODE, RequestTag.CODE_FAIL);
			result.put(RequestConstance.RESULT_MSG, "用户名或者密码错误!");
			return result.toString();
		}

		result.put(RequestConstance.RESULT_CODE, RequestTag.CODE_OK);
		result.put(RequestConstance.RESULT_MSG, RequestConstance.MSG_SUCCESS);
		return result.toString();
	}

}