package com.ziroh.chabby.keyStore.common;


/**
 * Represents a Key Record. All keys are stored encrypted.
 */
public class KeyRecord 
{
	/**
	 * KeyRecord constructor
	 */
	public KeyRecord()
	{
		GeneratedOn= System.currentTimeMillis();
        KeyStatus = "valid";
	}

	/**
	 * The unique ID
	 */
	public String Uniqueid;
	/**
	 * Get the uniqueId
	 * @return uniqueId
	 */
	public String getUniqueid() 
	{
		return Uniqueid;
	}

	/**
	 * Set the uniqueId
	 * @param uniqueid UniqueID is required
	 */
	public void setUniqueid(String uniqueid) 
	{
		Uniqueid = uniqueid;
	}

	/**
	 * The user identifier
	 */
	public String UserId;

	/**
	 * Get the user identifier
	 * @return userId
	 */
	public String getUserId() 
	{
		return UserId;
	}

	/**
	 * Set the user identifier
	 * @param userId UserID is required
	 */
	public void setUserId(String userId) 
	{
		UserId = userId;
	}

	/**
	 * The key
	 */
	 public byte[] ResourceKey;
	 
	 /**
	  * Get the key
	  * @return key in byteArray
	  */
	public byte[] getResourceKey() 
	{
		return ResourceKey;
	}

	/**
	 * Set the key
	 * @param key Key is required
	 */
	public void setResourceKey(byte[] key) 
	{
		ResourceKey = key;
	}
	 
	/**
	 * The key description.
	 */
	 public String KeyDescription;
	 
	 /**
	  * Get the key description
	  * @return KeyDescription
	  */
	public String getKeyDescription() {
		return KeyDescription;
	}

	/**
	 * Set the key description
	 * @param keyDescription KeyDescription is required
	 */
	public void setKeyDescription(String keyDescription) {
		KeyDescription = keyDescription;
	}
	 
	/**
	 * The generated on.
	 */
	 public long GeneratedOn;
	 
	 /**
	  * Get the generated on
	  * @return GeneratedOn
	  */
	public long getGeneratedOn() {
		return GeneratedOn;
	}

	/**
	 * Set the generatedOn
	 * @param generatedOn GeneratedOn is required
	 */
	public void setGeneratedOn(long generatedOn) {
		GeneratedOn = generatedOn;
	}
	 
	/**
	 * The key status.
	 */
	 public String KeyStatus;

	 /**
	 * Get the keyStatus
	 * @return keyStatus
	 */
	public String getKeyStatus() {
		return KeyStatus;
	}

	/**
	 * Set the key Status
	 * @param keyStatus KeyStatus is required
	 */
	public void setKeyStatus(String keyStatus) {
		KeyStatus = keyStatus;
	}
	
	 /**
	  *  The expiry date.
	  */
	public long ExpiryDate;
	
	/**
	 * Get the expiryDate
	 * @return ExpiryDate
	 */
	public long getExpiryDate() {
		return ExpiryDate;
	}

	/**
	 * Set the expiryDate
	 * @param expiryDate ExpiryDate is required
	 */
	public void setExpiryDate(long expiryDate) {
		ExpiryDate = expiryDate;
	}
	
	/**
	 * The Resource identifier
	 */
	 public String ResourceId;
	 
	 /**
	  * Get the resourceId
	  * @return ResourceId
	  */
	public String getResourceId() {
		return ResourceId;
	}

	/**
	 * Set the resourceId
	 * @param resourceId resource ID is required
	 */
	public void setResourceId(String resourceId) {
		ResourceId = resourceId;
	}
	 
	
	 public byte[] SymKeyIV;
	public byte[] getSymKeyIV() {
		return SymKeyIV;
	}

	public void setSymKeyIV(byte[] symKeyIV) {
		SymKeyIV = symKeyIV;
	}
	 
	 
	 
	 
	 
	
}
