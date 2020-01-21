package com.ziroh.chabby.common.keyTypes;

import java.io.Serializable;

/**
 * Base class for all types of keys
 */
public class Key implements Serializable
{
	/**
	 * The type of the key.
	 */
	public String KeyType;

	/**
	 * Get the type of the key.
	 * @return KeyType
	 */
	public String getKeyType() {
		return KeyType;
	}

	/**
	 * Set the type of the key.
	 * @param keyType KeyType is required
	 */
	public void setKeyType(String keyType) {
		KeyType = keyType;
	}
	
	/**
	 * The time (Jan 1, 1970) when the key is generated.
	 */
	 public long GeneratedOn;

	 /**
	  * Get the time when the key is generated
	  * @return The generated on
	  */
	public long getGeneratedOn() {
		return GeneratedOn;
	}

	/**
	 * Set the type of the key.
	 * @param generatedOn The time when the key is generated is required
	 */
	public void setGeneratedOn(long generatedOn) {
		GeneratedOn = generatedOn;
	}
	 
}
