package com.mofancn.classroom.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ObjectUtils.Null;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.DailyRollingFileAppender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mofancn.classroom.service.ClassroomService;
import com.mofancn.common.pojo.jedisClient;
import com.mofancn.common.utils.JsonUtils;
import com.mofancn.common.utils.MofancnResult;
import com.mofancn.mapper.MfClassroomMapper;
import com.mofancn.mapper.MfClassroomRelationMapper;
import com.mofancn.pojo.MfClassroom;
import com.mofancn.pojo.MfClassroomExample;
import com.mofancn.pojo.MfClassroomRelation;
import com.mofancn.pojo.MfClassroomRelationExample;
import com.mofancn.pojo.MfClassroomRelationExample.Criteria;

import com.mofancn.pojo.MfUser;

@Service
public class ClassroomServiceImpl implements ClassroomService {

	@Autowired
	private MfClassroomMapper MfClassroomMapper;

	@Autowired
	private MfClassroomRelationMapper MfClassroomRelationMapper;
	@Autowired
	private jedisClient jedisClient;

	@Value("${CLASSROOM_PASSWORD_KEY}")
	private String CLASSROOM_PASSWORD_KEY;

	@Value("${CLASSROOM_PASSWORD_INIT_VALUE}")
	private String CLASSROOM_PASSWORD_INIT_VALUE;

	@Value("${USER_SESSION_REDIS_KEY}")
	private String USER_SESSION_REDIS_KEY;
	@Value("${SSO_SESSION_EXPIRE}")
	private int SSO_SESSION_EXPIRE;

	@Override
	public MofancnResult createClassroom(MfClassroom classroom, String token) {

		MofancnResult result = new MofancnResult();
		String string = jedisClient.get(USER_SESSION_REDIS_KEY + ":" + token);
		if (StringUtils.isBlank(string)) {
			return MofancnResult.build(500, "登录已过期，请重新登录！");
		}
		MfUser mfUser = JsonUtils.jsonToPojo(string, MfUser.class);

		String string2 = jedisClient.get(CLASSROOM_PASSWORD_KEY);
		if (StringUtils.isBlank(string2)) {
			jedisClient.set(CLASSROOM_PASSWORD_KEY, CLASSROOM_PASSWORD_INIT_VALUE);
		}
		long classroomPassword = jedisClient.incr(CLASSROOM_PASSWORD_KEY);
		classroom.setClassroomPassword(classroomPassword + "");

		classroom.setClassroomManager(mfUser.getUserId());
		classroom.setClassroomValid((byte) 1);
		classroom.setCreateTime(new Date());
		classroom.setUpdateTime(new Date());
		try {
			int insert = MfClassroomMapper.insert(classroom);

			result = MofancnResult.ok(classroomPassword);
		} catch (Exception e) {
			// TODO: handle exception
			result = MofancnResult.build(500, "新建课堂数据失败！");

		}
		return result;

	}

	@Override
	public MofancnResult joinClassroom(MfClassroom classroom, String token) {
		MofancnResult result = new MofancnResult();
		String string = jedisClient.get(USER_SESSION_REDIS_KEY + ":" + token);
		if (StringUtils.isBlank(string)) {
			return MofancnResult.build(500, "登录已过期，请重新登录！");
		}
		MfUser mfUser = JsonUtils.jsonToPojo(string, MfUser.class);

		MfClassroomExample mfClassroomExample = new MfClassroomExample();
		com.mofancn.pojo.MfClassroomExample.Criteria criteria = mfClassroomExample.createCriteria();
		criteria.andClassroomPasswordEqualTo(classroom.getClassroomPassword());
		List<MfClassroom> list = MfClassroomMapper.selectByExample(mfClassroomExample);
		if (list.isEmpty()) {
			return MofancnResult.build(500, "密码失效！");
		}
		MfClassroom classroom2 = list.get(0);

		MfClassroomRelation mfClassroomRelation = new MfClassroomRelation();
		mfClassroomRelation.setUserId(mfUser.getUserId());
		mfClassroomRelation.setClassroomId(classroom2.getClassroomId());
		mfClassroomRelation.setRecord(0);
		mfClassroomRelation.setValid((byte) 1);
		mfClassroomRelation.setCreateTime(new Date());
		mfClassroomRelation.setUpdateTime(new Date());
		try {
			MfClassroomRelationMapper.insert(mfClassroomRelation);
			result = MofancnResult.ok(classroom2.getClassroomId());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

			result = MofancnResult.build(500, "加入班级失败");
		}

		return result;
	}

