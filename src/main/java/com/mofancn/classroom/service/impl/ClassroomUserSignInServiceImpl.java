package com.mofancn.classroom.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mofancn.classroom.service.classroomUserSignInService;
import com.mofancn.common.pojo.jedisClient;
import com.mofancn.common.utils.JsonUtils;
import com.mofancn.common.utils.MofancnResult;
import com.mofancn.mapper.MfClassroomSignInMapper;
import com.mofancn.mapper.MfClassroomUserSignInMapper;
import com.mofancn.pojo.MfClassroomSignIn;
import com.mofancn.pojo.MfClassroomSignInExample;
import com.mofancn.pojo.MfClassroomSignInExample.Criteria;
import com.mofancn.pojo.MfClassroomUserSignIn;
import com.mofancn.pojo.MfUser;

@Service
public class ClassroomUserSignInServiceImpl implements classroomUserSignInService {

	@Autowired
	private jedisClient jedisClient;
	@Value("${USER_SESSION_REDIS_KEY}")
	private String USER_SESSION_REDIS_KEY;
	@Value("${CLASSROOM_SIGN_IN_KEY}")
	private String CLASSROOM_SIGN_IN_KEY;

	@Autowired
	private MfClassroomSignInMapper MfClassroomSignInMapper;
	@Autowired
	private MfClassroomUserSignInMapper MfClassroomUserSignInMapper;

	/**
	 * 创建学生签到记录
	 */
	@Override
	public MofancnResult createClassroomSignInByStudent(MfClassroomSignIn mfClassroomSignIn, String token) {

		MofancnResult result = new MofancnResult();
		MfClassroomUserSignIn mfClassroomUserSignIn = new MfClassroomUserSignIn();
		String string = jedisClient.get(USER_SESSION_REDIS_KEY + ":" + token);
		if (StringUtils.isBlank(string)) {
			return MofancnResult.build(500, "登录已过期，请重新登录！");
		}
		MfUser mfUser = JsonUtils.jsonToPojo(string, MfUser.class);

		try {
			String string2 = jedisClient.get(CLASSROOM_SIGN_IN_KEY + ":" + mfClassroomSignIn.getClassroomSignInId());
			if (!StringUtils.isBlank(string2)) {
				MfClassroomSignIn classroomSignIn = JsonUtils.jsonToPojo(string2, MfClassroomSignIn.class);
				result = insertUserSignIn(mfClassroomSignIn, classroomSignIn, mfUser);
				return result;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		MfClassroomSignInExample example = new MfClassroomSignInExample();
		Criteria criteria = example.createCriteria();
		criteria.andClassroomSignInIdEqualTo(mfClassroomSignIn.getClassroomSignInId());
		List<MfClassroomSignIn> list = MfClassroomSignInMapper.selectByExample(example);
		if (list.get(0) == null) {
			return MofancnResult.build(500, "签到ID不存在");
		}
		MfClassroomSignIn classroomSignIn = list.get(0);
		result = insertUserSignIn(mfClassroomSignIn, classroomSignIn, mfUser);
		try {
			jedisClient.set(CLASSROOM_SIGN_IN_KEY + ":" + mfClassroomSignIn.getClassroomSignInId(),
					JsonUtils.objectToJson(classroomSignIn));

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 私用方法 插入签到记录
	 * 
	 * @return
	 */
	private MofancnResult insertUserSignIn(MfClassroomSignIn mfClassroomSignIn1, MfClassroomSignIn userClassroomSignIn2,
			MfUser mfUser) {

		BigDecimal signInLatitude = userClassroomSignIn2.getClassroomSignInLatitude();
		BigDecimal classroomSignInLatitude = mfClassroomSignIn1.getClassroomSignInLatitude();
		BigDecimal signInLongitude = userClassroomSignIn2.getClassroomSignInLongitude();
		BigDecimal classroomSignInLongitude = mfClassroomSignIn1.getClassroomSignInLongitude();
		Date date = new Date();
/*
		if (signInLatitude - classroomSignInLatitude > mfClassroomSignIn1.getClassroomSignInAccuracy()) {
			return MofancnResult.build(500, "定位信息错误");
		}
*/
/*		if (signInLongitude -classroomSignInLongitude > mfClassroomSignIn1.getClassroomSignInLongitude()) {
			return MofancnResult.build(500, "定位信息错误");
		}
		*/	
		if (!mfClassroomSignIn1.getClassroomSignInPassword().equals(userClassroomSignIn2.getClassroomSignInPassword())) {
			return MofancnResult.build(500, "签到密码错误");
		}
		
		MfClassroomUserSignIn mfClassroomUserSignIn = new MfClassroomUserSignIn();
		mfClassroomUserSignIn.setClassroomId(mfClassroomSignIn1.getClassroomId());
		// 1正常签到
		mfClassroomUserSignIn.setSignInType((byte) 1);
		mfClassroomUserSignIn.setSignInValid((byte) 1);
		mfClassroomUserSignIn.setUserId(mfUser.getUserId());
		mfClassroomUserSignIn.setCreateTime(new Date());
		mfClassroomUserSignIn.setUpdateTime(new Date());
		try {

			MfClassroomUserSignInMapper.insert(mfClassroomUserSignIn);
		} catch (Exception e) {
			e.printStackTrace();
			return MofancnResult.build(500, "插入签到记录失败!");
		}

		return MofancnResult.ok(JsonUtils.objectToJson(mfClassroomUserSignIn));
	}

}
