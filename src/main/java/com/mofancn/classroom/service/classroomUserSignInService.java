package com.mofancn.classroom.service;

import com.mofancn.common.utils.MofancnResult;
import com.mofancn.pojo.MfClassroomSignIn;

public interface classroomUserSignInService {
	
	MofancnResult createClassroomSignInByStudent(MfClassroomSignIn mfClassroomSignIn,String token);

}
