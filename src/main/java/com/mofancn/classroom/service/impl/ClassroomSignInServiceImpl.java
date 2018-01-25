package com.mofancn.classroom.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.druid.support.json.JSONUtils;
import com.mofancn.classroom.service.ClassroomSignInService;
import com.mofancn.common.pojo.jedisClient;
import com.mofancn.common.utils.JsonUtils;
import com.mofancn.common.utils.MofancnResult;
import com.mofancn.mapper.MfClassroomSignInMapper;
import com.mofancn.pojo.MfClassroomSignIn;
import com.mofancn.pojo.MfClassroomSignInExample;
import com.mofancn.pojo.MfClassroomSignInExample.Criteria;
import com.mofancn.pojo.MfUser;

@Service
public class ClassroomSignInServiceImpl implements ClassroomSignInService {

	@Autowired
	private MfClassroomSignInMapper MfClassroomSignInMapper;
	@Autowired
	private jedisClient jedisClient;
	@Value("${USER_SESSION_REDIS_KEY}")
	private String USER_SESSION_REDIS_KEY;
	@Value("${CLASSROOM_SIGN_IN_KEY}")
	private String CLASSROOM_SIGN_IN_KEY;

	/**
	 * 老师创建签到
	 */
	@Override
	public MofancnResult createClassrommSignByteacher(MfClassroomSignIn mfClassroomSignIn, String token) {
		// TODO Auto-generated method stub
		MofancnResult result = new MofancnResult();
		String string = jedisClient.get(USER_SESSION_REDIS_KEY + ":" + token);
		if (StringUtils.isBlank(string)) {
			return MofancnResult.build(500, "登录已过期，请重新登录！");
		}
		MfUser mfUser = JsonUtils.jsonToPojo(string, MfUser.class);
		if (mfUser.getUserGroup() == 1) {
			return MofancnResult.build(500, "学生创建课堂签到，暂不开放！");
		}
		if (false) {
			// 加入课堂管理者判断
		}

		// 生成4位随机数字
		String str = "123456789";
		StringBuilder sb = new StringBuilder(4);
		for (int i = 0; i < 4; i++) {
			char ch = str.charAt(new Random().nextInt(str.length()));
			sb.append(ch);
		}
		String classroomSignInPassword = sb.toString();
		mfClassroomSignIn.setClassroomSignInPassword(classroomSignInPassword);
		mfClassroomSignIn.setClassroomSignInManager(mfUser.getUserId());
		mfClassroomSignIn.setClassroomSignInValid((byte) 0);
		// mfClassroomSignIn.setClassrommSignInFrequency(1);
		mfClassroomSignIn.setClassroomSignInStartTime(new Date());
		mfClassroomSignIn.setClassroomSignInEndTime(new Date());
		mfClassroomSignIn.setClassroomSignInCreateTime(new Date());
		mfClassroomSignIn.setClassroomSignInUpdateTime(new Date());
		try {
			MfClassroomSignInMapper.insert(mfClassroomSignIn);
			result = MofancnResult.ok(JsonUtils.objectToJson(mfClassroomSignIn));
			jedisClient.set(CLASSROOM_SIGN_IN_KEY + ":" + mfClassroomSignIn.getClassroomSignInId(),
					JsonUtils.objectToJson(mfClassroomSignIn));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return MofancnResult.build(500, "插入数据失败！");
		}
		return result;
	}

