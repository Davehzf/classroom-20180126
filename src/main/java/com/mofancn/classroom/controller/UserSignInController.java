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
import com.mofancn.pojo.MfClassroomSignIn;
import com.mofancn.pojo.MfClassroomUserSignIn;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
public class UserSignInController {

	@Autowired
	private classroomUserSignInService classroomUserSignInService;

	@RequestMapping(value = "/usersignin", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "用户签到", httpMethod = "POST", response = MofancnResult.class, notes = "用户签到")
	public MofancnResult userSignIn(
			@ApiParam(required = true, value = "签到ID", name = "classroomSignInId") @RequestParam(value = "classroomSignInId") Long classroomSignInId,
			@ApiParam(required = true, value = "定位纬度", name = "classroomSignInLatitude") @RequestParam(value = "classroomSignInLatitude") BigDecimal classroomSignInLatitude,
			@ApiParam(required = true, value = "定位经度", name = "classroomSignInLongitude") @RequestParam(value = "classroomSignInLongitude") BigDecimal classroomSignInLongitude,
			@ApiParam(required = true, value = "签到密码", name = "classroomSignInPassword") @RequestParam(value = "classroomSignInPassword") String classroomSignInPassword,
			@ApiParam(required = true, value = "用户TOKEN", name = "token") @RequestParam(value = "token") String token) {
		MfClassroomSignIn mfClassroomSignIn = new MfClassroomSignIn();
		mfClassroomSignIn.setClassroomSignInId(classroomSignInId);
		mfClassroomSignIn.setClassroomSignInLatitude(classroomSignInLatitude);
		mfClassroomSignIn.setClassroomSignInLongitude(classroomSignInLongitude);
		mfClassroomSignIn.setClassroomSignInPassword(classroomSignInPassword);
		
		classroomUserSignInService.createClassroomSignInByStudent(mfClassroomSignIn, token);
		return MofancnResult.ok();
	}
}
