package com.ziroh.chabby.userManagement.common;

import com.ziroh.chabby.operationalResults.Result;

/**
 * AuthResult class
 */
public class AuthResult extends Result
{
	/**
	 * The token
	 */
	public String Token;

	/**
	 * Get the token
	 * @return Token
	 */
	public String getToken() 
	{
		return Token;
	}

	/**
	 * Set the token
	 * @param token Token is required
	 */
	public void setToken(String token) {
		Token = token;
	}
	
	
	
}
