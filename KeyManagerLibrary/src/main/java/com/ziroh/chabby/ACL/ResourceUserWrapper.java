package com.ziroh.chabby.ACL;

/**
 * This class represents the map between resource and users
 */
public class ResourceUserWrapper 
{
	/**
	 * Resource identifier
	 */
	public String ResourceId;

	/**
	 * Get the resource identifier
	 * @return String
	 */
	public String getResourceId() {
		return ResourceId;
	}

	/**
	 * Set the resource identifier
	 * @param resourceId Resource identifier
	 */
	public void setResourceId(String resourceId) {
		ResourceId = resourceId;
	}
	
	/**
	 * The users
	 */
	public String[] Users;

	/**
	 * Get the users
	 * @return String array
	 */
	public String[] getUsers() {
		return Users;
	}

	/**
	 * Set the users
	 * @param users Array of users
	 */
	public void setUsers(String[] users) {
		Users = users;
	}
	
}
