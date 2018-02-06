package com.mofancn.classroom.controller;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mofancn.classroom.service.ClassroomService;
import com.mofancn.common.utils.MofancnResult;
import com.mofancn.pojo.MfClassroom;
import com.mofancn.pojo.MfUser;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/classroom")
public class ClassroomController {

	@Autowired
	private ClassroomService classroomService;

	@RequestMapping(value = "/createClassroom", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "创建课堂", httpMethod = "POST", response = MofancnResult.class, notes = "创建课堂")
	public MofancnResult addClassroomController(
			@ApiParam(required = true, value = "课堂名称", name = "classroomName") @RequestParam(value = "classroomName") String classroomName,
			@ApiParam(required = true, value = "课堂学分", name = "classroomCredit") @RequestParam(value = "classroomCredit") Integer classroomCredit,
			@ApiParam(required = true, value = "课堂学时", name = "classroomPeriod") @RequestParam(value = "classroomPeriod") Integer classroomPeriod,
			@ApiParam(required = true, value = "课堂上课时间", name = "classroomSchooltime") @RequestParam(value = "classroomSchooltime") Integer classroomSchooltime,
			@ApiParam(required = true, value = "课堂上课地点", name = "classroomPlace") @RequestParam(value = "classroomPlace") String classroomPlace,
			@ApiParam(required = true, value = "课堂重复方式", name = "classroomRepetition") @RequestParam(value = "classroomRepetition") Integer classroomRepetition,
			@ApiParam(required = true, value = "用户TOKEN", name = "token") @RequestParam(value = "token") String token) {
		MfClassroom classroom = new MfClassroom();
		classroom.setClassroomCredit(classroomCredit);
		classroom.setClassroomName(classroomName);
		classroom.setClassroomSchooltime(classroomSchooltime);
		classroom.setClassroomPeriod(classroomPeriod);
		classroom.setClassroomPlace(classroomPlace);
		classroom.setClassroomRepetition(classroomRepetition);

		return classroomService.createClassroom(classroom, token);
	}

	@RequestMapping(value = "/joinClassroom", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "加入课堂", httpMethod = "POST", response = MofancnResult.class, notes = "加入课堂")
	public MofancnResult joinClassroomController(
			@ApiParam(required = true, value = "课堂密码", name = "classroomPassword") @RequestParam(value = "classroomPassword") String classroomPassword,
			@ApiParam(required = true, value = "用户TOKEN", name = "token") @RequestParam(value = "token") String token) {
		MfClassroom classroom = new MfClassroom();
		classroom.setClassroomPassword(classroomPassword);
		
		return classroomService.joinClassroom(classroom, token);
	}

	@RequestMapping(value ="/queryUserClassroom",method=RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "查询用户加入的课堂", httpMethod = "GET", response = MofancnResult.class, notes = "查询用户加入的课堂")
	public MofancnResult queryUserClassroom(@ApiParam(required = true, value = "用户TOKEN", name = "token") @RequestParam(value = "token") String token) {
		return classroomService.queryUserClassroom(token);
	}

	@RequestMapping(value= "/updateClassroom",method=RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "修改课堂信息", httpMethod = "POST", response = MofancnResult.class, notes = "修改课堂信息")
	public MofancnResult updateClassroom(
			@ApiParam(required = true, value = "课堂ID", name = "classroomId") @RequestParam(value = "classroomId") Long classroomId,
			@ApiParam(required = true, value = "课堂名称", name = "classroomName") @RequestParam(value = "classroomName") String classroomName,
			@ApiParam(required = true, value = "课堂学分", name = "classroomCredit") @RequestParam(value = "classroomCredit") Integer classroomCredit,
			@ApiParam(required = true, value = "课堂学时", name = "classroomPeriod") @RequestParam(value = "classroomPeriod") Integer classroomPeriod,
			@ApiParam(required = true, value = "课堂上课时间", name = "classroomSchooltime") @RequestParam(value = "classroomSchooltime") Integer classroomSchooltime,
			@ApiParam(required = true, value = "课堂上课地点", name = "classroomPlace") @RequestParam(value = "classroomPlace") String classroomPlace,
			@ApiParam(required = true, value = "课堂重复方式", name = "classroomRepetition") @RequestParam(value = "classroomRepetition") Integer classroomRepetition,
			@ApiParam(required = true, value = "用户TOKEN", name = "token") @RequestParam(value = "token") String token) {
		MfClassroom classroom = new MfClassroom();
		classroom.setClassroomId(classroomId);
		classroom.setClassroomCredit(classroomCredit);
		classroom.setClassroomName(classroomName);
		classroom.setClassroomSchooltime(classroomSchooltime);
		classroom.setClassroomPeriod(classroomPeriod);
		classroom.setClassroomPlace(classroomPlace);
		classroom.setClassroomRepetition(classroomRepetition);
		return classroomService.updateClassroom(classroom, token);
	}

	@RequestMapping(value = "/queryClassroominfobypwd", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "查询课堂信息byPWD", httpMethod = "GET", response = MofancnResult.class, notes = "查询课堂信息byPWD")
	public MofancnResult queryClassroomInfoByPWD(
			@ApiParam(required = true, value = "课堂密码", name = "classroomPassword") @RequestParam(value = "classroomPassword") String classroomPassword,
			@ApiParam(required = true, value = "用户TOKEN", name = "token") @RequestParam(value = "token") String token) {
		return classroomService.queryClassroomInfoByPassword(classroomPassword, token);
	}
	
	
	
	@RequestMapping(value = "/queryClassroominfobyid", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "查询课堂信息byID", httpMethod = "GET", response = MofancnResult.class, notes = "查询课堂信息byID")
	public MofancnResult queryClassroomInfoByID(
			@ApiParam(required = true, value = "课堂ID", name = "classroomId") @RequestParam(value = "classroomId") Long classroomId,
			@ApiParam(required = true, value = "用户TOKEN", name = "token") @RequestParam(value = "token") String token) {
		return classroomService.queryClassroomInfoById(classroomId, token);
	}
	
	@RequestMapping(value = "/querycreateclassroom", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "查询已经创建的课堂", httpMethod = "GET", response = MofancnResult.class, notes = "查询已经创建的课堂")
	public MofancnResult querysignin(
			@ApiParam(required = true, value = "用户ID", name = "userId") @RequestParam(value = "userId") Long userId,
			@ApiParam(required = true, value = "用户TOKEN", name = "token") @RequestParam(value = "token") String token) {
		MfUser mfUser = new MfUser();
		mfUser.setUserId(userId);
		
		return classroomService.queryCreateClassroomByTeacher(mfUser, token);
	}
	
	

}
