package com.ziroh.chabby.userManagement.common;

/**
 * User login credentials
 */
public class UserCredentials
{
	/**
	 * User name
	 */
	public String UserName;

	/**
	 * Get username
	 * @return UserName
	 */
	public String getUserName() {
		return UserName;
	}

	/**
	 * Set user name
	 * @param userName UserName is required
	 */
	public void setUserName(String userName) {
		UserName = userName;
	}
	
	/**
	 * User's password
	 */
	public String Password;

	/**
	 * Get password
	 * @return Password
	 */
	public String getPassword() {
		return Password;
	}

	/**
	 * Set password
	 * @param password Password is required
	 */
	public void setPassword(String password) {
		Password = password;
	}
	
}
