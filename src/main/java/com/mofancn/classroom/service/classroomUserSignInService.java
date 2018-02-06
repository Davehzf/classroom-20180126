package com.mofancn.classroom.service;

import com.mofancn.common.utils.MofancnResult;
import com.mofancn.pojo.MfClassroom;
import com.mofancn.pojo.MfClassroomSignIn;

public interface classroomUserSignInService {
	
	MofancnResult createClassroomSignInByStudent(MfClassroomSignIn mfClassroomSignIn,String token);
	MofancnResult queryUserSigninRecord(MfClassroom mfClassroom,String token);
	MofancnResult queryAvailableSignin(MfClassroom mfClassroom, String token);

}
