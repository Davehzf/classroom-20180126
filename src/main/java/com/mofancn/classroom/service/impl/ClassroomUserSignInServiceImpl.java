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
import com.mofancn.pojo.MfClassroom;
import com.mofancn.pojo.MfClassroomSignIn;
import com.mofancn.pojo.MfClassroomSignInExample;
import com.mofancn.pojo.MfClassroomSignInExample.Criteria;
import com.mofancn.pojo.MfClassroomUserSignIn;
import com.mofancn.pojo.MfClassroomUserSignInExample;
import com.mofancn.pojo.MfUser;

@Service
public class ClassroomUserSignInServiceImpl implements classroomUserSignInService {

	@Autowired
	private jedisClient jedisClient;
	@Value("${USER_SESSION_REDIS_KEY}")
	private String USER_SESSION_REDIS_KEY;
	@Value("${CLASSROOM_SIGN_IN_KEY}")
	private String CLASSROOM_SIGN_IN_KEY;
	@Value("${USER_SIGN_IN_KEY}")
	private String USER_SIGN_IN_KEY;

	@Autowired
	private MfClassroomSignInMapper MfClassroomSignInMapper;
	@Autowired
	private MfClassroomUserSignInMapper MfClassroomUserSignInMapper;

	// 地球平均半径
	private static final double EARTH_RADIUS = 6378137;

	// 把经纬度转为度（°）
	private static double rad(Double d) {
		return d * Math.PI / 180.0;
	}

