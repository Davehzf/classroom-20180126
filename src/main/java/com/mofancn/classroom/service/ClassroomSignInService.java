package com.mofancn.classroom.service;

import com.mofancn.common.utils.MofancnResult;
import com.mofancn.pojo.MfClassroom;
import com.mofancn.pojo.MfClassroomSignIn;
import com.mofancn.pojo.MfUser;

public interface ClassroomSignInService {
	
	MofancnResult createClassrommSignByteacher(MfClassroomSignIn mfClassroomSignIn,String token);
	MofancnResult startClassrommSignByteacher(MfClassroomSignIn mfClassroomSignIn,String token);
	MofancnResult endClassrommSignByteacher(MfClassroomSignIn mfClassroomSignIn,String token);
	MofancnResult queryClassroomSignByTeacher(MfClassroom mfClassroom,String token);
	MofancnResult queryClassroomSigninInfoByTeacher(MfClassroomSignIn mfClassroomSignIn,String token);
}
