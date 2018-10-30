package com.zkt.project.biology.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zkt.common.core.util.UserInfo;
import com.zkt.project.biology.constant.Constant;
import com.zkt.project.biology.constant.ReturnObjectHandle;
import com.zkt.project.biology.constant.ReturnSimpleHandle;
import com.zkt.project.biology.entity.Cage;
import com.zkt.project.biology.entity.Order;
import com.zkt.project.biology.entity.Pic;
import com.zkt.project.biology.entity.Problem;
import com.zkt.project.biology.entity.Problemmonitor;
import com.zkt.project.biology.entity.Problemrecord;
import com.zkt.project.biology.entity.Sample;
import com.zkt.project.biology.mapper.CageMapper;
import com.zkt.project.biology.mapper.LoginMapper;
import com.zkt.project.biology.mapper.OperatrecordMapper;
import com.zkt.project.biology.mapper.OrderMapper;
import com.zkt.project.biology.mapper.PicMapper;
import com.zkt.project.biology.mapper.ProblemMapper;
import com.zkt.project.biology.mapper.ProblemmonitorMapper;
import com.zkt.project.biology.mapper.ProblemrecordMapper;
import com.zkt.project.biology.mapper.SampleMapper;

import net.sf.json.JSONObject;

@Service
public class QueryExceptionSolvedOrderService {

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private PicMapper picMapper;

	@Autowired
	private SampleMapper sampleMapper;

	@Autowired
	private CageMapper cageMapper;
	
	@Autowired
	private OperatrecordMapper operatrecordMapper;
	
	@Autowired
	private ProblemMapper problemMapper;
	
	@Autowired
	private ProblemrecordMapper problemrecordMapper;
	
	@Autowired
	private LoginMapper loginMapper;
	
	@Autowired
	private ProblemmonitorMapper problemmonitorMapper;
	
	//查询已处理接收异常订单
	
	public ReturnObjectHandle search(JSONObject json) throws Exception {
		
		UserInfo userInfo = null;//RedisContent.getUserInfo();
		String userType = userInfo.getUserType();
		String userID = String.valueOf(userInfo.getUserId());// 锁定权限
		JSONObject userJson = JSONObject.fromObject(userInfo.getUser());
		String city = userJson.getString("city");
		String area = userJson.getString("area");
		
		String orderNo = json.getString("orderNo");
		String cageno = json.getString("cageno");
		String classify = json.getString("classify");
		
		Integer draw = Integer.parseInt(json.getString("draw"));
		Integer from = Integer.parseInt(json.getString("start"));
		Integer pageSize = Integer.parseInt(json.getString("pageCount"));
		
		Map<String, Object> map = new HashMap<>();
		if(Constant.USER_TYPE_CITY.equals(userType) || Constant.USER_TYPE_AREA.equals(userType)){
			map.put("city", city);
			map.put("area", area);
		}else if(Constant.USER_TYPE_HOSPITAL.equals(userType)){
			map.put("senderid", userID);//给发送方推送
		}else if(Constant.USER_TYPE_TRANSPORT.equals(userType)){
			map.put("transportid", userID);//给运输方推送
		}	
		map.put("orderNo", orderNo);
		map.put("cageno", cageno);		
		map.put("classify", classify);		
		map.put("pageSize", pageSize);
		map.put("from", from);

		List<Order> orderList = orderMapper.selectProblemOrder(map);
		Integer orderListCount = orderMapper.countByProblemOrder(map);
		//封装返回参数
		ReturnObjectHandle returnHandle = ReturnObjectHandle.createServerHandle();
		returnHandle.setDraw(draw);
		returnHandle.setData(orderList);
		returnHandle.setPageCount(pageSize);
		returnHandle.setDataMaxCount(orderListCount);
		returnHandle.setDataMaxPage(
				orderListCount % pageSize == 0 ? orderListCount / pageSize : orderListCount / pageSize + 1);
		return returnHandle;
	}
		
	//查询已处理运输异常订单
	
