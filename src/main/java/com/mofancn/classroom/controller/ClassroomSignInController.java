package com.mofancn.classroom.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mofancn.classroom.service.ClassroomSignInService;
import com.mofancn.classroom.service.classroomUserSignInService;
import com.mofancn.common.utils.MofancnResult;
import com.mofancn.pojo.MfClassroomSignIn;
import com.mofancn.pojo.MfUser;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/signin")
public class ClassroomSignInController {

	@Autowired
	private ClassroomSignInService classroomSignInService;

	@RequestMapping(value = "/createsignin", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "创建签到", httpMethod = "POST", response = MofancnResult.class, notes = "创建签到")
	public MofancnResult createSignIn(
			@ApiParam(required = true, value = "课堂ID", name = "classroomId") @RequestParam(value = "classroomId") Long classroomId,
			@ApiParam(required = true, value = "定位纬度", name = "classroomSignInLatitude") @RequestParam(value = "classroomSignInLatitude") BigDecimal classroomSignInLatitude,
			@ApiParam(required = true, value = "定位经度", name = "classroomSignInLongitude") @RequestParam(value = "classroomSignInLongitude") BigDecimal classroomSignInLongitude,
			@ApiParam(required = true, value = "定位精度", name = "classroomSignInAccuracy") @RequestParam(value = "classroomSignInAccuracy") Long classroomSignInAccuracy,
			
			@ApiParam(required = true, value = "用户TOKEN", name = "token") @RequestParam(value = "token") String token) {
		MfClassroomSignIn mfClassroomSignIn = new MfClassroomSignIn();
		mfClassroomSignIn.setClassroomId(classroomId);
		mfClassroomSignIn.setClassroomSignInLatitude(classroomSignInLatitude);
		mfClassroomSignIn.setClassroomSignInLongitude(classroomSignInLongitude);
		mfClassroomSignIn.setClassroomSignInAccuracy(classroomSignInAccuracy);
		return classroomSignInService.createClassrommSignByteacher(mfClassroomSignIn, token);
	}
	@RequestMapping(value = "/startsignin", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "开始签到", httpMethod = "POST", response = MofancnResult.class, notes = "开始签到")
	public MofancnResult startSignIn(
			@ApiParam(required = true, value = "签到ID", name = "ClassroomSignInId") @RequestParam(value = "ClassroomSignInId") Long ClassroomSignInId,
			@ApiParam(required = true, value = "用户TOKEN", name = "token") @RequestParam(value = "token") String token) {
		MfClassroomSignIn mfClassroomSignIn = new MfClassroomSignIn();
		mfClassroomSignIn.setClassroomSignInId(ClassroomSignInId);

		return classroomSignInService.startClassrommSignByteacher(mfClassroomSignIn, token);
	}
	@RequestMapping(value = "/endsignin", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "停止签到", httpMethod = "POST", response = MofancnResult.class, notes = "停止签到")
	public MofancnResult endSignIn(
			@ApiParam(required = true, value = "签到ID", name = "ClassroomSignInId") @RequestParam(value = "ClassroomSignInId") Long ClassroomSignInId,
			@ApiParam(required = true, value = "用户TOKEN", name = "token") @RequestParam(value = "token") String token) {
		MfClassroomSignIn mfClassroomSignIn = new MfClassroomSignIn();
		mfClassroomSignIn.setClassroomSignInId(ClassroomSignInId);

		return classroomSignInService.endClassrommSignByteacher(mfClassroomSignIn, token);
	}
	@RequestMapping(value = "/querysignin", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "查询已经创建的签到", httpMethod = "GET", response = MofancnResult.class, notes = "查询已经创建的签到")
	public MofancnResult querysignin(
			@ApiParam(required = true, value = "用户ID", name = "userId") @RequestParam(value = "userId") Long userId,
			@ApiParam(required = true, value = "用户TOKEN", name = "token") @RequestParam(value = "token") String token) {
		MfUser mfUser = new MfUser();
		mfUser.setUserId(userId);
		
		return classroomSignInService.queryClassroomSignByTeacher(mfUser, token);
	}
}
