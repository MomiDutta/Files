package com.ziroh.chabby.common;

/**
 * This class represent a resource along with the key
 */
public class ResourceKey 
{
	/**
	 * The key
	 */
	public byte[] Key;

	/**
	 * Get the key
	 * @return The key
	 */
	public byte[] getKey() {
		return Key;
	}

	/**
	 * Set the key
	 * @param key Key is required
	 */
	public void setKey(byte[] key) {
		Key = key;
	}
	
	/**
	 * Resource identifier
	 */
	 public String ResourceId;

	 /**
	  * Get ResourceID
	  * @return ResourceID
	  */
	public String getResourceId() {
		return ResourceId;
	}

	/**
	 * Set ResourceID
	 * @param resourceId ResourceID is required
	 */
	public void setResourceId(String resourceId) {
		ResourceId = resourceId;
	}
	 
}
