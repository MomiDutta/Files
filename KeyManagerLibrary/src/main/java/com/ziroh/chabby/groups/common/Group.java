package com.ziroh.chabby.groups.common;

import com.ziroh.chabby.keyStore.common.KeyRecord;

/**
 * This class is used as Object that represents an group.
 */
public class Group
{
	/**
	 * The name of the Group.
	 */
    public String GroupId;

    /**
     * Get groupID
     * @return GroupID
     */
	public String getGroupId() {
		return GroupId;
	}

	/**
	 * Set groupID
	 * @param groupId GroupID is required
	 */
	public void setGroupId(String groupId) {
		GroupId = groupId;
	}
    
	/**
	 *  The created on.
	 */
    public long CreatedOn;

    /**
     * Get when the group is created
     * @return CreatedOn
     */
	public long getCreatedOn() {
		return CreatedOn;
	}

	/**
	 * Set when the group is created
	 * @param createdOn CreatedOn is required
	 */
	public void setCreatedOn(long createdOn) {
		CreatedOn = createdOn;
	}
    
	/**
	 * Users of the group
	 */
    public String[] Users;

    /**
     * Get the users of the group
     * @return Users
     */
	public String[] getUsers() {
		return Users;
	}

	/**
	 * Set the users of the group
	 * @param users Users of the group is required
	 */
	public void setUsers(String[] users) {
		Users = users;
	}
	
    /**
     * The key
     */
    public KeyRecord Key;

    /**
     * Get the key
     * @return KeyRecord object
     */
	public KeyRecord getKey() {
		return Key;
	}

	/**
	 * Set the key
	 * @param key  KeyRecord is required
	 */
	public void setKey(KeyRecord key) {
		Key = key;
	}
    
	/**
	 * The name of the owner
	 */
    public String OwnerName;

    /**
     * Get the name of the owner
     * @return Owner name
     */
	public String getOwnerName() {
		return OwnerName;
	}

	/**
	 * Set the name of the owner
	 * @param ownerName Owner Name is required
	 */
	public void setOwnerName(String ownerName) {
		OwnerName = ownerName;
	}

	/**
	 * The resource identifier
	 */
    public String ResourceId;

    /**
     * Get the resourceID
     * @return ResourceID
     */
	public String getResourceId() {
		return ResourceId;
	}

	/**
	 * Set the resourceID
	 * @param resourceId resourceID is required
	 */
	public void setResourceId(String resourceId) {
		ResourceId = resourceId;
	}
    
    public Group()
    {
//        CreatedOn = DateTime
    }
    
    /**
     * Creates the random identifier for the group.
     * @return Random identifier
     */
    public static String CreateRandomId()
    {
    	String id = new RandomNumberSecure().RandomNumberGeneratorString(2048);
    	return id;
    }

}