	public ReturnObjectHandle searchProblemmonitor(JSONObject json) throws Exception {
		
		UserInfo userInfo = null;//RedisContent.getUserInfo();
		String userType = userInfo.getUserType();
		String userID = String.valueOf(userInfo.getUserId());//锁定权限
		JSONObject userJson = JSONObject.fromObject(userInfo.getUser());
		String city = userJson.getString("city");
		String area = userJson.getString("area");
		
		String orderNo = json.getString("orderNo");
		String status = json.getString("status");
		
		Integer draw = Integer.parseInt(json.getString("draw"));
		Integer from = Integer.parseInt(json.getString("start"));
		Integer pageSize = Integer.parseInt(json.getString("pageCount"));
		
		Map<String, Object> map = new HashMap<>();
		if(Constant.USER_TYPE_CITY.equals(userType) || Constant.USER_TYPE_AREA.equals(userType)){
			map.put("city", city);
			map.put("area", area);
		}else if(Constant.USER_TYPE_HOSPITAL.equals(userType)){
			map.put("senderid", userID);//给发送方推送
		}else if(Constant.USER_TYPE_TRANSPORT.equals(userType)){
			map.put("transportid", userID);//给运输方推送
		}		
		map.put("orderNo", orderNo);
		map.put("status", status);				
		map.put("pageSize", pageSize);
		map.put("from", from);
		
		List<Problemmonitor> problemmonitorList = problemmonitorMapper.selectAllOver(map);
		Integer problemmonitorListCount = problemmonitorMapper.countAllOver(map);
		// 封装返回参数
		ReturnObjectHandle returnHandle = ReturnObjectHandle.createServerHandle();
		returnHandle.setDraw(draw);
		returnHandle.setData(problemmonitorList);
		returnHandle.setPageCount(pageSize);
		returnHandle.setDataMaxCount(problemmonitorListCount);
		returnHandle.setDataMaxPage(
				problemmonitorListCount % pageSize == 0 ? problemmonitorListCount / pageSize : problemmonitorListCount / pageSize + 1);
		return returnHandle;
	}
		
	//查询已处理异常订单详情
	
