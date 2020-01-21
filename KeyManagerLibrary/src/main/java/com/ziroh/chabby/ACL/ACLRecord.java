package com.ziroh.chabby.ACL;

/**
 * This class represents an ACL record
 */
public class ACLRecord
{
	/**
	 * Resource identifier
	 */
	public String ResourceId;

	/**
	 * Get resource identifier
	 * @return String
	 */
	public String getResourceId() {
		return ResourceId;
	}

	/**
	 * Set resource identifier
	 * @param resourceId Resource identifier
	 */
	public void setResourceId(String resourceId) {
		this.ResourceId = resourceId;
	}
	
	/**
	 * The User identifier Collection
	 */
	public String[] UserIdCollection ;

	/**
	 * Get UserID collection
	 * @return UserID collections
	 */
	public String[] getUserIdCollection() {
		return UserIdCollection;
	}

	/**
	 * Set the UserID collection
	 * @param userIdCollection UserIDCollection is required
	 */
	public void setUserIdCollection(String[] userIdCollection) 
	{
		this.UserIdCollection = userIdCollection;
	}
	
}
