package com.ziroh.chabby.audit.common;

/**
 * This class represents an audit server request
 */
public class AuditServerRequest
{
	/**
	 * User identifier
	 */
	public String UserID;

	/**
	 * Get userID
	 * @return String
	 */
	public String getUserID() {
		return UserID;
	}

	/**
	 * set UserID
	 * @param userID UserID is required
	 */
	public void setUserID(String userID) {
		UserID = userID;
	}
	
	
	
}
