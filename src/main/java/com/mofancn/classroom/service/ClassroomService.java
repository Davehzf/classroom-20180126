package com.mofancn.classroom.service;

import javax.servlet.http.HttpServletResponse;

import com.mofancn.common.utils.MofancnResult;
import com.mofancn.pojo.MfClassroom;
import com.mofancn.pojo.MfUser;

public interface ClassroomService {
	
	MofancnResult createClassroom(MfClassroom classroom,String token);
	MofancnResult updateClassroom(MfClassroom classroom,String token);
	MofancnResult joinClassroom(MfClassroom classroom, String token);
	MofancnResult queryUserClassroom(String token);
	MofancnResult queryClassroomInfoByPassword(String classroomPassword, String token);
	MofancnResult queryClassroomInfoById(Long classroomId, String token);
	MofancnResult queryCreateClassroomByTeacher(MfUser mfUser,String token);
}
