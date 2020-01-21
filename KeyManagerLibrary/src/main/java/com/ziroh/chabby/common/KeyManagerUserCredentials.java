package com.ziroh.chabby.common;

/**
 * This class represents User login credentials
 */
public class KeyManagerUserCredentials 
{
	/**
	 * The userName
	 */
	public String UserName;

	/**
	 * Get the userName
	 * @return userName
	 */
	public String getUserName() {
		return UserName;
	}

	/**
	 * Set userName
	 * @param userName UserName is required
	 */
	public void setUserName(String userName) {
		UserName = userName;
	}
	
	/**
	 * The password
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