	@Override
	public MofancnResult startClassrommSignByteacher(MfClassroomSignIn mfClassroomSignIn, String token) {
		MofancnResult result = new MofancnResult();
		String string = jedisClient.get(USER_SESSION_REDIS_KEY + ":" + token);
		if (StringUtils.isBlank(string)) {
			return MofancnResult.build(500, "登录已过期，请重新登录！");
		}
		MfUser mfUser = JsonUtils.jsonToPojo(string, MfUser.class);
		if (mfUser.getUserId().equals(mfClassroomSignIn.getClassroomSignInManager())) {
			System.out.println("usrid:" + mfUser.getUserId());
			System.out.println("Manager:" + mfUser.getUserId());
			return MofancnResult.build(500, "非所有者！");

		}
		// 添加相关用户消息推送（开始签到啦）
		String string2 = jedisClient.get(CLASSROOM_SIGN_IN_KEY + ":" + mfClassroomSignIn.getClassroomSignInId());
		if (string2.isEmpty()) {
			return MofancnResult.build(500, "课堂ID错误或不存在！");
		}

		MfClassroomSignIn classroomSignIn = JsonUtils.jsonToPojo(string2, MfClassroomSignIn.class);
		if (classroomSignIn.getClassroomSignInValid().equals(1)) {
			return MofancnResult.build(500, "已经开始签到,请勿重复提交！");

		}
		classroomSignIn.setClassroomSignInValid((byte) 1);
		try {

			MfClassroomSignInMapper.updateByPrimaryKey(classroomSignIn);
			jedisClient.set(CLASSROOM_SIGN_IN_KEY + ":" + classroomSignIn.getClassroomSignInId(),
					JsonUtils.objectToJson(classroomSignIn));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}

		return MofancnResult.ok(classroomSignIn.getClassroomId());
	}

	@Override
	public MofancnResult endClassrommSignByteacher(MfClassroomSignIn mfClassroomSignIn, String token) {

		MofancnResult result = new MofancnResult();
		String string = jedisClient.get(USER_SESSION_REDIS_KEY + ":" + token);
		if (StringUtils.isBlank(string)) {
			return MofancnResult.build(500, "登录已过期，请重新登录！");
		}
		MfUser mfUser = JsonUtils.jsonToPojo(string, MfUser.class);
		if (mfUser.getUserId().equals(mfClassroomSignIn.getClassroomSignInManager())) {
			System.out.println("usrid:" + mfUser.getUserId());
			System.out.println("Manager:" + mfUser.getUserId());
			return MofancnResult.build(500, "非所有者！");

		}
		// 添加相关用户消息推送（开始签到啦）
		String string2 = jedisClient.get(CLASSROOM_SIGN_IN_KEY + ":" + mfClassroomSignIn.getClassroomSignInId());
		if (string2.isEmpty()) {
			return MofancnResult.build(500, "课堂ID错误或不存在！");
		}
		MfClassroomSignIn classroomSignIn = JsonUtils.jsonToPojo(string2, MfClassroomSignIn.class);
		if (classroomSignIn.getClassroomSignInValid().equals(0)) {
			return MofancnResult.build(500, "已经停止签到,请勿重复提交！");

		}
		classroomSignIn.setClassroomSignInValid((byte) 0);
		try {

			MfClassroomSignInMapper.updateByPrimaryKey(classroomSignIn);
			jedisClient.set(CLASSROOM_SIGN_IN_KEY + ":" + classroomSignIn.getClassroomSignInId(),
					JsonUtils.objectToJson(classroomSignIn));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}

		return MofancnResult.ok(classroomSignIn.getClassroomId());
	}
	/**
	 * 查询用户已经创建的签到
	 */

	@Override
	public MofancnResult queryClassroomSignByTeacher(MfUser mfUser, String token) {
		MofancnResult result = new MofancnResult();
		String string = jedisClient.get(USER_SESSION_REDIS_KEY + ":" + token);
		if (StringUtils.isBlank(string)) {
			return MofancnResult.build(500, "登录已过期，请重新登录！");
		}
		MfUser mfUser2 = JsonUtils.jsonToPojo(string, MfUser.class);
		if (!mfUser.getUserId().equals(mfUser2.getUserId())) {
			return MofancnResult.build(500, "用户名不一致");
		}
		if (mfUser.getUserGroup().equals(1)) {
			return MofancnResult.build(500, "学生暂不开放");
		}
		
		MfClassroomSignInExample mfClassroomSignInExample = new MfClassroomSignInExample();
		Criteria criteria = mfClassroomSignInExample.createCriteria();
		criteria.andClassroomSignInManagerEqualTo(mfUser.getUserId());
		List<MfClassroomSignIn> list = MfClassroomSignInMapper.selectByExample(mfClassroomSignInExample);
		
		if (!list.get(0).equals(null)) {
			
			return MofancnResult.build(500, "该用户还未创建签到");
		}
		
		
		
		return MofancnResult.ok(list);
	}

}
