package com.mofancn.classroom.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mofancn.classroom.service.classroomUserSignInService;
import com.mofancn.common.utils.MofancnResult;
import com.mofancn.pojo.MfClassroom;
import com.mofancn.pojo.MfClassroomSignIn;
import com.mofancn.pojo.MfClassroomUserSignIn;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/student")
public class UserSignInController {

	@Autowired
	private classroomUserSignInService classroomUserSignInService;

	@RequestMapping(value = "/usersignin", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "用户签到", httpMethod = "POST", response = MofancnResult.class, notes = "用户签到")
	public MofancnResult userSignIn(
			@ApiParam(required = true, value = "签到ID", name = "classroomSignInId") @RequestParam(value = "classroomSignInId") Long classroomSignInId,
			@ApiParam(required = true, value = "定位纬度", name = "classroomSignInLatitude") @RequestParam(value = "classroomSignInLatitude") Double classroomSignInLatitude,
			@ApiParam(required = true, value = "定位经度", name = "classroomSignInLongitude") @RequestParam(value = "classroomSignInLongitude") Double classroomSignInLongitude,
			@ApiParam(required = true, value = "签到密码", name = "classroomSignInPassword") @RequestParam(value = "classroomSignInPassword") String classroomSignInPassword,
			@ApiParam(required = true, value = "用户TOKEN", name = "token") @RequestParam(value = "token") String token) {
		MfClassroomSignIn mfClassroomSignIn = new MfClassroomSignIn();
		mfClassroomSignIn.setClassroomSignInId(classroomSignInId);
		mfClassroomSignIn.setClassroomSignInLatitude(classroomSignInLatitude);
		mfClassroomSignIn.setClassroomSignInLongitude(classroomSignInLongitude);
		mfClassroomSignIn.setClassroomSignInPassword(classroomSignInPassword);

		return classroomUserSignInService.createClassroomSignInByStudent(mfClassroomSignIn, token);
		
	}
	@RequestMapping(value = "/querySigninRecord", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "查询签到记录", httpMethod = "GET", response = MofancnResult.class, notes = "查询签到记录")
	public MofancnResult querySigninRecord(
			@ApiParam(required = true, value = "课堂ID", name = "classroomId") @RequestParam(value = "classroomId") Long classroomId,
			@ApiParam(required = true, value = "用户TOKEN", name = "token") @RequestParam(value = "token") String token) {
		 MfClassroom mfClassroom = new MfClassroom();
		 mfClassroom.setClassroomId(classroomId);
		
		return classroomUserSignInService.queryUserSigninRecord(mfClassroom, token);
		
	}
	@RequestMapping(value = "/queryAvailableSignin", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "查询课堂可用签到", httpMethod = "GET", response = MofancnResult.class, notes = "查询课堂可用签到")
	public MofancnResult queryAvailableSignin(
			@ApiParam(required = true, value = "课堂ID", name = "classroomId") @RequestParam(value = "classroomId") Long classroomId,
			@ApiParam(required = true, value = "用户TOKEN", name = "token") @RequestParam(value = "token") String token) {
		 MfClassroom mfClassroom = new MfClassroom();
		 mfClassroom.setClassroomId(classroomId);
		
		return classroomUserSignInService.queryAvailableSignin(mfClassroom, token);
		
	}
	
	
	
}