	private double calcDistance(Double signInLatitude1, Double signInLongitude1, Double signInLatitude2,
			Double signInLongitude2) {

		/**
		 * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米
		 * 
		 * @param lng1
		 * @param lat1
		 * @param lng2
		 * @param lat2
		 * @return
		 */

		double radLat1 = rad(signInLatitude1);
		double radLat2 = rad(signInLatitude2);
		double a = radLat1 - radLat2;
		double b = rad(signInLongitude1) - rad(signInLongitude2);
		double s = 2 * Math.asin(Math.sqrt(
				Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;

	}

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

		String isSignIn = jedisClient
				.get(USER_SIGN_IN_KEY + ":" + mfClassroomSignIn.getClassroomSignInId() + mfUser.getUserId());
		if (!StringUtils.isBlank(isSignIn)) {
			return MofancnResult.build(500, "已经签到，请勿重复签到！");
		}

		try {
			String string2 = jedisClient.get(CLASSROOM_SIGN_IN_KEY + ":" + mfClassroomSignIn.getClassroomSignInId());
			if (!StringUtils.isBlank(string2)) {
				MfClassroomSignIn classroomSignIn = JsonUtils.jsonToPojo(string2, MfClassroomSignIn.class);
				if (classroomSignIn.getClassroomSignInValid() != 1) {
					return MofancnResult.build(500, "签到已经停止");
				}
				result = insertUserSignIn(classroomSignIn, mfClassroomSignIn, mfUser);
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
		if (list.size() == 0) {
			return MofancnResult.build(500, "签到ID不存在");
		}
		MfClassroomSignIn classroomSignIn = list.get(0);
		if (classroomSignIn.getClassroomSignInValid() != 1) {
			return MofancnResult.build(500, "签到已经停止");
		}
		result = insertUserSignIn(classroomSignIn, mfClassroomSignIn, mfUser);
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

		double signInLatitude1 = userClassroomSignIn2.getClassroomSignInLatitude();
		double signInLongitude1 = userClassroomSignIn2.getClassroomSignInLongitude();
		double classroomSignInLatitude2 = mfClassroomSignIn1.getClassroomSignInLatitude();
		double classroomSignInLongitude2 = mfClassroomSignIn1.getClassroomSignInLongitude();
		double distance = calcDistance(signInLatitude1, signInLongitude1, classroomSignInLatitude2,
				classroomSignInLongitude2);
		System.out.println(mfClassroomSignIn1.getClassroomSignInAccuracy());
		System.out.println(distance);

		if (Double.compare(distance, mfClassroomSignIn1.getClassroomSignInAccuracy()) > 0) {
			System.out.println("2");
			return MofancnResult.build(500, "签到地理位置不符！");
		}
		System.out.println("1");
		Date date = new Date();

		if (!mfClassroomSignIn1.getClassroomSignInPassword()
				.equals(userClassroomSignIn2.getClassroomSignInPassword())) {
			return MofancnResult.build(500, "签到密码错误");
		}
		MfClassroomUserSignIn mfClassroomUserSignIn = new MfClassroomUserSignIn();

		MfClassroomUserSignInExample mfClassroomUserSignInExample = new MfClassroomUserSignInExample();
		com.mofancn.pojo.MfClassroomUserSignInExample.Criteria criteria = mfClassroomUserSignInExample.createCriteria();
		criteria.andClassroomSignInIdEqualTo(mfClassroomSignIn1.getClassroomSignInId());
		criteria.andUserIdEqualTo(mfUser.getUserId());
		List<MfClassroomUserSignIn> list = MfClassroomUserSignInMapper.selectByExample(mfClassroomUserSignInExample);
		if (list.size() <= 0) {// 用户在开始课堂时未加入课堂需求重新创建
			mfClassroomUserSignIn.setClassroomId(mfClassroomSignIn1.getClassroomId());
			// 1正常签到
			mfClassroomUserSignIn.setClassroomSignInId(mfClassroomSignIn1.getClassroomSignInId());
			mfClassroomUserSignIn.setSignInType((byte) 1);
			mfClassroomUserSignIn.setSignInValid((byte) 1);
			mfClassroomUserSignIn.setUserId(mfUser.getUserId());
			mfClassroomUserSignIn.setCreateTime(new Date());
			mfClassroomUserSignIn.setUpdateTime(new Date());

			try {

				MfClassroomUserSignInMapper.insert(mfClassroomUserSignIn);
				jedisClient.set(USER_SIGN_IN_KEY + ":" + mfClassroomSignIn1.getClassroomSignInId() + mfUser.getUserId(),
						JsonUtils.objectToJson(mfClassroomUserSignIn));
			} catch (Exception e) {
				e.printStackTrace();
				return MofancnResult.build(500, "插入签到记录失败!");
			}
			return MofancnResult.ok(JsonUtils.objectToJson(mfClassroomUserSignIn));
		}
		// 开课签到时老师已经创建签到只需要更新
		mfClassroomUserSignIn = list.get(0);
		mfClassroomUserSignIn.setSignInType((byte) 1);
		mfClassroomUserSignIn.setUpdateTime(new Date());
		try {

			MfClassroomUserSignInMapper.updateByPrimaryKey(mfClassroomUserSignIn);
		} catch (Exception e) {
			e.printStackTrace();
			return MofancnResult.build(500, "签到更新失败");
		}

		return MofancnResult.ok(JsonUtils.objectToJson(mfClassroomUserSignIn));
	}

	/*
	 * 学生查询课堂签到记录
	 * 
	 * @see com.mofancn.classroom.service.classroomUserSignInService#
	 * queryUserSigninRecord(com.mofancn.pojo.MfClassroom, java.lang.String)
	 */
	@Override
	public MofancnResult queryUserSigninRecord(MfClassroom mfClassroom, String token) {

		MofancnResult result = new MofancnResult();
		MfClassroomUserSignIn mfClassroomUserSignIn = new MfClassroomUserSignIn();
		String string = jedisClient.get(USER_SESSION_REDIS_KEY + ":" + token);
		if (StringUtils.isBlank(string)) {
			return MofancnResult.build(500, "登录已过期，请重新登录！");
		}
		MfUser mfUser = JsonUtils.jsonToPojo(string, MfUser.class);

		MfClassroomUserSignInExample mfClassroomUserSignInExample = new MfClassroomUserSignInExample();
		com.mofancn.pojo.MfClassroomUserSignInExample.Criteria criteria = mfClassroomUserSignInExample.createCriteria();
		criteria.andClassroomIdEqualTo(mfClassroom.getClassroomId());
		criteria.andUserIdEqualTo(mfUser.getUserId());
		List<MfClassroomUserSignIn> list = MfClassroomUserSignInMapper.selectByExample(mfClassroomUserSignInExample);
		if (list.size() <= 0) {
			return MofancnResult.build(500, "未查询到签到记录");
		}

		return MofancnResult.ok(list);
	}

	@Override
	public MofancnResult queryAvailableSignin(MfClassroom mfClassroom, String token) {

		MofancnResult result = new MofancnResult();
		MfClassroomUserSignIn mfClassroomUserSignIn = new MfClassroomUserSignIn();
		String string = jedisClient.get(USER_SESSION_REDIS_KEY + ":" + token);
		if (StringUtils.isBlank(string)) {
			return MofancnResult.build(500, "登录已过期，请重新登录！");
		}
		MfUser mfUser = JsonUtils.jsonToPojo(string, MfUser.class);

		MfClassroomSignInExample classroomSignInExample = new MfClassroomSignInExample();
		Criteria create = classroomSignInExample.createCriteria();
		create.andClassroomIdEqualTo(mfClassroom.getClassroomId());
		create.andClassroomSignInValidEqualTo((byte) 1);
		List<MfClassroomSignIn> list = MfClassroomSignInMapper.selectByExample(classroomSignInExample);
		if (list.size() <= 0) {
			return MofancnResult.build(500, "该课堂还没有创建签到！请和老师确认");
		}

		return MofancnResult.ok(list);
	}

}
