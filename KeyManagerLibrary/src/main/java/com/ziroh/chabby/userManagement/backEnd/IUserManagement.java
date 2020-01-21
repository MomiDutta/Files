package com.ziroh.chabby.userManagement.backEnd;

import com.ziroh.chabby.operationalResults.Result;
import com.ziroh.chabby.userManagement.common.AuthResult;
import com.ziroh.chabby.userManagement.common.UserData;

interface IUserManagement 
{
	Result StoreUserData(UserData UserInfo);
	 Result UpdateUser(UserData UserInfo);
	 UserData GetUser(String userid);
	 Result DeleteUser(String userid);
	 boolean CheckIfUserExist(String userid);
	 byte[] GetUserSalt(String userid);
	 AuthResult AuthenticateUser(String userId, byte[] digest);
}