	@Override
	public MofancnResult queryUserClassroom(String token) {
		MofancnResult result = new MofancnResult();
		String string = jedisClient.get(USER_SESSION_REDIS_KEY + ":" + token);
		if (StringUtils.isBlank(string)) {
			return MofancnResult.build(500, "登录已过期，请重新登录！");
		}
		MfUser mfUser = JsonUtils.jsonToPojo(string, MfUser.class);
		MfClassroomRelationExample example = new MfClassroomRelationExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(mfUser.getUserId());
		List<MfClassroomRelation> list = MfClassroomRelationMapper.selectByExample(example);

		return MofancnResult.ok(list);
	}

	@Override
	public MofancnResult updateClassroom(MfClassroom classroom, String token) {
		MofancnResult result = new MofancnResult();
		String string = jedisClient.get(USER_SESSION_REDIS_KEY + ":" + token);
		if (StringUtils.isBlank(string)) {
			return MofancnResult.build(500, "登录已过期，请重新登录！");
		}
		MfUser mfUser = JsonUtils.jsonToPojo(string, MfUser.class);

		classroom.setUpdateTime(new Date());
		try {

			MfClassroomMapper.updateByPrimaryKey(classroom);
			result = MofancnResult.ok();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			result = MofancnResult.build(500, "修改课堂失败！");
		}
		return MofancnResult.ok();
	}

	@Override
	public MofancnResult queryClassroomInfoByPassword(String classroomPassword, String token) {

		String string = jedisClient.get(USER_SESSION_REDIS_KEY + ":" + token);
		if (StringUtils.isBlank(string)) {
			return MofancnResult.build(500, "登录已过期，请重新登录！");
		}

		MfClassroomExample classroomExample = new MfClassroomExample();
		com.mofancn.pojo.MfClassroomExample.Criteria criteria = classroomExample.createCriteria();
		criteria.andClassroomPasswordEqualTo(classroomPassword);
		List<MfClassroom> list = MfClassroomMapper.selectByExample(classroomExample);
		if (list.isEmpty()) {
			return MofancnResult.build(500, "课堂不存在");
		}
		MfClassroom mfClassroom = list.get(0);

		return MofancnResult.ok(JsonUtils.objectToJson(mfClassroom));
	}

	@Override
	public MofancnResult queryClassroomInfoById(Long classroomId, String token) {
		String string = jedisClient.get(USER_SESSION_REDIS_KEY + ":" + token);
		if (StringUtils.isBlank(string)) {
			return MofancnResult.build(500, "登录已过期，请重新登录！");
		}
		MfClassroom mfClassroom = MfClassroomMapper.selectByPrimaryKey(classroomId);
		if (mfClassroom.toString().isEmpty()) {
			return MofancnResult.build(500, "Id错误！");
		}

		return MofancnResult.ok(JsonUtils.objectToJson(mfClassroom));
	}

	@Override
	public MofancnResult queryCreateClassroomByTeacher(MfUser mfUser, String token) {

		MofancnResult result = new MofancnResult();
		String string = jedisClient.get(USER_SESSION_REDIS_KEY + ":" + token);
		if (StringUtils.isBlank(string)) {
			return MofancnResult.build(500, "登录已过期，请重新登录！");
		}
		MfUser mfUser2 = JsonUtils.jsonToPojo(string, MfUser.class);
		if (!mfUser.getUserId().equals(mfUser2.getUserId())) {
			return MofancnResult.build(500, "用户名不一致");
		}
/*
 * 加这个判断有错误
 * 		if (mfUser2.getUserGroup().equals(1)) {
			return MofancnResult.build(500, "学生暂不开放");
		}
*/
		MfClassroomExample mfClassroomExample = new MfClassroomExample();
		com.mofancn.pojo.MfClassroomExample.Criteria criteria = mfClassroomExample.createCriteria();
		criteria.andClassroomManagerEqualTo(mfUser.getUserId());
		List<MfClassroom> list = MfClassroomMapper.selectByExample(mfClassroomExample);
		if (list.size() == 0) {
			return MofancnResult.build(500, "还未创建课堂");

		}

		return MofancnResult.ok(list);
	}

}