	public ReturnSimpleHandle detail(JSONObject json) throws Exception {
		
		String orderNo = json.getString("orderNo");
		Order order = orderMapper.selectByOrderNo(orderNo);
		//订单状态
		String orderStatus = "";
		switch (order.getOrderStatus()) {
		case Constant.ORDER_STATUS_NEW:
			orderStatus = "新建";
			order.setOrderStatus(orderStatus);
			break;
		case Constant.ORDER_STATUS_PACK:
			orderStatus = "已装箱";
			order.setOrderStatus(orderStatus);
			break;
		case Constant.ORDER_STATUS_TRANSPORT:
			orderStatus = "运输中";
			order.setOrderStatus(orderStatus);
			break;
		case Constant.ORDER_STATUS_FINISH:
			orderStatus = "已完成";
			order.setOrderStatus(orderStatus);
			break;
		case Constant.ORDER_STATUS_XIETIAO:
			orderStatus = "待仲裁";
			order.setOrderStatus(orderStatus);
			break;
		default:
			break;
		}
		//问题件状态
		String isProblem = "";
		switch (order.getIsProblem()) {
		case Constant.IS_PROBLEM_Y:
			isProblem = "是异常件";
			order.setIsProblem(isProblem);
			break;
		case Constant.IS_PROBLEM_N:
			isProblem = "不是异常件";
			order.setIsProblem(isProblem);
			break;
		default:
			break;
		}
		//下单分类
		String classify = "";
		switch (order.getClassify()) {
		case Constant.ORDER_CLASSIFY_PC:
			classify = "PC端录入";
			order.setClassify(classify);
			break;
		case Constant.ORDER_CLASSIFY_WEIXIN:
			classify = "微信录入";
			order.setClassify(classify);
			break;
		default:
			break;
		}
		Cage cage = cageMapper.selectByCageno(order.getCageNo());
		//箱子状态
		String state = "";
		switch (cage.getState()) {
		case Constant.CAGESTATE_FREE:
			state = "空闲";
			cage.setState(state);
			break;
		case Constant.CAGESTATE_ORDER:
			state = "已绑定订单";
			cage.setState(state);
			break;
		case Constant.CAGESTATE_TRANSPORT:
			state = "在途";
			cage.setState(state);
			break;
		case Constant.CAGESTATE_FAULT:
			state = "故障";
			cage.setState(state);
			break;
		default:
			break;
		}

		List<Sample> sample = sampleMapper.selectByKeys(orderNo);
		for(int i=0; i<sample.size(); i++){
			sample.get(i).setSpare4(order.getSampleclassify());//样本类型
			sample.get(i).setSpare5(order.getNum());//样本数量
		}
		
		Map<String, Object> classify4 = new HashMap<>();
		classify4.put("orderId", orderNo);
		classify4.put("classify", Constant.PICTURE_TYPE_PROBLEM);		
		List<Pic> picAddresList4 = picMapper.selectByClassify(classify4);
		
		List<Problemmonitor> problemmonitor = problemmonitorMapper.selectByOrderNo(orderNo);
		for(int i=0; i<problemmonitor.size(); i++){
			
			//处理状态
			String status = "";
			switch (problemmonitor.get(i).getStatus()) {
			case Constant.TREATMENT_SITUATION_ING:
				status = "处理中";
				problemmonitor.get(i).setStatus(status);;
				break;
			case Constant.TREATMENT_SITUATION_OVER:
				status = "处理完毕";
				problemmonitor.get(i).setStatus(status);
				break;
			default:
				break;
			}
			
			//异常描述
			String discribtion = "";
			switch (problemmonitor.get(i).getDiscribtion()) {
			case Constant.TLIMITUP_ERROR:
				discribtion = "温度上限异常";
				problemmonitor.get(i).setDiscribtion(discribtion);
				break;
			case Constant.TLIMITDOWN_ERROR:
				discribtion = "温度下限异常";
				problemmonitor.get(i).setDiscribtion(discribtion);
				break;
			case Constant.HLIMITUP_ERROR:
				discribtion = "湿度上限异常";
				problemmonitor.get(i).setDiscribtion(discribtion);
				break;
			case Constant.HLIMITDOWN_ERROR:
				discribtion = "湿度下限异常";
				problemmonitor.get(i).setDiscribtion(discribtion);
				break;
			case Constant.CAGE_OPEN_ERROR:
				discribtion = "箱体非正常打开";
				problemmonitor.get(i).setDiscribtion(discribtion);
				break;
			case Constant.ARRIVETIME_ERROR:
				discribtion = "预计时间未到达报警";
				problemmonitor.get(i).setDiscribtion(discribtion);
				break;
			default:
				break;
			}
			
		}
		
		Problemrecord problemrecord = problemrecordMapper.selectByOrderNo(orderNo);
		
		Problem problem = problemMapper.selectByOrderNo(orderNo);
		//处理状态
		String status = "";
		switch (problem.getStatus()) {
		case Constant.TREATMENT_SITUATION_ING:
			status = "处理中";
			problem.setStatus(status);
			break;
		case Constant.TREATMENT_SITUATION_OVER:
			state = "处理完毕";
			problem.setStatus(status);
			break;
		default:
			break;
		}
				
		JSONObject result = new JSONObject();
		result.put("order", order);//订单信息
		result.put("cage", cage);//箱体信息
		result.put("sample", sample);//样本信息
		result.put("problemmonitor", problemmonitor);//运输异常信息
		result.put("picAddresList4", picAddresList4);
		result.put("problem", problem);//异常报告
		result.put("problemrecord", problemrecord);//异常报告审核流程
		
		ReturnSimpleHandle returnHandle = ReturnSimpleHandle.createServerHandle();
		returnHandle.setData(result);
		return returnHandle;
	}
	
	
}


